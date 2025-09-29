package com.example.electionmanagement.Activity.User;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.electionmanagement.Database.DatabaseHelper;
import com.example.electionmanagement.Model.Election;
import com.example.electionmanagement.Model.ResultItem;
import com.example.electionmanagement.R;
import com.example.electionmanagement.Utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    Spinner spElections;
    ListView lvResults;
    DatabaseHelper db;
    List<Election> elections;
    String userState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);
        spElections = findViewById(R.id.spElectionsRes);
        lvResults = findViewById(R.id.lvResults);

        // Get user state from Intent
        userState = getIntent().getStringExtra("voter_state");

        loadElections();

        spElections.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (elections.isEmpty()) return; // no elections to select

                Election e = elections.get(position);
                if (!DateTimeUtils.isEnded(e.endIso)) {
                    Toast.makeText(ResultActivity.this, "Results will be declared after " + e.endIso, Toast.LENGTH_SHORT).show();
                    lvResults.setAdapter(new ArrayAdapter<>(ResultActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
                    return;
                }

                List<ResultItem> results = db.getResultsForElection(e.id);
                List<String> lines = new ArrayList<>();
                for (ResultItem r : results)
                    lines.add(r.name + " (" + r.party + ") - Votes: " + r.votes);
                lvResults.setAdapter(new ArrayAdapter<>(ResultActivity.this, android.R.layout.simple_list_item_1, lines));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadElections() {
        elections = db.getElectionsByState(userState);

        if (elections.isEmpty()) {
            // Show "No elections" message in Spinner
            List<String> emptyList = new ArrayList<>();
            emptyList.add("No elections available for your state");
            spElections.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, emptyList));

            // Clear ListView
            lvResults.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
            Toast.makeText(this, "No elections available for your state", Toast.LENGTH_LONG).show();
        } else {
            List<String> items = new ArrayList<>();
            for (Election e : elections) items.add(e.title + " - " + e.state);
            spElections.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
        }
    }
}
