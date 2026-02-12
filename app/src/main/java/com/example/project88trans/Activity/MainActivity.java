package com.example.project88trans.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Bottom.PromoBottomSheet;
import com.example.project88trans.Fragment.PaketWisataFragment;
import com.example.project88trans.Fragment.AllPromoFragment;
import com.example.project88trans.R;
import com.example.project88trans.model.PaketWisata;
import com.example.project88trans.model.Promo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.project88trans.Adapater.PromoAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvViewAll, tvUserName, tvViewAllPromo;
    private boolean isLoggedIn;
    private final HashMap<Integer, Integer> menuOrder = new HashMap<>();
    private int lastMenuIndex = 0;

    private LinearLayout containerPaketHome;

    // Promo
    private RecyclerView rvPromo;
    private PromoAdapter promoAdapter;
    private final List<Promo> promoList = new ArrayList<>();

    private boolean isActive = false; // Flag untuk lifecycle aman

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isActive = true;

        initViews();
        setupNavigation();
        loadPaketWisataCard();
        loadPromo();
    }

    private void initViews() {
        tvViewAll = findViewById(R.id.tvViewAll);
        tvUserName = findViewById(R.id.tvUserName);
        containerPaketHome = findViewById(R.id.containerPaketHome);
        rvPromo = findViewById(R.id.rvPromo);
        tvViewAllPromo = findViewById(R.id.tvViewAllPromo);

        rvPromo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Adapter aman
        promoAdapter = new PromoAdapter(this, promoList, promo -> {
            if (!isActivityAlive()) return;

            PromoBottomSheet sheet = PromoBottomSheet.newInstance(
                    promo.getName(), promo.getCode(), promo.getAmount(),
                    promo.getStart_date(), promo.getEnd_date(), promo.getSlot()
            );

            if (isActivityAlive()) {
                sheet.show(getSupportFragmentManager(), "PromoSheet");
            }
        });

        rvPromo.setAdapter(promoAdapter);

        // Session
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        tvUserName.setText(sp.getString("username", "Guest"));
        isLoggedIn = sp.getBoolean("isLoggedIn", false);

        // View All Promo
        tvViewAllPromo.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
                goToLogin(true);
                return;
            }
            openPromoFragment();
        });

        // View All Paket
        tvViewAll.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
                goToLogin(true);
                return;
            }
            openPaketWisataFragment();
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        menuOrder.put(R.id.nav_home, 0);
        menuOrder.put(R.id.nav_order, 1);
        menuOrder.put(R.id.nav_armada, 2);
        menuOrder.put(R.id.nav_profile, 3);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            boolean isRight = menuOrder.get(id) > lastMenuIndex;
            lastMenuIndex = menuOrder.get(id);

            if (id == R.id.nav_home) {
                showHomePage();
                applyTransition(isRight);
                return true;
            }

            if (id == R.id.nav_order) {
                if (isLoggedIn && isActivityAlive()) {
                    startActivity(new Intent(this, PesananSayaActivity.class));
                    applyTransition(isRight);
                } else goToLogin(true);
                return true;
            }

            if (id == R.id.nav_armada) {
                if (isLoggedIn && isActivityAlive()) {
                    startActivity(new Intent(this, ArmadaActivity.class));
                    applyTransition(isRight);
                } else goToLogin(true);
                return true;
            }

            if (id == R.id.nav_profile) {
                if (isLoggedIn) goToProfile(isRight);
                else goToLogin(true);
                return true;
            }

            return false;
        });
    }

    // ================= LOAD DATA ====================

    private void loadPaketWisataCard() {
        containerPaketHome.removeAllViews();

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAllPaketWisata().enqueue(new Callback<ApiResponse<List<PaketWisata>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaketWisata>>> call, Response<ApiResponse<List<PaketWisata>>> res) {
                if (!isActivityAlive()) return;

                if (!res.isSuccessful() || res.body() == null) {
                    Toast.makeText(MainActivity.this, "Gagal memuat paket wisata", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<PaketWisata> list = res.body().getData();
                if (list == null) list = new ArrayList<>();

                int max = Math.min(2, list.size());
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

                for (int i = 0; i < max; i++) {
                    PaketWisata paket = list.get(i);
                    View card = inflater.inflate(R.layout.item_paket_wisata, containerPaketHome, false);

                    ImageView img = card.findViewById(R.id.imgPaket);
                    TextView title = card.findViewById(R.id.tvTitle);
                    TextView rating = card.findViewById(R.id.tvRating);
                    TextView review = card.findViewById(R.id.tvReviews);
                    TextView lokasi = card.findViewById(R.id.tvDuration);
                    TextView harga = card.findViewById(R.id.tvHarga);
                    TextView hargaCoret = card.findViewById(R.id.tvHargacoret);
                    ChipGroup destinasi = card.findViewById(R.id.containerDestinasi);
                    ChipGroup fasilitas = card.findViewById(R.id.containerFasilitas);
                    Button btnBook = card.findViewById(R.id.btnBooking);

                    title.setText(paket.getNama());
                    rating.setText(String.valueOf(paket.getRating()));
                    review.setText("(" + paket.getJumlahUlasan() + " ulasan)");

                    String lokasiUtama = (paket.getDestinasiUtama() != null && !paket.getDestinasiUtama().isEmpty())
                            ? paket.getDestinasiUtama().get(0) : "-";

                    lokasi.setText(lokasiUtama + " • " + paket.getDurasi());

                    NumberFormat format = new DecimalFormat("#,###");
                    harga.setText("Rp " + format.format(paket.getHargaPromo()));
                    hargaCoret.setText("Rp " + format.format(paket.getHargaAsli()));
                    hargaCoret.setPaintFlags(hargaCoret.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                    // Glide aman pakai application context
                    try {
                        if (paket.getGambarUrl() != null && !paket.getGambarUrl().isEmpty()) {
                            byte[] decoded = Base64.decode(paket.getGambarUrl(), Base64.DEFAULT);
                            Glide.with(getApplicationContext()).asBitmap().load(decoded)
                                    .placeholder(R.drawable.yogyakarta_tugu)
                                    .into(img);
                        } else img.setImageResource(R.drawable.yogyakarta_tugu);
                    } catch (Exception e) {
                        img.setImageResource(R.drawable.yogyakarta_tugu);
                    }

                    // Chip Destinasi
                    destinasi.removeAllViews();
                    if (paket.getDetailDestinasi() != null)
                        for (String d : paket.getDetailDestinasi()) {
                            Chip c = new Chip(MainActivity.this);
                            c.setText(d);
                            c.setTextColor(getResources().getColor(R.color.white));
                            c.setChipBackgroundColorResource(R.color.primary_red);
                            c.setClickable(false);
                            destinasi.addView(c);
                        }

                    // Chip Fasilitas
                    fasilitas.removeAllViews();
                    if (paket.getFasilitas() != null)
                        for (String f : paket.getFasilitas()) {
                            Chip c = new Chip(MainActivity.this);
                            c.setText(f);
                            c.setTextColor(getResources().getColor(R.color.black));
                            c.setChipBackgroundColorResource(R.color.light_gray);
                            c.setClickable(false);
                            fasilitas.addView(c);
                        }

                    Chip chipCapacity = new Chip(MainActivity.this);
                    chipCapacity.setText("Kapasitas: " + paket.getFixedCapacity() + " orang");
                    chipCapacity.setChipBackgroundColorResource(R.color.light_gray);
                    chipCapacity.setTextColor(getResources().getColor(R.color.black));
                    chipCapacity.setClickable(false);
                    chipCapacity.setCheckable(false);
                    fasilitas.addView(chipCapacity);

                    btnBook.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, BookingActivity.class);

                        intent.putExtra("type", "tour");
                        intent.putExtra("package_id", paket.getId());
                        intent.putExtra("nama_tour", paket.getNama());
                        intent.putExtra("durasi", paket.getDurasi());
                        intent.putExtra("price", paket.getHargaPromo());
                        intent.putExtra("fixed_capacity", paket.getFixedCapacity());

                        if (paket.getDestinasiUtama() != null)
                            intent.putStringArrayListExtra("destinasi_utama", new ArrayList<>(paket.getDestinasiUtama()));
                        if (paket.getFasilitas() != null)
                            intent.putStringArrayListExtra("fasilitas", new ArrayList<>(paket.getFasilitas()));

                        startActivity(intent);
                    });


                    containerPaketHome.addView(card);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaketWisata>>> call, Throwable t) {
                if (isActivityAlive())
                    Toast.makeText(MainActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPromo() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAllPromo().enqueue(new Callback<ApiResponse<List<Promo>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Promo>>> call, Response<ApiResponse<List<Promo>>> res) {
                if (!isActivityAlive()) return;

                List<Promo> data = res.body().getData();
                if (data != null) {
                    promoList.clear();
                    promoList.addAll(data.subList(0, Math.min(4, data.size())));
                    promoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Promo>>> call, Throwable t) {
                if (isActivityAlive())
                    Toast.makeText(MainActivity.this, "Gagal memuat promo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= HELPERS ====================
    private void openPaketWisataFragment() {
        if (!isActivityAlive()) return;

        PaketWisataFragment fragment = new PaketWisataFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();

        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
    }

    private void openPromoFragment() {
        if (!isActivityAlive()) return;

        AllPromoFragment fragment = new AllPromoFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();

        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
    }

    private void showHomePage() {
        findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
    }

    private void goToProfile(boolean isRight) {
        if (!isActivityAlive()) return;
        startActivity(new Intent(this, ProfileActivity.class));
        applyTransition(isRight);
    }

    private void goToLogin(boolean isRight) {
        if (!isActivityAlive()) return;
        startActivity(new Intent(this, LoginActivity.class));
        applyTransition(isRight);
    }

    private void applyTransition(boolean isRight) {
        if (isRight) overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        else overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private boolean isActivityAlive() {
        return isActive && !isFinishing() && !isDestroyed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (rvPromo != null) rvPromo.setAdapter(null); // Cegah crash
    }

    @Override
    protected void onDestroy() {
        isActive = false;

        if (rvPromo != null) rvPromo.setAdapter(null);

        try {
            Glide.with(getApplicationContext()).onStop();
        } catch (Exception ignore) {}

        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
