package com.example.electionmanagement.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.electionmanagement.Activity.Admin.MainActivity;
import com.example.electionmanagement.Activity.User.ProfileActivity;
import com.example.electionmanagement.Activity.User.RegisterActivity;
import com.example.electionmanagement.Database.DatabaseHelper;
import com.example.electionmanagement.Model.Voter;
import com.example.electionmanagement.R;

public class LoginActivity extends AppCompatActivity {

    EditText etName, etPass;
    Button btnLogin, btnReg;
    DatabaseHelper db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLoginU);
        btnReg = findViewById(R.id.btnRegisterU);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        // Check if already logged in
        boolean isLoggedIn = sharedPreferences.getBoolean("Login", false);
        if (isLoggedIn) {
            int voterId = sharedPreferences.getInt("voter_id", -1);
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("voter_id", voterId);
            startActivity(i);
            finish();
            return; // stop further execution
        }

        // Login button click
        btnLogin.setOnClickListener(v -> {
            String email = etName.getText().toString().trim();
            String password = etPass.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Admin login
            if (email.equals("Admin") && password.equals("Admin")) {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
                return;
            }

            // Voter login
            Voter voter = db.loginVoter(email, password);
            if (voter != null) {
                // Save login info
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Login", true);
                editor.putInt("voter_id", voter.id);
                editor.putString("voter_name", voter.firstName + " " + voter.lastName);
                editor.apply();

                Intent i = new Intent(this, ProfileActivity.class);
                i.putExtra("voter_id", voter.id);
                i.putExtra("voter_name", voter.firstName + " " + voter.lastName);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        // Register button click
        btnReg.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
