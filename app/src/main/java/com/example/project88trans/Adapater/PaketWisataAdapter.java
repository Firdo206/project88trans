package com.example.project88trans.Adapater;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project88trans.Activity.BookingActivity;
import com.example.project88trans.model.PaketWisata;
import com.example.project88trans.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PaketWisataAdapter extends RecyclerView.Adapter<PaketWisataAdapter.ViewHolder> {

    private final Context context;
    private final java.util.List<PaketWisata> paketList;

    public PaketWisataAdapter(Context context, java.util.List<PaketWisata> paketList) {
        this.context = context;
        this.paketList = paketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_paket_wisata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaketWisata paket = paketList.get(position);

        // --- Data utama ---
        holder.tvTitle.setText(paket.getNama());
        holder.tvLocation.setText(paket.getLokasi() + " • " + paket.getDurasi());
        holder.tvRating.setText(String.valueOf(paket.getRating()));
        holder.tvReviews.setText("(" + paket.getJumlahUlasan() + " ulasan)");

        // --- Harga ---
        NumberFormat formatter = new DecimalFormat("#,###");
        holder.tvHarga.setText("Rp " + formatter.format(paket.getHargaPromo()));
        holder.tvHargaCoret.setText("Rp " + formatter.format(paket.getHargaAsli()));
        holder.tvHargaCoret.setPaintFlags(holder.tvHargaCoret.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

        // --- Gambar ---
        if (paket.getGambarUrl() != null && !paket.getGambarUrl().isEmpty()) {
            byte[] decoded = Base64.decode(paket.getGambarUrl(), Base64.DEFAULT);
            Glide.with(context)
                    .asBitmap()
                    .load(decoded)
                    .placeholder(R.drawable.yogyakarta_tugu)
                    .into(holder.imgPaket);
        } else {
            holder.imgPaket.setImageResource(R.drawable.yogyakarta_tugu);
        }

        // --- Destinasi utama ---
        holder.containerDestinasi.removeAllViews();
        if (paket.getDestinasiUtama() != null) {
            for (String dest : paket.getDestinasiUtama()) {
                Chip chip = new Chip(context);
                chip.setText(dest);
                chip.setChipBackgroundColorResource(R.color.primary_red);
                chip.setTextColor(context.getResources().getColor(R.color.white));
                chip.setClickable(false);
                chip.setCheckable(false);
                holder.containerDestinasi.addView(chip);
            }
        }

        // --- Fasilitas ---
        holder.containerFasilitas.removeAllViews();
        if (paket.getFasilitas() != null) {
            for (String fas : paket.getFasilitas()) {
                Chip chip = new Chip(context);
                chip.setText(fas);
                chip.setChipBackgroundColorResource(R.color.light_gray);
                chip.setTextColor(context.getResources().getColor(R.color.black));
                chip.setClickable(false);
                chip.setCheckable(false);
                holder.containerFasilitas.addView(chip);
            }
        }

        // --- Tambahkan chip kapasitas ---
        Chip chipCapacity = new Chip(context);
        chipCapacity.setText("Kapasitas: " + paket.getFixedCapacity() + " orang");
        chipCapacity.setChipBackgroundColorResource(R.color.light_gray);
        chipCapacity.setTextColor(context.getResources().getColor(R.color.black));
        chipCapacity.setClickable(false);
        chipCapacity.setCheckable(false);
        holder.containerFasilitas.addView(chipCapacity);

        // --- Button Booking ---
        holder.btnBooking.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingActivity.class);

            // Kirim tipe paket
            intent.putExtra("type", "tour");

            // Kirim data paket
            intent.putExtra("package_id", paket.getId());
            intent.putExtra("nama_tour", paket.getNama());
            intent.putExtra("durasi", paket.getDurasi());
            intent.putExtra("price", paket.getHargaPromo());
            intent.putExtra("fixed_capacity", paket.getFixedCapacity());

            // Kirim list destinasi & fasilitas
            if (paket.getDestinasiUtama() != null)
                intent.putStringArrayListExtra("destinasi_utama", new ArrayList<>(paket.getDestinasiUtama()));
            if (paket.getFasilitas() != null)
                intent.putStringArrayListExtra("fasilitas", new ArrayList<>(paket.getFasilitas()));

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return paketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPaket;
        TextView tvTitle, tvLocation, tvRating, tvReviews, tvHarga, tvHargaCoret;
        ChipGroup containerDestinasi, containerFasilitas;
        Button btnBooking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPaket = itemView.findViewById(R.id.imgPaket);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvDuration);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviews = itemView.findViewById(R.id.tvReviews);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvHargaCoret = itemView.findViewById(R.id.tvHargacoret);
            containerDestinasi = itemView.findViewById(R.id.containerDestinasi);
            containerFasilitas = itemView.findViewById(R.id.containerFasilitas);
            btnBooking = itemView.findViewById(R.id.btnBooking);
        }
    }
}
