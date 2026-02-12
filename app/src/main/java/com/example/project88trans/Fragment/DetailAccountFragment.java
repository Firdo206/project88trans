package com.example.project88trans.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project88trans.Activity.ProfileActivity;
import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.R;
import com.example.project88trans.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailAccountFragment extends Fragment {

    private EditText etEmail, etNama, etPhone;
    private ImageButton btnEditNama, btnEditPhone;
    private ProgressBar progressBar;
    private Button btnSave;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.etEmail);
        etNama = view.findViewById(R.id.etNama);
        etPhone = view.findViewById(R.id.etPhone);
        btnEditNama = view.findViewById(R.id.btnEditNama);
        btnEditPhone = view.findViewById(R.id.btnEditPhone);
        progressBar = view.findViewById(R.id.progressBar);
        btnSave = view.findViewById(R.id.btnSave);

        // Tombol back
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Ambil email dari SharedPreferences
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            userEmail = prefs.getString("email", null);

            if (userEmail != null && !userEmail.isEmpty()) {
                etEmail.setText(userEmail); // email readonly
                loadUserData();
            } else {
                Toast.makeText(getContext(), "Email user tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        }

        // Tombol edit nama & phone
        btnEditNama.setOnClickListener(v -> {
            if (getActivity() instanceof ProfileActivity) {
                ((ProfileActivity) getActivity()).openEditNamaFragment();
            }
        });

        btnEditPhone.setOnClickListener(v -> {
            if (getActivity() instanceof ProfileActivity) {
                ((ProfileActivity) getActivity()).openEditPhoneFragment();
            }
        });

        // Tombol simpan
        btnSave.setOnClickListener(v -> {
            // Implementasi update data user
            Toast.makeText(getContext(), "Fitur simpan akan diimplementasi", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "Email tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<UserResponse.User>> call = api.getUserByEmail(userEmail);

        call.enqueue(new Callback<ApiResponse<UserResponse.User>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse.User>> call, Response<ApiResponse<UserResponse.User>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse.User> body = response.body();
                    if (body.isSuccess() && body.getData() != null) {
                        UserResponse.User user = body.getData();
                        etNama.setText(user.getNama());
                        etPhone.setText(user.getPhone());
                    } else {
                        Toast.makeText(getContext(), body.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal memuat data user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse.User>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}