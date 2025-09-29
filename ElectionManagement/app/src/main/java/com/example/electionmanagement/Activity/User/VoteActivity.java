package com.example.electionmanagement.Activity.User;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.electionmanagement.Database.DatabaseHelper;
import com.example.electionmanagement.Model.Candidate;
import com.example.electionmanagement.Model.Election;
import com.example.electionmanagement.R;
import com.example.electionmanagement.Utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class VoteActivity extends AppCompatActivity {

    Spinner spElections;
    ListView lvCandidates;
    Button btnCast;
    DatabaseHelper db;
    int voterId;
    String voterState;
    List<Election> elections;
    List<Candidate> candidates;
    int selectedCandidateId = -1;
    long selectedElectionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vote);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);
        voterId = getIntent().getIntExtra("voter_id", -1);
        voterState = getIntent().getStringExtra("voter_state");

        spElections = findViewById(R.id.spElections);
        lvCandidates = findViewById(R.id.lvCandidates);
        btnCast = findViewById(R.id.btnCast);

        loadElections();

        spElections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (elections.isEmpty()) return;

                Election e = elections.get(position);
                selectedElectionId = e.id;

                // Clear candidate selection
                selectedCandidateId = -1;
                lvCandidates.clearChoices();
                lvCandidates.setAdapter(null);

                // Check if election is active
                if (!DateTimeUtils.isNowBetween(e.startIso, e.endIso)) {
                    lvCandidates.setEnabled(false);
                    btnCast.setEnabled(false);
                    String msg = DateTimeUtils.isEnded(e.endIso) ? "Election ended. You cannot vote." : "Election not started yet.";
                    Toast.makeText(VoteActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    lvCandidates.setEnabled(true);
                    btnCast.setEnabled(true);
                    loadCandidates(e.id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        lvCandidates.setOnItemClickListener((parent, view, position, id) -> {
            Candidate c = candidates.get(position);
            selectedCandidateId = c.id;
        });

        btnCast.setOnClickListener(v -> {
            if (selectedElectionId == -1 || selectedCandidateId == -1) {
                Toast.makeText(this, "Select election & candidate", Toast.LENGTH_SHORT).show();
                return;
            }

            Election e = db.getElectionById(selectedElectionId);
            if (!DateTimeUtils.isNowBetween(e.startIso, e.endIso)) {
                Toast.makeText(this, "Election is not active", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.hasVoted(voterId, selectedElectionId)) {
                Toast.makeText(this, "You already voted in this election", Toast.LENGTH_SHORT).show();
                return;
            }

            long res = db.castVote(voterId, selectedCandidateId, selectedElectionId);
            if (res != -1) {
                new AlertDialog.Builder(this)
                        .setTitle("Success")
                        .setMessage("Vote recorded.")
                        .setPositiveButton("OK", (d, i) -> finish())
                        .show();
            } else {
                Toast.makeText(this, "Vote failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadElections() {
        elections = db.getElectionsByState("Gujrat");
        List<String> items = new ArrayList<>();
        if (elections.isEmpty()) {
            items.add("No elections available for your state");
            selectedElectionId = -1;
            lvCandidates.setAdapter(null); // clear candidates
            btnCast.setEnabled(false);
            Toast.makeText(this, "No elections available for your state", Toast.LENGTH_LONG).show();
        } else {
            for (Election e : elections)
                items.add(e.title + " (" + e.startIso + " → " + e.endIso + ")");
            btnCast.setEnabled(true);
        }
        spElections.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void loadCandidates(long electionId) {
        candidates = db.getCandidatesByElection(electionId);
        List<String> items = new ArrayList<>();
        for (Candidate c : candidates)
            items.add(c.name + " - " + c.party);
        lvCandidates.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, items));
        lvCandidates.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
}
