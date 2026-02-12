package com.example.project88trans.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project88trans.R;

public class RingkasanBookingActivity extends AppCompatActivity {

    TextView tvNama, tvEmail, tvTelepon;
    TextView tvJenis, tvNamaPaketAtauBus, tvTanggal, tvJumlah, tvTotalHarga;
    Button btnKonfirmasi;
    ImageView btnBack;

    String nama, email, telepon;
    String jenis, namaPaketAtauBus, tanggal;
    int jumlahOrang;
    double totalHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);

        initViews();
        setupStepIndicator();
        loadUserData();
        loadBookingData();
        setupButton();
    }

    private void initViews() {
        tvNama = findViewById(R.id.tvNama);
        tvEmail = findViewById(R.id.tvEmail);
        tvTelepon = findViewById(R.id.tvTelepon);

        tvJenis = findViewById(R.id.tvJenis);
        tvNamaPaketAtauBus = findViewById(R.id.tvNamaPaketAtauBus);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvJumlah = findViewById(R.id.tvJumlah);
        tvTotalHarga = findViewById(R.id.tvTotalHarga);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);

        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        } else {
            Log.w("BookingDetail", "btnBack not found in current layout");
        }
    }

    private void setupStepIndicator() {
        TextView step1 = findViewById(R.id.step1);
        TextView step2 = findViewById(R.id.step2);
        TextView step3 = findViewById(R.id.step3);

        TextView labelStep1 = findViewById(R.id.labelStep1);
        TextView labelStep2 = findViewById(R.id.labelStep2);
        TextView labelStep3 = findViewById(R.id.labelStep3);

        // Step 1 = inactive
        step1.setBackgroundResource(R.drawable.bg_step_inactive);
        step1.setTextColor(getResources().getColor(R.color.gray_disabled));
        labelStep1.setTextColor(getResources().getColor(R.color.gray_disabled));

        // Step 2 = ACTIVE (MERAH)
        step2.setBackgroundResource(R.drawable.bg_step_inactive);
        step2.setTextColor(getResources().getColor(R.color.white));
        labelStep2.setTextColor(getResources().getColor(R.color.gray_disabled));

        // Step 3 = inactive
        step3.setBackgroundResource(R.drawable.bg_step_active);
        step3.setTextColor(getResources().getColor(R.color.gray_disabled));
        labelStep3.setTextColor(getResources().getColor(R.color.primary_red));
    }

    private void loadUserData() {
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        nama = sp.getString("nama", "-");
        email = sp.getString("email", "-");
        telepon = sp.getString("phone", "-");

        tvNama.setText(nama);
        tvEmail.setText(email);
        tvTelepon.setText(telepon);
    }

    private void loadBookingData() {
        Intent intent = getIntent();

        jenis = intent.getStringExtra("type");
        namaPaketAtauBus = intent.getStringExtra("nama_paket_atau_bus");
        tanggal = intent.getStringExtra("tanggal");
        jumlahOrang = intent.getIntExtra("jumlah_orang", 0);
        totalHarga = intent.getDoubleExtra("total_harga", 0);

        tvJenis.setText("Jenis: " + jenis);
        tvNamaPaketAtauBus.setText("Nama Paket / Armada: " + namaPaketAtauBus);
        tvTanggal.setText("Tanggal: " + tanggal);
        tvJumlah.setText("Jumlah Orang: " + jumlahOrang);
        tvTotalHarga.setText("Total Harga: Rp " + String.format("%,.0f", totalHarga).replace(",", "."));
    }

    private void setupButton() {
        btnKonfirmasi.setOnClickListener(v -> showPaymentPopup());
    }

    private void showPaymentPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog); // pakai style custom
        builder.setTitle("Pesanan Berhasil 🎉")
                .setMessage("Pesanan kamu telah berhasil dibuat. Silakan cek pesanan dan lakukan pembayaran di halaman pesanan.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    // Kembali ke Dashboard
                    Intent intent = new Intent(RingkasanBookingActivity.this, PesananSayaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Custom warna tombol
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary_red));
    }

}
