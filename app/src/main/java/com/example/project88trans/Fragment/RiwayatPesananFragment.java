package com.example.project88trans.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project88trans.Adapater.PesananAdapter;
import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.R;
import com.example.project88trans.model.BusRental;
import com.example.project88trans.model.TourRental;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import android.widget.ImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatPesananFragment extends Fragment {

    private RecyclerView rvPesanan;
    private PesananAdapter adapter;
    private List<Object> listPesanan;
    private ApiService apiService;
    private ChipGroup chipGroupFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_riwayat, container, false);

        rvPesanan = view.findViewById(R.id.rvPesanan);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        ImageView btnBack = view.findViewById(R.id.btnBack);

        rvPesanan.setLayoutManager(new LinearLayoutManager(getContext()));
        listPesanan = new ArrayList<>();
        adapter = new PesananAdapter(getContext(), listPesanan);
        rvPesanan.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        SharedPreferences sp = requireActivity()
                .getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = sp.getInt("user_id", 0);

        adapter.setOnRentalActionListener(new PesananAdapter.OnRentalActionListener() {
            @Override
            public void onPaymentClick(Object rental) {
                // Tidak digunakan di riwayat
            }

            @Override
            public void onDetailClick(Object rental) {
                String type;
                int id;

                if (rental instanceof BusRental) {
                    type = "bus";
                    id = ((BusRental) rental).getId();
                } else if (rental instanceof TourRental) {
                    type = "tour";
                    id = ((TourRental) rental).getId();
                } else {
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                bundle.putString("id", String.valueOf(id));

                InvoiceFragment invoiceFragment = new InvoiceFragment();
                invoiceFragment.setArguments(bundle);

                // Langsung ganti content, tidak pakai fragment_container
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,  // enter
                                R.anim.slide_out_left,  // exit
                                R.anim.slide_in_left,   // popEnter
                                R.anim.slide_out_right  // popExit
                        )
                        .replace(android.R.id.content, invoiceFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }

        });

        // riwayat = tidak ada tombol bayar

        loadRiwayat(userId);

        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipSemua) adapter.filterCategory("all");
            else if (checkedId == R.id.chipBus) adapter.filterCategory("bus");
            else if (checkedId == R.id.chipTour) adapter.filterCategory("tour");
        });

        return view;
    }

    private void loadRiwayat(int userId) {
        listPesanan.clear();
        adapter.notifyDataSetChanged();

        // Bus
        apiService.getBusRentalsByUser(userId).enqueue(new Callback<ApiResponse<List<BusRental>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BusRental>>> call,
                                   Response<ApiResponse<List<BusRental>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (BusRental b : response.body().getData()) {
                        String status = b.getStatus() == null ? "" : b.getStatus().trim().toLowerCase();
                        if (status.equals("completed") || status.equals("canceled")) listPesanan.add(b);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BusRental>>> call, Throwable t) {
                Toast.makeText(requireContext(), "Gagal load bus rentals", Toast.LENGTH_SHORT).show();
                Log.e("RiwayatPesanan", "Bus rental load failed", t);
            }
        });

        // Tour
        apiService.getTourRentalsByUser(userId).enqueue(new Callback<ApiResponse<List<TourRental>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TourRental>>> call,
                                   Response<ApiResponse<List<TourRental>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (TourRental t : response.body().getData()) {
                        String status = t.getStatus() == null ? "" : t.getStatus().trim().toLowerCase();
                        if (status.equals("completed") || status.equals("canceled")) listPesanan.add(t);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TourRental>>> call, Throwable t) {
                Toast.makeText(requireContext(), "Gagal load tour rentals", Toast.LENGTH_SHORT).show();
                Log.e("RiwayatPesanan", "Tour rental load failed", t);
            }
        });
    }
}
