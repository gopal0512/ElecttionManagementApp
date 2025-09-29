package com.example.electionmanagement.Activity.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.electionmanagement.Activity.LoginActivity;
import com.example.electionmanagement.Database.DatabaseHelper;
import com.example.electionmanagement.Model.Voter;
import com.example.electionmanagement.R;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvVoterCard, tvState, tvAddress;
    Button btnLogout, btnVote, btnResult;
    DatabaseHelper db;
    int voterId;
    SharedPreferences userShared;
    Voter voter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Handle system bar insets safely
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvVoterCard = findViewById(R.id.tvVoterCard);
        tvState = findViewById(R.id.tvState);
        tvAddress = findViewById(R.id.tvAddress);
        btnLogout = findViewById(R.id.btnLogout);
        btnVote = findViewById(R.id.btnVote);
        btnResult = findViewById(R.id.btnResult);

        db = new DatabaseHelper(this);
        userShared = getSharedPreferences("UserData", MODE_PRIVATE);

        // Get voterId from intent
        voterId = getIntent().getIntExtra("voter_id", -1);
        if (voterId == -1) {
            Toast.makeText(this, "Invalid voter. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Load voter profile
        loadVoterProfile();

        // Logout button
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = userShared.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        // Vote button
        btnVote.setOnClickListener(v -> {
            if (voter != null) {
                Intent i = new Intent(ProfileActivity.this, VoteActivity.class);
                i.putExtra("voter_id", voter.id);
                i.putExtra("voter_state", voter.state);
                startActivity(i);
            } else {
                Toast.makeText(this, "Voter data not available.", Toast.LENGTH_SHORT).show();
            }
        });

        // Result button
        btnResult.setOnClickListener(v -> {
            if (voter != null) {
                Intent i = new Intent(ProfileActivity.this, ResultActivity.class);
                i.putExtra("voter_id", voter.id);
                i.putExtra("voter_state", voter.state); // use actual state dynamically
                startActivity(i);
            } else {
                Toast.makeText(this, "Voter data not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVoterProfile() {
        voter = db.getVoterById(voterId);
        if (voter != null) {
            tvName.setText(voter.getFirstName() + " " + voter.getLastName());
            tvEmail.setText(voter.getEmail());
            tvPhone.setText(voter.getPhone());
            tvVoterCard.setText(voter.getVoterCard());
            tvState.setText(voter.getState());
            tvAddress.setText(voter.getAddress());
        } else {
            Toast.makeText(this, "Voter not found.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }
    }
}
