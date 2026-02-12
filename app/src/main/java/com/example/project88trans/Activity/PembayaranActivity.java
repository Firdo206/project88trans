package com.example.project88trans.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.project88trans.Fragment.CashFragment;
import com.example.project88trans.Fragment.TransferFragment;
import com.example.project88trans.R;

public class PembayaranActivity extends AppCompatActivity {

    EditText edtPromo;
    MaterialButton btnApplyPromo;
    TextView tvTotalBayar;
    MaterialCardView cardCash, cardTransfer;
    ImageView btnBack;
    double totalBayar = 0;
    double discount = 0;

    String rentalType;
    int rentalId;
    String paymentMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        edtPromo = findViewById(R.id.edtPromo);
        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        tvTotalBayar = findViewById(R.id.tvTotalBayar);
        cardCash = findViewById(R.id.cardCash);
        cardTransfer = findViewById(R.id.cardTransfer);
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        } else {
            Log.w("BookingDetail", "btnBack not found in current layout");
        }

        // Ambil data dari intent
        totalBayar = getIntent().getDoubleExtra("total", 0);
        rentalType = getIntent().getStringExtra("rental_type");
        rentalId = getIntent().getIntExtra("rental_id", 0);

        tvTotalBayar.setText("Rp " + String.format("%,.0f", totalBayar).replace(",", "."));

        // Apply promo
        btnApplyPromo.setOnClickListener(v -> {
            String kode = edtPromo.getText().toString().trim();
            if (!kode.isEmpty()) {
                discount = 0.2 * totalBayar; // contoh diskon 20%
                double newTotal = totalBayar - discount;
                tvTotalBayar.setText("Rp " + String.format("%,.0f", newTotal).replace(",", "."));
                Toast.makeText(this, "Promo berhasil diterapkan!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Masukkan kode promo terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });

        // Pilih metode pembayaran
        cardCash.setOnClickListener(v -> {
            paymentMethod = "cash";
            cardCash.setStrokeWidth(4);
            cardCash.setStrokeColor(getResources().getColor(R.color.primary_red));
            cardTransfer.setStrokeWidth(0);

            CashFragment cashFragment = CashFragment.newInstance(
                    1,
                    rentalType,
                    rentalId,
                    edtPromo.getText().toString(),
                    totalBayar - discount
            );
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, cashFragment)
                    .addToBackStack(null)
                    .commit();
        });

        cardTransfer.setOnClickListener(v -> {
            paymentMethod = "transfer";
            cardTransfer.setStrokeWidth(4);
            cardTransfer.setStrokeColor(getResources().getColor(R.color.primary_red));
            cardCash.setStrokeWidth(0);

            TransferFragment transferFragment = TransferFragment.newInstance(
                    1,
                    rentalType,
                    rentalId,
                    edtPromo.getText().toString(),
                    totalBayar - discount
            );
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, transferFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
