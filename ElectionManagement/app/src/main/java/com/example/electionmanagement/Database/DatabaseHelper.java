package com.example.electionmanagement.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.electionmanagement.Model.Candidate;
import com.example.electionmanagement.Model.Election;
import com.example.electionmanagement.Model.ResultItem;
import com.example.electionmanagement.Model.Voter;
import com.example.electionmanagement.Utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DB";
    private static final String DB_NAME = "election_app.db";
    private static final int DB_VERSION = 2;

    // Tables
    private static final String T_ELECTION = "Elections";
    private static final String T_CANDIDATE = "Candidates";
    private static final String T_VOTER = "Voters";
    private static final String T_VOTE = "Votes";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // Enable FK support
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Elections
        db.execSQL("CREATE TABLE " + T_ELECTION + "("
                + "election_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "state TEXT NOT NULL,"
                + "start_time TEXT NOT NULL," // ISO: yyyy-MM-dd HH:mm
                + "end_time TEXT NOT NULL)");

        // Candidates
        db.execSQL("CREATE TABLE " + T_CANDIDATE + "("
                + "candidate_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "party TEXT,"
                + "symbol TEXT,"
                + "election_id INTEGER NOT NULL,"
                + "FOREIGN KEY(election_id) REFERENCES " + T_ELECTION + "(election_id) ON DELETE CASCADE)");

        // Voters (full details)
        db.execSQL("CREATE TABLE " + T_VOTER + "("
                + "voter_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "first_name TEXT NOT NULL,"
                + "last_name TEXT NOT NULL,"
                + "address TEXT NOT NULL,"
                + "voter_card TEXT UNIQUE NOT NULL,"
                + "email TEXT UNIQUE NOT NULL,"
                + "phone TEXT NOT NULL,"
                + "password TEXT NOT NULL,"
                + "state TEXT NOT NULL)");

        // Votes (1 per election per voter)
        db.execSQL("CREATE TABLE " + T_VOTE + "("
                + "vote_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "voter_id INTEGER NOT NULL,"
                + "candidate_id INTEGER NOT NULL,"
                + "election_id INTEGER NOT NULL,"
                + "UNIQUE(election_id, voter_id),"
                + "FOREIGN KEY(voter_id) REFERENCES " + T_VOTER + "(voter_id) ON DELETE CASCADE,"
                + "FOREIGN KEY(candidate_id) REFERENCES " + T_CANDIDATE + "(candidate_id) ON DELETE CASCADE,"
                + "FOREIGN KEY(election_id) REFERENCES " + T_ELECTION + "(election_id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading DB - dropping all tables");
        db.execSQL("DROP TABLE IF EXISTS " + T_VOTE);
        db.execSQL("DROP TABLE IF EXISTS " + T_CANDIDATE);
        db.execSQL("DROP TABLE IF EXISTS " + T_VOTER);
        db.execSQL("DROP TABLE IF EXISTS " + T_ELECTION);
        onCreate(db);
    }

    // ---------- Election ----------
    public long addElection(String title, String startIso, String endIso, String state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("startIso", startIso);
        values.put("endIso", endIso);
        values.put("state", state);
        return db.insert(T_ELECTION, null, values);
    }


    public boolean updateElection(long electionId, String title, String state, String startIso, String endIso) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("state", state);
        cv.put("start_time", startIso);
        cv.put("end_time", endIso);
        int rows = db.update(T_ELECTION, cv, "election_id=?", new String[]{String.valueOf(electionId)});
        return rows > 0;
    }

    public boolean deleteElection(long electionId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(T_ELECTION, "election_id=?", new String[]{String.valueOf(electionId)});
        return rows > 0;
    }

    public List<Election> getAllElections() {
        List<Election> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_ELECTION + " ORDER BY start_time DESC", null);
        while (c.moveToNext()) {
            list.add(new Election(
                    c.getInt(c.getColumnIndexOrThrow("election_id")),
                    c.getString(c.getColumnIndexOrThrow("title")),
                    c.getString(c.getColumnIndexOrThrow("state")),
                    c.getString(c.getColumnIndexOrThrow("start_time")),
                    c.getString(c.getColumnIndexOrThrow("end_time"))
            ));
        }
        c.close();
        return list;
    }

    // ---------- Candidate ----------
    public long addCandidate(String name, String party, String symbol, long electionId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("party", party);
        cv.put("symbol", symbol);
        cv.put("election_id", electionId);
        return db.insert(T_CANDIDATE, null, cv);
    }

    public List<Candidate> getCandidatesByElection(long electionId) {
        List<Candidate> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_CANDIDATE + " WHERE election_id=?",
                new String[]{String.valueOf(electionId)});
        while (c.moveToNext()) {
            list.add(new Candidate(
                    c.getInt(c.getColumnIndexOrThrow("candidate_id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("party")),
                    c.getString(c.getColumnIndexOrThrow("symbol")),
                    c.getInt(c.getColumnIndexOrThrow("election_id"))
            ));
        }
        c.close();
        return list;
    }

    // ---------- Voter ----------
    public long registerVoter(String first, String last, String address, String voterCard,
                              String email, String phone, String password, String state) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("first_name", first);
        cv.put("last_name", last);
        cv.put("address", address);
        cv.put("voter_card", voterCard);
        cv.put("email", email);
        cv.put("phone", phone);
        cv.put("password", password);
        cv.put("state", state);
        return db.insert(T_VOTER, null, cv);
    }

    public Voter loginVoter(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_VOTER + " WHERE email=? AND password=?",
                new String[]{email, password});
        if (c.moveToFirst()) {
            Voter v = new Voter(
                    c.getInt(c.getColumnIndexOrThrow("voter_id")),
                    c.getString(c.getColumnIndexOrThrow("first_name")),
                    c.getString(c.getColumnIndexOrThrow("last_name")),
                    c.getString(c.getColumnIndexOrThrow("address")),
                    c.getString(c.getColumnIndexOrThrow("voter_card")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("phone")),
                    c.getString(c.getColumnIndexOrThrow("password")),
                    c.getString(c.getColumnIndexOrThrow("state"))
            );
            c.close();
            return v;
        }
        c.close();
        return null;
    }

    // ---------- Vote ----------
    public boolean hasVoted(long voterId, long electionId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + T_VOTE +
                        " WHERE voter_id=? AND election_id=? LIMIT 1",
                new String[]{String.valueOf(voterId), String.valueOf(electionId)});
        boolean res = c.moveToFirst();
        c.close();
        return res;
    }

    public long castVote(long voterId, long candidateId, long electionId) {
        if (hasVoted(voterId, electionId)) return -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("voter_id", voterId);
        cv.put("candidate_id", candidateId);
        cv.put("election_id", electionId);
        try {
            return db.insertOrThrow(T_VOTE, null, cv);
        } catch (Exception ex) {
            return -1;
        }
    }

    // ---------- Results ----------
    public List<ResultItem> getResultsForElection(long electionId) {
        List<ResultItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT c.name, c.party, COUNT(v.vote_id) as votes " +
                "FROM " + T_CANDIDATE + " c " +
                "LEFT JOIN " + T_VOTE + " v ON c.candidate_id=v.candidate_id " +
                "WHERE c.election_id=? " +
                "GROUP BY c.candidate_id ORDER BY votes DESC";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(electionId)});
        while (c.moveToNext()) {
            list.add(new ResultItem(
                    c.getString(0),
                    c.getString(1),
                    c.getInt(2)
            ));
        }
        c.close();
        return list;
    }
    public List<Election> getElectionsByState(String state) {
        List<Election> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Query all elections for the given state
        Cursor c = db.rawQuery(
                "SELECT * FROM " + T_ELECTION + " WHERE state=? ORDER BY start_time DESC",
                new String[]{state}
        );

        long now = System.currentTimeMillis();

        while (c.moveToNext()) {
            String startIso = c.getString(c.getColumnIndexOrThrow("start_time"));
            String endIso = c.getString(c.getColumnIndexOrThrow("end_time"));

            // Include only elections that haven't ended yet
            if (!DateTimeUtils.isEnded(endIso)) {
                Election e = new Election(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("title")),
                        c.getString(c.getColumnIndexOrThrow("state")),
                        startIso,
                        endIso
                );
                list.add(e);
            }
        }
        c.close();
        return list;
    }
    public Election getElectionById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM " + T_ELECTION + " WHERE id=?",
                new String[]{String.valueOf(id)}
        );

        Election election = null;
        if (c.moveToFirst()) {
            election = new Election(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("title")),
                    c.getString(c.getColumnIndexOrThrow("state")),
                    c.getString(c.getColumnIndexOrThrow("start_time")),
                    c.getString(c.getColumnIndexOrThrow("end_time"))
            );
        }
        c.close();
        return election;
    }
    // Delete Candidate by ID
    public boolean deleteCandidate(int candidateId) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete all votes related to this candidate
        db.delete("Votes", "candidate_id=?", new String[]{String.valueOf(candidateId)});

        // Delete the candidate itself
        int rows = db.delete("Candidates", "id=?", new String[]{String.valueOf(candidateId)});

        return rows > 0; // returns true if deletion was successful
    }
    public Voter getVoterById(int voterId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_VOTER + " WHERE voter_id=?", new String[]{String.valueOf(voterId)});
        if (c.moveToFirst()) {
            Voter v = new Voter(
                    c.getInt(c.getColumnIndexOrThrow("voter_id")),
                    c.getString(c.getColumnIndexOrThrow("first_name")),
                    c.getString(c.getColumnIndexOrThrow("last_name")),
                    c.getString(c.getColumnIndexOrThrow("address")),
                    c.getString(c.getColumnIndexOrThrow("voter_card")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("phone")),
                    c.getString(c.getColumnIndexOrThrow("password")),
                    c.getString(c.getColumnIndexOrThrow("state"))
            );
            c.close();
            return v;
        }
        c.close();
        return null;
    }
}
