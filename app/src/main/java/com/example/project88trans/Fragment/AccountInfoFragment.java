package com.example.project88trans.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.R;
import com.example.project88trans.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountInfoFragment extends Fragment {

    private TextView tvName, tvRoleOrSubtitle, tvEmail, tvPasswordMasked;
    private View btnAccountInfo, btnChangePassword;
    private View btnBack;

    private String userEmail;
    private static final String TAG = "AccountInfoFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        tvName = view.findViewById(R.id.tvName);
        tvRoleOrSubtitle = view.findViewById(R.id.tvRoleOrSubtitle);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPasswordMasked = view.findViewById(R.id.tvPasswordMasked);
        btnAccountInfo = view.findViewById(R.id.btnAccountInfo);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnBack = view.findViewById(R.id.btnBack);

        // Set default role/subtitle
        tvRoleOrSubtitle.setText("Pengguna");

        // Ambil email user dari SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        userEmail = prefs.getString("email", null);
        Log.d(TAG, "Email dari SharedPreferences: " + userEmail);

        if (userEmail != null) {
            loadUserData(userEmail);
        } else {
            Toast.makeText(getContext(), "User belum login atau email kosong", Toast.LENGTH_SHORT).show();
        }

        // Tombol back
        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Tombol info akun → buka DetailAccountFragment
        btnAccountInfo.setOnClickListener(v -> openDetailAccountFragment());

        // Tombol change password → buka ChangePasswordFragment
        btnChangePassword.setOnClickListener(v -> openChangePasswordFragment());
    }

    private void loadUserData(String email){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<UserResponse.User>> call = api.getUserByEmail(email);

        call.enqueue(new Callback<ApiResponse<UserResponse.User>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse.User>> call, Response<ApiResponse<UserResponse.User>> response) {
                if(response.isSuccessful() && response.body() != null){
                    ApiResponse<UserResponse.User> body = response.body();

                    if(body.isSuccess() && body.getData() != null){
                        UserResponse.User user = body.getData();

                        // Set data ke TextView
                        if(tvName != null) tvName.setText(user.getNama());
                        if(tvEmail != null) tvEmail.setText(user.getEmail());
                        if(tvPasswordMasked != null) tvPasswordMasked.setText("••••••••");

                    } else {
                        Toast.makeText(getContext(), body.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "API gagal: " + body.getMessage());
                    }
                } else {
                    Toast.makeText(getContext(), "Response gagal", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response gagal: code=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse.User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API call failure: ", t);
            }
        });
    }

    private void openDetailAccountFragment(){
        DetailAccountFragment fragment = new DetailAccountFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(android.R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private void openChangePasswordFragment(){
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(android.R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
