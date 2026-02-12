package com.example.project88trans.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project88trans.Adapater.PromoAdapter;
import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Bottom.PromoBottomSheet;
import com.example.project88trans.R;
import com.example.project88trans.model.Promo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllPromoFragment extends Fragment {

    private RecyclerView rvPromo;
    private PromoAdapter promoAdapter;
    private List<Promo> promoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_promo, container, false);

        rvPromo = view.findViewById(R.id.rvPromo);
        rvPromo.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter, klik item langsung buka BottomSheet
        promoAdapter = new PromoAdapter(getContext(), promoList, promo -> {
            PromoBottomSheet sheet = PromoBottomSheet.newInstance(
                    promo.getName(),
                    promo.getCode(),
                    promo.getAmount(),
                    promo.getStart_date(),
                    promo.getEnd_date(),
                    promo.getSlot()
            );
            sheet.show(getParentFragmentManager(), "PromoSheet");
        });

        rvPromo.setAdapter(promoAdapter);

        loadAllPromo();

        return view;
    }

    private void loadAllPromo() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllPromo().enqueue(new Callback<ApiResponse<List<Promo>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Promo>>> call,
                                   Response<ApiResponse<List<Promo>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    promoList.clear();
                    promoList.addAll(response.body().getData());
                    promoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Promo>>> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal memuat promo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

