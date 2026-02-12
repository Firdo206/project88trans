package com.example.project88trans.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project88trans.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnContinue;
    TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Tombol Continue
        btnContinue.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                // Pindah ke halaman verifikasi
                Intent intent = new Intent(ForgotPasswordActivity.this, VerificationEmailActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        // Kembali ke Login
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
