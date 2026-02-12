package com.example.project88trans.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Fragment.DetailBusFragment;
import com.example.project88trans.R;
import com.example.project88trans.model.bus;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArmadaActivity extends AppCompatActivity {

    private static final String TAG = "ArmadaActivity";
    private RecyclerView recyclerView;
    private EditText etSearch;
    private TextView tvTitle;
    private View fragmentContainer;
    private SimpleBusAdapter adapter;
    private List<bus> busList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armada);

        Log.d(TAG, "onCreate started");

        initializeViews();
        setupRecyclerView();
        setupBackPressHandler();
        loadBusData();
        setupSearch();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.rvBusList);
        etSearch = findViewById(R.id.etSearch);
        tvTitle = findViewById(R.id.tvTitle);
        fragmentContainer = findViewById(R.id.fragment_container_armada);

        if (tvTitle != null) {
            tvTitle.setText("Armada Bus");
        }

        Log.d(TAG, "Views initialized");
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "OnBackPressedDispatcher called");

                if (fragmentContainer != null && fragmentContainer.getVisibility() == View.VISIBLE) {
                    Log.d(TAG, "Fragment is visible, showing bus list");
                    showBusList();
                    getSupportFragmentManager().popBackStack();
                } else {
                    Log.d(TAG, "No fragment visible, finishing activity");
                    setEnabled(false);
                    ArmadaActivity.super.onBackPressed();
                }
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new SimpleBusAdapter(this, busList, new SimpleBusAdapter.OnBusClickListener() {
            @Override
            public void onBusClick(int busId) {
                Log.d(TAG, "onBusClick: " + busId);
                showDetailBusFragment(busId);
            }

            @Override
            public void onBookingClick(bus busItem) {
                Log.d(TAG, "onBookingClick for bus: " + busItem.getName());
                startBookingActivity(busItem);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "RecyclerView setup complete");
    }

    private void startBookingActivity(bus busItem) {
        Log.d(TAG, "startBookingActivity for bus: " + busItem.getName());

        try {
            // Cek ketersediaan bus
            boolean isAvailable = busItem.getStatus() == null ||
                    busItem.getStatus().isEmpty() ||
                    busItem.getStatus().equalsIgnoreCase("available");

            if (!isAvailable) {
                Toast.makeText(this, "Bus tidak tersedia untuk booking", Toast.LENGTH_SHORT).show();
                return;
            }

            // Buat Intent ke BookingActivity
            Intent intent = new Intent(this, BookingActivity.class);

            // PERBAIKAN: Gunakan key "type" bukan "booking_type" untuk kompatibilitas
            intent.putExtra("type", "bus"); // INI YANG PERLU DIPERBAIKI!

            // Kirim data bus yang diperlukan - SESUAI DENGAN FORMAT YANG DITUNGGU BookingActivity
            intent.putExtra("bus_id", busItem.getId());
            intent.putExtra("bus_name", busItem.getName());
            intent.putExtra("bus_price", busItem.getPrice());
            intent.putExtra("bus_type", busItem.getType_bus());
            intent.putExtra("bus_capacity", busItem.getCapacity());

            // Data tambahan jika ada
            if (busItem.getDescription() != null) {
                intent.putExtra("bus_description", busItem.getDescription());
            }

            if (busItem.getFeatures() != null && !busItem.getFeatures().isEmpty()) {
                intent.putStringArrayListExtra("bus_features", new ArrayList<>(busItem.getFeatures()));
            }

            intent.putExtra("bus_rating", busItem.getRating());
            intent.putExtra("bus_reviews_count", busItem.getReviews_count());

            // Kirim gambar jika diperlukan
            if (busItem.getImage() != null) {
                intent.putExtra("bus_image", busItem.getImage());
            }

            // Start activity
            startActivity(intent);
            Log.d(TAG, "BookingActivity started for bus: " + busItem.getName());

        } catch (Exception e) {
            Log.e(TAG, "Error starting BookingActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Gagal membuka halaman booking", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetailBusFragment(int busId) {
        Log.d(TAG, "showDetailBusFragment for ID: " + busId);

        try {
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }

            if (etSearch != null) {
                etSearch.setVisibility(View.GONE);
            }

            if (tvTitle != null) {
                tvTitle.setVisibility(View.GONE);
            }

            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.VISIBLE);
            }

            DetailBusFragment detailFragment = DetailBusFragment.newInstance(busId);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_armada, detailFragment, "DETAIL_FRAGMENT")
                    .addToBackStack("detail")
                    .commit();

            Log.d(TAG, "Fragment transaction committed");

        } catch (Exception e) {
            Log.e(TAG, "Error showing detail fragment: " + e.getMessage(), e);
            Toast.makeText(this, "Error membuka detail", Toast.LENGTH_SHORT).show();
        }
    }

    public void showBusList() {
        Log.d(TAG, "showBusList called");

        try {
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            if (etSearch != null) {
                etSearch.setVisibility(View.VISIBLE);
            }

            if (tvTitle != null) {
                tvTitle.setVisibility(View.VISIBLE);
            }

            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.GONE);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container_armada);

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error showing bus list: " + e.getMessage(), e);
        }
    }

    private void loadBusData() {
        Log.d(TAG, "loadBusData called");

        Toast.makeText(this, "Memuat data armada...", Toast.LENGTH_SHORT).show();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<bus>>> call = apiService.getBuses();

        call.enqueue(new Callback<ApiResponse<List<bus>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<bus>>> call, Response<ApiResponse<List<bus>>> response) {
                Log.d(TAG, "API Response received");

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response is successful");

                    if (response.body().isSuccess()) {
                        List<bus> buses = response.body().getData();
                        Log.d(TAG, "Data received, count: " + (buses != null ? buses.size() : 0));

                        if (buses != null && !buses.isEmpty()) {
                            // Set default status jika null
                            for (bus b : buses) {
                                if (b.getStatus() == null || b.getStatus().isEmpty()) {
                                    b.setStatus("available");
                                }
                            }

                            busList.clear();
                            busList.addAll(buses);
                            adapter.setData(buses);

                            String message = buses.size() + " armada ditemukan";
                            Toast.makeText(ArmadaActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ArmadaActivity.this, "Tidak ada data armada", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = response.body().getMessage() != null ?
                                response.body().getMessage() : "Gagal memuat data armada";
                        Toast.makeText(ArmadaActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ArmadaActivity.this, "Gagal memuat data armada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<bus>>> call, Throwable t) {
                Log.e(TAG, "API Call failed: " + t.getMessage(), t);
                Toast.makeText(ArmadaActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        Log.d(TAG, "setupSearch called");

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (adapter != null) {
                        adapter.getFilter().filter(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    // ===================================================================
    // SIMPLE BUS ADAPTER
    // ===================================================================
    public static class SimpleBusAdapter extends RecyclerView.Adapter<SimpleBusAdapter.ViewHolder> implements Filterable {
        private Context context;
        private List<bus> busList;
        private List<bus> busListFull;
        private OnBusClickListener listener;

        public interface OnBusClickListener {
            void onBusClick(int busId);
            void onBookingClick(bus busItem);
        }

        public SimpleBusAdapter(Context context, List<bus> busList, OnBusClickListener listener) {
            this.context = context;
            this.busList = busList;
            this.busListFull = new ArrayList<>(busList);
            this.listener = listener;
        }

        public void setData(List<bus> newList) {
            busList.clear();
            if (newList != null) {
                busList.addAll(newList);
            }
            busListFull = new ArrayList<>(busList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_bus_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            bus busItem = busList.get(position);

            // Set data dasar
            holder.tvTitle.setText(busItem.getName());
            holder.tvBusDesc.setText(busItem.getType_bus());

            // Format harga
            NumberFormat formatPrice = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            holder.tvBusPrice.setText(formatPrice.format(busItem.getPrice()) + " / hari");

            // Cek ketersediaan
            boolean isAvailable = busItem.getStatus() == null ||
                    busItem.getStatus().isEmpty() ||
                    busItem.getStatus().equalsIgnoreCase("available");

            // Set tombol booking
            if (isAvailable) {
                holder.btnBooking.setEnabled(true);
                holder.btnBooking.setBackgroundTintList(context.getResources().getColorStateList(R.color.red));
                holder.btnBooking.setText("Booking");
            } else {
                holder.btnBooking.setEnabled(false);
                holder.btnBooking.setBackgroundTintList(context.getResources().getColorStateList(R.color.dark_gray));
                holder.btnBooking.setText("Tidak Tersedia");
            }

            // Tombol booking
            holder.btnBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // HENTIKAN PROPAGASI EVENT
                    v.setClickable(false);

                    // Cek ketersediaan lagi
                    boolean canBook = busItem.getStatus() == null ||
                            busItem.getStatus().isEmpty() ||
                            busItem.getStatus().equalsIgnoreCase("available");

                    if (canBook) {
                        // LANGSUNG buka BookingActivity dari sini
                        Intent intent = new Intent(context, BookingActivity.class);

                        // PERBAIKAN: Gunakan key "type" bukan "booking_type"
                        intent.putExtra("type", "bus");
                        intent.putExtra("bus_id", busItem.getId());
                        intent.putExtra("bus_name", busItem.getName());
                        intent.putExtra("bus_price", busItem.getPrice());
                        intent.putExtra("bus_type", busItem.getType_bus());
                        intent.putExtra("bus_capacity", busItem.getCapacity());

                        // Data tambahan
                        if (busItem.getDescription() != null) {
                            intent.putExtra("bus_description", busItem.getDescription());
                        }

                        if (busItem.getFeatures() != null && !busItem.getFeatures().isEmpty()) {
                            intent.putStringArrayListExtra("bus_features", new ArrayList<>(busItem.getFeatures()));
                        }

                        intent.putExtra("bus_rating", busItem.getRating());
                        intent.putExtra("bus_reviews_count", busItem.getReviews_count());

                        // Mulai activity
                        context.startActivity(intent);

                        // Tampilkan toast konfirmasi
                        Toast.makeText(context, "Membuka booking untuk " + busItem.getName(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Bus tidak tersedia untuk booking",
                                Toast.LENGTH_SHORT).show();
                    }

                    // Aktifkan kembali tombol setelah 500ms
                    v.postDelayed(() -> v.setClickable(true), 500);
                }
            });

            // Tombol detail
            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onBusClick(busItem.getId());
                    }
                }
            });

            // Klik item
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onBusClick(busItem.getId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return busList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgBus;
            TextView tvTitle, tvRating, tvReviews, tvBusDesc, tvBusFacilities, tvBusPrice;
            Button btnDetail, btnBooking;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgBus = itemView.findViewById(R.id.imgBus);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvRating = itemView.findViewById(R.id.tvRating);
                tvReviews = itemView.findViewById(R.id.tvReviews);
                tvBusDesc = itemView.findViewById(R.id.tvBusDesc);
                tvBusFacilities = itemView.findViewById(R.id.tvBusFacilities);
                tvBusPrice = itemView.findViewById(R.id.tvBusPrice);
                btnDetail = itemView.findViewById(R.id.btnDetail);
                btnBooking = itemView.findViewById(R.id.btnBooking);
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<bus> filteredList = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        filteredList.addAll(busListFull);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (bus item : busListFull) {
                            if (item.getName().toLowerCase().contains(filterPattern) ||
                                    (item.getType_bus() != null &&
                                            item.getType_bus().toLowerCase().contains(filterPattern))) {
                                filteredList.add(item);
                            }
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredList;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    busList.clear();
                    if (results.values != null) {
                        busList.addAll((List<bus>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}