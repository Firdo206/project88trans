package com.example.project88trans.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.R;
import com.example.project88trans.model.bus;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBusFragment extends Fragment {

    private static final String TAG = "DetailBusFragment";
    private int busId;
    private bus busData;
    private ProgressBar progressBar;

    private ImageView imageBus;
    private TextView textNamaBus, textTipeBus, textHargaBus, textDeskripsiBus, textFasilitasBus, textRating, textReviews;

    public static DetailBusFragment newInstance(int id) {
        DetailBusFragment fragment = new DetailBusFragment();
        Bundle args = new Bundle();
        args.putInt("bus_id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called for bus ID: " + busId);
        return inflater.inflate(R.layout.fragment_detail_bus, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        initializeViews(view);
        getBusIdFromArguments();

        if (busId != -1) {
            Log.d(TAG, "Fetching detail for bus ID: " + busId);
            fetchBusDetail();
        } else {
            Log.e(TAG, "Invalid bus ID");
            Toast.makeText(getContext(), "ID bus tidak valid", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    private void initializeViews(View view) {
        Log.d(TAG, "Initializing views");

        // Sesuaikan dengan ID di layout fragment_detail_bus.xml Anda
        imageBus = view.findViewById(R.id.imageBus);
        textNamaBus = view.findViewById(R.id.textNamaBus);
        textTipeBus = view.findViewById(R.id.textTipeBus);
        textHargaBus = view.findViewById(R.id.textHargaBus);
        textDeskripsiBus = view.findViewById(R.id.textDeskripsiBus);
        textFasilitasBus = view.findViewById(R.id.textFasilitasBus);
        textRating = view.findViewById(R.id.textRating);
        textReviews = view.findViewById(R.id.textReviews);

        // Progress bar (jika ada) - jika tidak ada, buat sederhana
        progressBar = new ProgressBar(getContext());
        progressBar.setVisibility(View.GONE);

        // Debug: Check jika semua view ditemukan
        Log.d(TAG, "Views found:");
        Log.d(TAG, "imageBus: " + (imageBus != null));
        Log.d(TAG, "textNamaBus: " + (textNamaBus != null));
        Log.d(TAG, "textTipeBus: " + (textTipeBus != null));
        Log.d(TAG, "textHargaBus: " + (textHargaBus != null));
        Log.d(TAG, "textDeskripsiBus: " + (textDeskripsiBus != null));
        Log.d(TAG, "textFasilitasBus: " + (textFasilitasBus != null));
        Log.d(TAG, "textRating: " + (textRating != null));
        Log.d(TAG, "textReviews: " + (textReviews != null));

        // Tambahkan tombol back jika tidak ada di layout
        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        } else {
            // Jika tidak ada tombol back, buat sendiri
            Log.w(TAG, "No back button found in layout");
        }
    }

    private void getBusIdFromArguments() {
        if (getArguments() != null) {
            busId = getArguments().getInt("bus_id", -1);
            Log.d(TAG, "Got bus ID from arguments: " + busId);
        } else {
            Log.w(TAG, "No arguments found!");
        }
    }

    private void fetchBusDetail() {
        Log.d(TAG, "fetchBusDetail called for ID: " + busId);

        // Tampilkan loading state
        if (textNamaBus != null) {
            textNamaBus.setText("Memuat data...");
            textHargaBus.setText("Loading...");
            textDeskripsiBus.setText("Sedang memuat deskripsi...");
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getBuses().enqueue(new Callback<ApiResponse<List<bus>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<bus>>> call, Response<ApiResponse<List<bus>>> response) {
                Log.d(TAG, "API Response received");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<bus> buses = response.body().getData();
                    Log.d(TAG, "Total buses in response: " + (buses != null ? buses.size() : 0));

                    if (buses != null) {
                        boolean found = false;
                        for (bus b : buses) {
                            Log.d(TAG, "Checking bus ID: " + b.getId() + " vs requested: " + busId);
                            if (b.getId() == busId) {
                                busData = b;
                                found = true;
                                Log.d(TAG, "Found matching bus: " + b.getName());
                                setDataToUI();
                                break;
                            }
                        }

                        if (!found) {
                            Log.e(TAG, "Bus dengan ID " + busId + " tidak ditemukan dalam response");
                            Toast.makeText(getContext(), "Bus tidak ditemukan", Toast.LENGTH_SHORT).show();
                            if (getActivity() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }
                    } else {
                        Log.e(TAG, "Buses list is null");
                        Toast.makeText(getContext(), "Data bus kosong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API Response not successful or empty");
                    String errorMsg = "Gagal memuat data bus";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<bus>>> call, Throwable t) {
                Log.e(TAG, "API Call failed: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Fallback: tampilkan data placeholder
                if (textNamaBus != null) {
                    textNamaBus.setText("Bus ID: " + busId);
                    textHargaBus.setText("Gagal memuat harga");
                    textDeskripsiBus.setText("Tidak dapat memuat data. Periksa koneksi internet.");
                }
            }
        });
    }

    private void setDataToUI() {
        if (busData == null || getContext() == null) {
            Log.e(TAG, "busData or context is null");
            return;
        }

        Log.d(TAG, "Setting data to UI for: " + busData.getName());

        try {
            // Set data ke view
            if (textNamaBus != null) {
                textNamaBus.setText(busData.getName());
            }

            if (textTipeBus != null) {
                String typeInfo = busData.getType_bus();
                if (busData.getCapacity() > 0) {
                    typeInfo += " · Kapasitas " + busData.getCapacity() + " orang";
                }
                textTipeBus.setText(typeInfo);
            }

            if (textHargaBus != null) {
                NumberFormat formatPrice = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                String priceText = formatPrice.format(busData.getPrice()) + " / hari";
                textHargaBus.setText(priceText);
            }

            if (textDeskripsiBus != null) {
                textDeskripsiBus.setText(busData.getDescription() != null ?
                        busData.getDescription() : "Tidak ada deskripsi");
            }

            if (textFasilitasBus != null) {
                if (busData.getFeatures() != null && !busData.getFeatures().isEmpty()) {
                    String facilities = "• " + TextUtils.join("\n• ", busData.getFeatures());
                    textFasilitasBus.setText(facilities);
                } else {
                    textFasilitasBus.setText("• AC\n• TV\n• WiFi");
                }
            }

            if (textRating != null) {
                if (busData.getRating() > 0) {
                    textRating.setText(String.format("%.1f", busData.getRating()));
                } else {
                    textRating.setText("N/A");
                }
            }

            if (textReviews != null) {
                if (busData.getReviews_count() > 0) {
                    textReviews.setText("(" + busData.getReviews_count() + " ulasan)");
                } else {
                    textReviews.setText("(Belum ada ulasan)");
                }
            }

            // Load image
            loadBusImage();

            Log.d(TAG, "UI data set successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error setting UI data: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error menampilkan data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBusImage() {
        try {
            if (busData.getImage() != null && !busData.getImage().isEmpty()) {
                Log.d(TAG, "Loading image for bus: " + busData.getName());

                // Handle Base64 image dari database
                if (busData.getImage().startsWith("data:image")) {
                    String base64Image = busData.getImage().split(",")[1];
                    byte[] img = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(img)
                            .placeholder(R.drawable.sample_bus)
                            .error(R.drawable.sample_bus)
                            .into(imageBus);
                    Log.d(TAG, "Image loaded from Base64");
                } else {
                    // Jika URL langsung
                    Glide.with(requireContext())
                            .load(busData.getImage())
                            .placeholder(R.drawable.sample_bus)
                            .error(R.drawable.sample_bus)
                            .into(imageBus);
                    Log.d(TAG, "Image loaded from URL");
                }
            } else {
                Log.d(TAG, "No image data, using placeholder");
                imageBus.setImageResource(R.drawable.sample_bus);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage(), e);
            imageBus.setImageResource(R.drawable.sample_bus);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Fragment paused");
    }
}