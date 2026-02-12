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

public class EditNamaFragment extends Fragment {

    private EditText etNewNama;
    private TextView btnSaveNama;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_nama, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNewNama = view.findViewById(R.id.etNewNama);
        btnSaveNama = view.findViewById(R.id.btnSaveNama);
        view.findViewById(R.id.btnBackNama).setOnClickListener(v -> requireActivity().onBackPressed());

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        etNewNama.setText(prefs.getString("nama",""));

        btnSaveNama.setOnClickListener(v -> {
            String newNama = etNewNama.getText().toString().trim();
            if(newNama.isEmpty()){
                Toast.makeText(getContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            updateNamaAPI(newNama);
        });
    }

    private void updateNamaAPI(String newNama){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<DefaultResponse> call = api.updateUser(userEmail, newNama, null); // phone null

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if(response.isSuccessful() && response.body() != null && response.body().success){
                    Toast.makeText(getContext(), "Nama berhasil diperbarui", Toast.LENGTH_SHORT).show();

                    // update SharedPreferences
                    SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
                    prefs.edit().putString("nama", newNama).apply();

                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Gagal memperbarui nama", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
