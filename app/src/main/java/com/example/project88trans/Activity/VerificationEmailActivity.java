package com.example.project88trans.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project88trans.R;

public class VerificationEmailActivity extends AppCompatActivity {

    EditText etCode1, etCode2, etCode3, etCode4;
    Button btnVerify;
    TextView tvResend, tvEmail, tvBackToForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_email);

        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);
        tvEmail = findViewById(R.id.tvEmail);
        tvBackToForgot = findViewById(R.id.tvBackToForgot); // ← ini yang baru

        // ambil email dari ForgotPasswordActivity
        String email = getIntent().getStringExtra("email");
        if (email != null) {
            tvEmail.setText(email);
        }

        btnVerify.setOnClickListener(v -> {
            String code = etCode1.getText().toString() +
                    etCode2.getText().toString() +
                    etCode3.getText().toString() +
                    etCode4.getText().toString();

            if (code.length() == 4) {
                Toast.makeText(this, "Code entered: " + code, Toast.LENGTH_SHORT).show();
                // TODO: tambahkan logic verifikasi ke server di sini
            } else {
                Toast.makeText(this, "Enter 4 digit code", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> {
            Toast.makeText(this, "Resend code clicked", Toast.LENGTH_SHORT).show();
        });

        // 🔹 Wrong email? Go back
        tvBackToForgot.setOnClickListener(v -> {
            Intent intent = new Intent(VerificationEmailActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish(); // tutup activity verifikasi
        });
    }
}
