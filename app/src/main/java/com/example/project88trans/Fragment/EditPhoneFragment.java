package com.example.project88trans.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project88trans.Activity.ProfileActivity;
import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.model.DefaultResponse;
import com.example.project88trans.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPhoneFragment extends Fragment {

    private EditText etNewPhone;
    private TextView btnSavePhone;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNewPhone = view.findViewById(R.id.etNewPhone);
        btnSavePhone = view.findViewById(R.id.btnSavePhone);
        view.findViewById(R.id.btnBackPhone).setOnClickListener(v -> requireActivity().onBackPressed());

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        etNewPhone.setText(prefs.getString("phone",""));

        btnSavePhone.setOnClickListener(v -> {
            String newPhone = etNewPhone.getText().toString().trim();
            if(newPhone.isEmpty()){
                Toast.makeText(getContext(), "Nomor HP tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            updatePhoneAPI(newPhone);
        });
    }

    private void updatePhoneAPI(String newPhone){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<DefaultResponse> call = api.updateUser(userEmail, null, newPhone); // nama null

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if(response.isSuccessful() && response.body() != null && response.body().success){
                    Toast.makeText(getContext(), "Nomor HP berhasil diperbarui", Toast.LENGTH_SHORT).show();

                    // update SharedPreferences
                    SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
                    prefs.edit().putString("phone", newPhone).apply();

                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Gagal memperbarui nomor HP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
