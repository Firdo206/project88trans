package com.example.project88trans.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

public class TransferFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1001;

    TextView tvNoRek;
    Button btnChooseFile;
    MaterialButton btnKirim;

    Uri fileUri;

    int userId;
    String rentalType;
    int rentalId;
    String promoCode;
    double totalBayar;

    public TransferFragment() {}

    public static TransferFragment newInstance(int userId, String rentalType, int rentalId, String promoCode, double totalBayar) {
        TransferFragment fragment = new TransferFragment();
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
        View view = inflater.inflate(R.layout.transfer_fragment, container, false);

        tvNoRek = view.findViewById(R.id.tvNoRek);
        btnChooseFile = view.findViewById(R.id.btnChooseFile);
        btnKirim = view.findViewById(R.id.btnKirim);

        if (getArguments() != null) {
            userId = getArguments().getInt("user_id");
            rentalType = getArguments().getString("rental_type");
            rentalId = getArguments().getInt("rental_id");
            promoCode = getArguments().getString("promo_code");
            totalBayar = getArguments().getDouble("total");
        }

        tvNoRek.setText("No Rek: 1234567890 (Bank XYZ)");

        btnChooseFile.setOnClickListener(v -> openFileChooser());

        btnKirim.setOnClickListener(v -> {
            if (fileUri == null) {
                Toast.makeText(getActivity(), "Silahkan pilih bukti transfer terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadPayment();
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getData();
            Toast.makeText(getActivity(), "File dipilih", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPayment() {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(fileUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), bytes);
            MultipartBody.Part buktiPart = MultipartBody.Part.createFormData("bukti", "bukti.jpg", reqFile);

            RequestBody rbUserId = RequestBody.create(MultipartBody.FORM, String.valueOf(userId));
            RequestBody rbRentalType = RequestBody.create(MultipartBody.FORM, rentalType);
            RequestBody rbRentalId = RequestBody.create(MultipartBody.FORM, String.valueOf(rentalId));
            RequestBody rbPromo = RequestBody.create(MultipartBody.FORM, promoCode != null ? promoCode : "");
            RequestBody rbPaymentMethod = RequestBody.create(MultipartBody.FORM, "transfer");
            RequestBody rbTotal = RequestBody.create(MultipartBody.FORM, String.valueOf(totalBayar));

            ApiService api = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<Payment>> call = api.updatePaymentWithBukti(
                    rbUserId, rbRentalType, rbRentalId, rbPromo, rbPaymentMethod, rbTotal, buktiPart
            );

            call.enqueue(new Callback<ApiResponse<Payment>>() {
                @Override
                public void onResponse(Call<ApiResponse<Payment>> call, Response<ApiResponse<Payment>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(getActivity(), "Pembayaran berhasil dikirim, tunggu konfirmasi.", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Gagal mengirim pembayaran.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Payment>> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Gagal membaca file.", Toast.LENGTH_SHORT).show();
        }
    }
}
