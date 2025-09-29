package com.example.electionmanagement.Activity.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.electionmanagement.Activity.CandidateListActivity;
import com.example.electionmanagement.Activity.ElectionListActivity;
import com.example.electionmanagement.Activity.LoginActivity;
import com.example.electionmanagement.R;

public class MainActivity extends AppCompatActivity {

    // Declare Buttons
    Button btnManageElections, btnManageCandidates, btnAlogout;
    SharedPreferences AsharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        AsharedPreferences = getSharedPreferences("Admin.txt", MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnManageElections  = findViewById(R.id.btnManageElections);
        btnManageCandidates = findViewById(R.id.btnManageCandidates);
        btnAlogout          = findViewById(R.id.btnLogout);

        // Set click listeners
        btnManageElections.setOnClickListener(v ->
                startActivity(new Intent(this, ElectionListActivity.class)));

        btnManageCandidates.setOnClickListener(v ->
                startActivity(new Intent(this, CandidateListActivity.class)));

        btnAlogout.setOnClickListener(view -> {
            SharedPreferences.Editor ed = AsharedPreferences.edit();
            ed.clear();
            ed.apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
