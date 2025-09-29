package com.example.electionmanagement.Activity.Admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.electionmanagement.Database.DatabaseHelper;
import com.example.electionmanagement.Model.Election;
import com.example.electionmanagement.R;

import java.util.List;

public class AddCandidateActivity extends AppCompatActivity {
    EditText etName, etParty, etSymbol;
    Spinner spElection;
    Button btnSave;
    DatabaseHelper db;
    List<Election> elections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_candidate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etParty = findViewById(R.id.etParty);
        etSymbol = findViewById(R.id.etSymbol);
        spElection = findViewById(R.id.spElection);
        btnSave = findViewById(R.id.btnSaveCandidate);
        loadElections();
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String party = etParty.getText().toString().trim();
            String symbol = etSymbol.getText().toString().trim();
            int pos = spElection.getSelectedItemPosition();
            if (name.isEmpty() || pos < 0) { Toast.makeText(this, "Fill fields", Toast.LENGTH_SHORT).show(); return; }
            long electionId = elections.get(pos).id;
            long id = db.addCandidate(name, party, symbol, electionId);
            if (id != -1) { Toast.makeText(this, "Candidate added", Toast.LENGTH_SHORT).show(); finish(); }
            else Toast.makeText(this, "Error adding candidate", Toast.LENGTH_SHORT).show();
        });
    }
    private void loadElections() {
        elections = db.getAllElections();
        String[] items = new String[elections.size()];
        for (int i=0;i<elections.size();i++) items[i] = elections.get(i).title + " (" + elections.get(i).state + ")";
        spElection.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
    }
}