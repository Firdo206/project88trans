package com.example.project88trans.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private ImageButton btnToggleOldPassword, btnToggleNewPassword, btnToggleConfirmPassword;
    private Button btnChangePassword;
    private ImageButton btnBack;

    private boolean isOldVisible = false, isNewVisible = false, isConfirmVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        btnToggleOldPassword = view.findViewById(R.id.btnToggleOldPassword);
        btnToggleNewPassword = view.findViewById(R.id.btnToggleNewPassword);
        btnToggleConfirmPassword = view.findViewById(R.id.btnToggleConfirmPassword);

        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if(getActivity()!=null) getActivity().onBackPressed();
        });

        btnToggleOldPassword.setOnClickListener(v -> isOldVisible = toggleVisibility(etOldPassword, btnToggleOldPassword, isOldVisible));
        btnToggleNewPassword.setOnClickListener(v -> isNewVisible = toggleVisibility(etNewPassword, btnToggleNewPassword, isNewVisible));
        btnToggleConfirmPassword.setOnClickListener(v -> isConfirmVisible = toggleVisibility(etConfirmPassword, btnToggleConfirmPassword, isConfirmVisible));

        btnChangePassword.setOnClickListener(v -> validateAndChangePassword());
    }

    private boolean toggleVisibility(EditText et, ImageButton btn, boolean visible){
        if(visible){
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btn.setImageResource(R.drawable.ic_visibility_off);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btn.setImageResource(R.drawable.ic_visibility);
        }
        et.setSelection(et.getText().length());
        return !visible;
    }

    private void validateAndChangePassword(){
        String oldPwd = etOldPassword.getText().toString().trim();
        String newPwd = etNewPassword.getText().toString().trim();
        String confirmPwd = etConfirmPassword.getText().toString().trim();

        if(oldPwd.isEmpty()){ etOldPassword.setError("Password lama kosong"); etOldPassword.requestFocus(); return; }
        if(newPwd.isEmpty()){ etNewPassword.setError("Password baru kosong"); etNewPassword.requestFocus(); return; }
        if(confirmPwd.isEmpty()){ etConfirmPassword.setError("Konfirmasi password kosong"); etConfirmPassword.requestFocus(); return; }
        if(newPwd.length()<8){ etNewPassword.setError("Minimal 8 karakter"); etNewPassword.requestFocus(); return; }
        if(!isValidPassword(newPwd)){ etNewPassword.setError("Harus ada huruf besar, kecil & angka"); etNewPassword.requestFocus(); return; }
        if(!newPwd.equals(confirmPwd)){ etConfirmPassword.setError("Password tidak cocok"); etConfirmPassword.requestFocus(); return; }
        if(oldPwd.equals(newPwd)){ etNewPassword.setError("Tidak boleh sama dengan password lama"); etNewPassword.requestFocus(); return; }

        changePassword(oldPwd,newPwd);
    }

    private boolean isValidPassword(String pwd){
        boolean hasUpper=false, hasLower=false, hasDigit=false;
        for(char c : pwd.toCharArray()){
            if(Character.isUpperCase(c)) hasUpper=true;
            if(Character.isLowerCase(c)) hasLower=true;
            if(Character.isDigit(c)) hasDigit=true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    private void changePassword(String oldPwd, String newPwd){
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE);
        String email = prefs.getString("email", null);
        if(email==null){ Toast.makeText(getContext(),"Email user tidak ditemukan",Toast.LENGTH_SHORT).show(); return; }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Void>> call = api.changePassword(email, oldPwd, newPwd);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if(response.isSuccessful() && response.body()!=null){
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if(response.body().isSuccess() && getActivity()!=null) getActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Gagal mengubah password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
