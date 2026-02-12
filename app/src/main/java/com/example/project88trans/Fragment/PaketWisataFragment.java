package com.example.project88trans.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Adapater.PaketWisataAdapter;
import com.example.project88trans.R;
import com.example.project88trans.model.PaketWisata;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaketWisataFragment extends Fragment {

    private RecyclerView recyclerView;
    private PaketWisataAdapter adapter;
    private List<PaketWisata> paketList = new ArrayList<>();
    private EditText searchBar;
    private ProgressBar progressBar;
    private View btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_paket_wisata, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPaket);
        searchBar = view.findViewById(R.id.searchBar);
        progressBar = view.findViewById(R.id.progressBar);
        btnBack = view.findViewById(R.id.btnBack);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaketWisataAdapter(getContext(), paketList);
        recyclerView.setAdapter(adapter);

        loadDataFromApi();
        setupSearchBar();

        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        return view;
    }

    private void loadDataFromApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getAllPaketWisata().enqueue(new Callback<ApiResponse<List<PaketWisata>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaketWisata>>> call, Response<ApiResponse<List<PaketWisata>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    paketList.clear();
                    paketList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Gagal memuat data paket wisata", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaketWisata>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        List<PaketWisata> filteredList = new ArrayList<>();
        for (PaketWisata item : paketList) {
            if (item.getNama().toLowerCase().contains(text.toLowerCase()) ||
                    item.getLokasi().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter = new PaketWisataAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);
    }
}
