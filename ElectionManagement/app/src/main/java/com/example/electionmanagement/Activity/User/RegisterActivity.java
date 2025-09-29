package com.example.electionmanagement.Activity.User;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.example.electionmanagement.R;

public class RegisterActivity extends AppCompatActivity {
    EditText etFirstName, etLastName, etAddress, etVoterCard, etEmail, etPhone, etPassword;
    Button btnRegister;
    DatabaseHelper db;
    Spinner spState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etAddress = findViewById(R.id.etAddress);
        etVoterCard = findViewById(R.id.etVoterCard);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        spState = findViewById(R.id.spState);

        btnRegister.setOnClickListener(v -> registerUser());
        String[] states = new String[]{"Gujrat"};
        db=new DatabaseHelper(this);
        spState.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states));
    }
    private void registerUser() {
        String fname = etFirstName.getText().toString().trim();
        String lname = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String voterCard = etVoterCard.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String state = spState.getSelectedItem().toString();

        // Validation
        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(voterCard) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid Email");
            return;
        }

        if (phone.length() != 10) {
            etPhone.setError("Phone must be 10 digits");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        // Insert into database
        try {
            long id = db.registerVoter(fname, lname, address, voterCard, email, phone, password,state);
            if (id > 0) {
                Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                finish(); // back to login page
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (RuntimeException e) {
           Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }

    }
}