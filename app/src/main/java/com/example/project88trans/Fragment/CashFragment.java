package com.example.project88trans.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.R;
import com.example.project88trans.model.Payment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashFragment extends Fragment {

    TextView tvInfo;
    MaterialButton btnOke;

    int userId;
    String rentalType;
    int rentalId;
    String promoCode;
    double totalBayar;

    public CashFragment() {}

    public static CashFragment newInstance(int userId, String rentalType, int rentalId, String promoCode, double totalBayar) {
        CashFragment fragment = new CashFragment();
        Bundle args = new Bundle();
        args.putInt("user_id", userId);
        args.putString("rental_type", rentalType);
        args.putInt("rental_id", rentalId);
        args.putString("promo_code", promoCode);
        args.putDouble("total", totalBayar);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cash_fragment, container, false);

        tvInfo = view.findViewById(R.id.tvInfo);
        btnOke = view.findViewById(R.id.btnOke);

        if (getArguments() != null) {
            userId = getArguments().getInt("user_id");
            rentalType = getArguments().getString("rental_type");
            rentalId = getArguments().getInt("rental_id");
            promoCode = getArguments().getString("promo_code");
            totalBayar = getArguments().getDouble("total");
        }

        tvInfo.setText("Silahkan lunasi pembayaran Anda di kantor kami.");

        btnOke.setOnClickListener(v -> updateCashPayment());

        return view;
    }

    private void updateCashPayment() {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        Call<ApiResponse<Payment>> call = api.updatePaymentCash(
                userId, rentalType, rentalId, promoCode, totalBayar, "cash"
        );

        call.enqueue(new Callback<ApiResponse<Payment>>() {
            @Override
            public void onResponse(Call<ApiResponse<Payment>> call, Response<ApiResponse<Payment>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getActivity(), "Status pembayaran diperbarui. Tunggu konfirmasi.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Gagal memperbarui status.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Payment>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
