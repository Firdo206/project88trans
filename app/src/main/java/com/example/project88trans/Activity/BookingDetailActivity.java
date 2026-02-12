package com.example.project88trans.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Fragment.AllPromoFragment;
import com.example.project88trans.R;
import com.example.project88trans.model.BusRentalCreate;
import com.example.project88trans.model.TourRentalCreate;
import com.example.project88trans.model.Promo;
import android.widget.ImageView;
import android.text.TextUtils;
import java.util.ArrayList;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailActivity extends AppCompatActivity {
    TextView tvSummary, tvKapasitas, tvHargaTour,tvHargaBus, tvLokasi;
    EditText edtTanggalBerangkat, edtTanggalPulang, edtTanggal, edtJumlahOrang;
    FrameLayout fragmentPromo;
    Button btnBooking;
    ImageView btnBack;
    SharedPreferences sp;
    int userId;
    String type;
    int busId;
    int packageId;
    int numberOfPeople;
    double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getStringExtra("type");
        if ("bus".equalsIgnoreCase(type)) {
            setContentView(R.layout.activity_booking_armada);
        } else {
            setContentView(R.layout.activity_booking_pw);
        }

        sp = getSharedPreferences("UserSession", MODE_PRIVATE);

        // 🔥 FIX USER ID — dijamin tidak 0 lagi
        userId = sp.getInt("user_id", 0);
        Log.d("BOOKING", "User ID Loaded = " + userId);

        if (userId == 0) {
            Toast.makeText(this, "Silakan login ulang (User ID tidak ditemukan)", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupStepIndicator();
        setupDatePickers();
        loadDataFromIntent();
        showCardDetail();
        setupButton();

    }

    private void initViews() {
        tvSummary = findViewById(R.id.tvSummary);
        tvKapasitas = findViewById(R.id.tvKapasitas);
        tvHargaTour = findViewById(R.id.tvHargaTour);
        tvHargaBus = findViewById(R.id.tvHargaBus);
        tvLokasi = findViewById(R.id.tvLokasiPaket);

        edtTanggalBerangkat = findViewById(R.id.edtTanggalBerangkat);
        edtTanggalPulang = findViewById(R.id.edtTanggalPulang);
        edtTanggal = findViewById(R.id.edtTanggal);
        edtJumlahOrang = findViewById(R.id.edtJumlahOrang);
        btnBooking = findViewById(R.id.btnBooking);
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
        step2.setBackgroundResource(R.drawable.bg_step_active);
        step2.setTextColor(getResources().getColor(R.color.white));
        labelStep2.setTextColor(getResources().getColor(R.color.primary_red));

        // Step 3 = inactive
        step3.setBackgroundResource(R.drawable.bg_step_inactive);
        step3.setTextColor(getResources().getColor(R.color.gray_disabled));
        labelStep3.setTextColor(getResources().getColor(R.color.gray_disabled));
    }


    private void setupDatePickers() {
        if ("bus".equalsIgnoreCase(type)) {
            edtTanggalBerangkat.setFocusable(false);
            edtTanggalBerangkat.setOnClickListener(v -> showDatePicker(edtTanggalBerangkat));
            edtTanggalPulang.setFocusable(false);
            edtTanggalPulang.setOnClickListener(v -> showDatePicker(edtTanggalPulang));
        } else {
            edtTanggal.setFocusable(false);
            edtTanggal.setOnClickListener(v -> showDatePicker(edtTanggal));
        }
    }

    private void showDatePicker(EditText target) {
        Calendar c = Calendar.getInstance();
        new android.app.DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    target.setText(sdf.format(selected.getTime()));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadDataFromIntent() {
        if ("bus".equalsIgnoreCase(type)) {
            busId = getIntent().getIntExtra("bus_id", -1);
            if (getIntent().hasExtra("bus_price")) {
                totalPrice = getIntent().getDoubleExtra("bus_price", 0);
            }
            tvSummary.setText(getIntent().getStringExtra("bus_type"));
            tvKapasitas.setText("Kapasitas: " + getIntent().getIntExtra("bus_capacity", 0));
        } else {
            packageId = getIntent().getIntExtra("package_id", -1);
            numberOfPeople = getIntent().getIntExtra("fixed_capacity", 0);
            if (getIntent().hasExtra("price")) {
                totalPrice = getIntent().getDoubleExtra("price", 0);
            }
            tvSummary.setText(getIntent().getStringExtra("nama_tour"));
            ArrayList<String> destinasiUtama = getIntent().getStringArrayListExtra("destinasi_utama");

            if (destinasiUtama != null && !destinasiUtama.isEmpty()) {
                tvLokasi.setText(TextUtils.join(", ", destinasiUtama));
            } else {
                tvLokasi.setText("-");
            }
            edtJumlahOrang.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) validateJumlahOrang(numberOfPeople);
            });

        }
    }

    private void validateJumlahOrang(int maxPeople) {
        String input = edtJumlahOrang.getText().toString().trim();

        if (input.isEmpty()) {
            edtJumlahOrang.setText(String.valueOf(maxPeople));
            return;
        }

        int value = Integer.parseInt(input);

        if (value > maxPeople) {
            showTopPopup("Jumlah melebihi batas (" + maxPeople + ")");
            edtJumlahOrang.setText(String.valueOf(maxPeople));
        }
    }

    private void showTopPopup(String message) {
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.popup_limit);

        TextView tvMsg = dialog.findViewById(R.id.tvPopupMessage);
        tvMsg.setText(message);

        // Posisi di atas
        dialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(android.view.Gravity.TOP);

        dialog.show();

        // Auto close dalam 2 detik
        new android.os.Handler().postDelayed(dialog::dismiss, 2000);
    }


    private void showCardDetail() {
        if ("bus".equalsIgnoreCase(type)) {
            if (tvHargaBus != null) {
                tvHargaBus.setText("Rp " + String.format("%,.0f", totalPrice).replace(",", "."));
            }
        } else {
            if (tvHargaTour != null) {
                tvHargaTour.setText("Rp " + String.format("%,.0f", totalPrice).replace(",", "."));
            }
        }
    }


    private void setupButton() {
        btnBooking.setOnClickListener(v -> makeBooking());
    }


    private void makeBooking() {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        if ("bus".equalsIgnoreCase(type)) {
            String startDate = edtTanggalBerangkat.getText().toString().trim();
            String endDate = edtTanggalPulang.getText().toString().trim();

            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Tanggal harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            api.addBusRental(userId, busId, startDate, endDate, null)
                    .enqueue(new Callback<ApiResponse<BusRentalCreate>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<BusRentalCreate>> call, Response<ApiResponse<BusRentalCreate>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                BusRentalCreate data = response.body().getData();
                                // Buka RingkasanBookingActivity langsung setelah sukses
                                openRingkasanBooking(data.getId(), data.getTotalPrice(), startDate + " s/d " + endDate, getIntent().getIntExtra("bus_capacity", 0));
                            } else {
                                Toast.makeText(BookingDetailActivity.this, "Gagal booking bus", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<BusRentalCreate>> call, Throwable t) {
                            Toast.makeText(BookingDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            String startDate = edtTanggal.getText().toString().trim();
            int people = Integer.parseInt(edtJumlahOrang.getText().toString().trim());

            if (startDate.isEmpty() || people <= 0) {
                Toast.makeText(this, "Tanggal & jumlah orang wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (people > numberOfPeople) {
                showTopPopup("Jumlah melebihi batas (" + numberOfPeople + ")");
                edtJumlahOrang.setText(String.valueOf(numberOfPeople));
                return; // stop, jangan lanjut booking
            }


            api.addTourRental(userId, packageId, people, startDate, totalPrice, null)
                    .enqueue(new Callback<ApiResponse<TourRentalCreate>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<TourRentalCreate>> call, Response<ApiResponse<TourRentalCreate>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                TourRentalCreate data = response.body().getData();
                                openRingkasanBooking(data.getId(), data.getTotalPrice(), startDate, people);
                            } else {
                                Toast.makeText(BookingDetailActivity.this, "Gagal booking paket", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<TourRentalCreate>> call, Throwable t) {
                            Toast.makeText(BookingDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Buka RingkasanBookingActivity (Hanya untuk tampilan)
    private void openRingkasanBooking(int bookingId, double totalPrice, String tanggal, int jumlah) {
        Intent i = new Intent(this, RingkasanBookingActivity.class);
        i.putExtra("type", type);
        i.putExtra("nama_paket_atau_bus", tvSummary.getText().toString());
        i.putExtra("tanggal", tanggal);
        i.putExtra("jumlah_orang", jumlah);
        i.putExtra("total_harga", totalPrice);
        if ("bus".equalsIgnoreCase(type)) i.putExtra("bus_id", busId);
        else i.putExtra("package_id", packageId);
        i.putExtra("booking_id", bookingId); // optional kalau mau disimpan
        startActivity(i);
    }

}
