package com.example.electionmanagement.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electionmanagement.Activity.Admin.AddCandidateActivity;
import com.example.electionmanagement.Database.DatabaseHelper;
import com.example.electionmanagement.Model.Candidate;
import com.example.electionmanagement.Model.Election;
import com.example.electionmanagement.R;

import java.util.List;

public class CandidateListActivity extends AppCompatActivity {

    RecyclerView rv;
    DatabaseHelper db;
    long electionId = -1; // optional: show all if -1
    List<Candidate> candidates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new DatabaseHelper(this);
        rv = findViewById(R.id.rvCandidates);
        findViewById(R.id.btnAddCandidate).setOnClickListener(v -> startActivity(new android.content.Intent(this, AddCandidateActivity.class)));
        electionId = getIntent().getLongExtra("election_id", -1);
    }

    private void load() {
        if (electionId == -1) {
            // show all candidates for all elections
            // flatten all elections' candidates
            candidates = new java.util.ArrayList<>();
            for (Election e : db.getAllElections())
                candidates.addAll(db.getCandidatesByElection(e.id));
        } else {
            candidates = db.getCandidatesByElection(electionId);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CAAdapter(candidates));
    }

    private class CAAdapter extends RecyclerView.Adapter<CAAdapter.H> {
        List<Candidate> list;

        CAAdapter(List<Candidate> l) {
            list = l;
        }

        class H extends RecyclerView.ViewHolder {
            TextView tvName, tvParty;

            H(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvName);
                tvParty = v.findViewById(R.id.tvParty);
            }
        }

        @Override
        public H onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidate, parent, false);
            return new H(v);
        }

        @Override
        public void onBindViewHolder(H holder, int pos) {
            Candidate c = list.get(pos);
            holder.tvName.setText(c.name);
            holder.tvParty.setText(c.party + "  (" + getElectionTitle(c.electionId) + ")");
            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(CandidateListActivity.this).setTitle("Candidate Options").setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        Toast.makeText(CandidateListActivity.this, "Edit - implement editing UI", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean ok = db.deleteCandidate(c.id);
                        if (ok) {
                            Toast.makeText(CandidateListActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            load();
                        } else
                            Toast.makeText(CandidateListActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                }).show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private String getElectionTitle(int electionId) {
        Election e = db.getElectionById(electionId);
        return e == null ? "Unknown" : e.title;
    }
}