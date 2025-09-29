package com.example.electionmanagement.Activity;

import android.content.Intent;
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

import com.example.electionmanagement.Activity.Admin.AddElectionActivity;
import com.example.electionmanagement.Database.DatabaseHelper;
import com.example.electionmanagement.Model.Election;
import com.example.electionmanagement.R;
import com.example.electionmanagement.Utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ElectionListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private DatabaseHelper db;
    private List<Election> elections;
    private ElectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable EdgeToEdge for modern UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_election_list);

        // Safe area / Insets handling
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize Database
        db = new DatabaseHelper(this);

        // Initialize RecyclerView
        rv = findViewById(R.id.rvElections);
        if (rv == null) {
            Toast.makeText(this, "RecyclerView not found in layout!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add Election Button
        View btnAdd = findViewById(R.id.btnAddElection);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddElectionActivity.class)));
        }

        loadElections();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadElections(); // Refresh list when returning
    }

    // Load elections from database
    private void loadElections() {
        if (db != null) {
            elections = db.getAllElections();
        }
        if (elections == null) elections = new ArrayList<>();

        adapter = new ElectionAdapter(elections);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    // ---------------- Election Adapter ----------------
    private class ElectionAdapter extends RecyclerView.Adapter<ElectionAdapter.ElectionViewHolder> {
        private final List<Election> list;

        ElectionAdapter(List<Election> list) {
            this.list = list != null ? list : new ArrayList<>();
        }

        class ElectionViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvState, tvTime, tvStatus;

            ElectionViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvTitle);
                tvState = v.findViewById(R.id.tvState);
                tvTime = v.findViewById(R.id.tvTime);
                tvStatus = v.findViewById(R.id.tvStatus);
            }
        }

        @Override
        public ElectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_election, parent, false);
            return new ElectionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ElectionViewHolder holder, int position) {
            Election e = list.get(position);

            // Safe text assignment
            holder.tvTitle.setText(e.title != null ? e.title : "No Title");
            holder.tvState.setText(e.state != null ? e.state : "Unknown");
            holder.tvTime.setText((e.startIso != null ? e.startIso : "?")
                    + " → " + (e.endIso != null ? e.endIso : "?"));

            // Determine status safely
            holder.tvStatus.setText(DateTimeUtils.getStatus(e.startIso, e.endIso));

            try {
                if (e.startIso != null && e.endIso != null) {
//                    if (DateTimeUtils.isNowBetween(e.startIso, e.endIso)) {
//                        status = "Ongoing";
//                    } else if (DateTimeUtils.isEnded(e.endIso)) {
//                        status = "Ended";
//                    } else {
//                        status = "Upcoming";
//                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //status = "Invalid Date";
            }
            //holder.tvStatus.setText(status);

            // Click → Candidate list
            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(ElectionListActivity.this, CandidateListActivity.class);
                i.putExtra("election_id", e.id);
                startActivity(i);
            });

            // Long click → Edit/Delete options
            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(ElectionListActivity.this)
                        .setTitle("Election Options")
                        .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                            if (which == 0) {
                                Toast.makeText(ElectionListActivity.this,
                                        "Edit not implemented yet", Toast.LENGTH_SHORT).show();
                            } else {
                                boolean ok = db != null && db.deleteElection(e.id);
                                if (ok) {
                                    Toast.makeText(ElectionListActivity.this,
                                            "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    loadElections();
                                } else {
                                    Toast.makeText(ElectionListActivity.this,
                                            "Delete failed", Toast.LENGTH_SHORT).show();
                                }
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
}
