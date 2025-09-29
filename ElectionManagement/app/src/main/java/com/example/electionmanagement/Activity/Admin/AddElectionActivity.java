package com.example.electionmanagement.Activity.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.electionmanagement.Database.DatabaseHelper;
import com.example.electionmanagement.R;
import com.example.electionmanagement.Utils.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;

public class AddElectionActivity extends AppCompatActivity {

    EditText etTitle;
    Spinner spState;
    TextView tvStart, tvEnd;
    Button btnPickStart, btnPickEnd, btnSave;
    DatabaseHelper db;
    String startIso = "", endIso = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_election);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new DatabaseHelper(this);
        etTitle = findViewById(R.id.etTitle);
        spState = findViewById(R.id.spState);
        tvStart = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        btnPickStart = findViewById(R.id.btnPickStart);
        btnPickEnd = findViewById(R.id.btnPickEnd);
        btnSave = findViewById(R.id.btnSaveElection);

        String[] states = new String[]{"Gujrat"};
        spState.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states));

        btnPickStart.setOnClickListener(v -> pickDateTime((iso) -> { startIso = iso; tvStart.setText(iso); }));
        btnPickEnd.setOnClickListener(v -> pickDateTime((iso) -> { endIso = iso; tvEnd.setText(iso); }));

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String state = spState.getSelectedItem().toString();
            if (title.isEmpty() || startIso.isEmpty() || endIso.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = db.addElection(title, startIso, endIso,state);
            if (id != -1) {
                Toast.makeText(this, "Election added", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error adding", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private interface IsoCallback { void onIso(String iso); }

    private void pickDateTime(IsoCallback callback) {
        final Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                Date d = cal.getTime();
                String iso = DateTimeUtils.format(d);
                callback.onIso(iso);
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}