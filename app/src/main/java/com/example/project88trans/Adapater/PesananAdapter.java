package com.example.project88trans.Adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project88trans.R;
import com.example.project88trans.model.BusRental;
import com.example.project88trans.model.TourRental;

import java.util.ArrayList;
import java.util.List;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananViewHolder> {

    private Context context;
    private List<Object> listPesanan;
    private List<Object> listPesananFull;
    private OnRentalActionListener listener;

    public interface OnRentalActionListener {
        void onPaymentClick(Object rental);
        void onDetailClick(Object rental);
    }



    public void setOnRentalActionListener(OnRentalActionListener listener) {
        this.listener = listener;
    }



    public PesananAdapter(Context context, List<Object> listPesanan) {
        this.context = context;
        this.listPesanan = listPesanan;
        this.listPesananFull = new ArrayList<>(listPesanan);
    }

    @NonNull
    @Override
    public PesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pesanan_card, parent, false);
        return new PesananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesananViewHolder holder, int position) {
        Object obj = listPesanan.get(position);

        String status = "";
        String kode = "";
        String tanggal = "";
        String tanggalSewa = "";
        String info = "";
        String harga = "";

        if (obj instanceof BusRental) {
            BusRental b = (BusRental) obj;
            kode = b.getRentalCode();
            tanggal = b.getDate();
            tanggalSewa = b.getStartDate() + " → " + b.getEndDate();
            info = "Total Hari: " + b.getTotalDays();
            harga = "Rp " + b.getTotalPrice();
            status = b.getStatus() != null ? b.getStatus().toLowerCase() : "";
        } else if (obj instanceof TourRental) {
            TourRental t = (TourRental) obj;
            kode = t.getRentalCode();
            tanggal = t.getDate();
            tanggalSewa = "-"; // TIDAK ada tanggal sewa untuk paket tour
            info = "Jumlah Orang: " + t.getNumberOfPeople();
            harga = "Rp " + t.getTotalPrice();
            status = t.getStatus() != null ? t.getStatus().toLowerCase() : "";
        }

        holder.tvKode.setText("Kode: " + kode);
        holder.tvTanggal.setText("Tanggal: " + tanggal);
        holder.tvSewa.setText("Tanggal Sewa: " + tanggalSewa);
        holder.tvHari.setText(info);
        holder.tvHarga.setText(harga);
        holder.tvStatus.setText(status);

        holder.btnAksi.setVisibility(View.GONE);
        holder.btnDetail.setVisibility(View.GONE);

        switch (status){
            case "pending":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.btnAksi.setVisibility(View.VISIBLE);
                holder.btnAksi.setText("Pilih Metode Pembayaran");
                holder.btnAksi.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onPaymentClick(obj); // kirim rental (BusRental/TourRental) ke activity
                    }
                });

                break;
            case "wait_confirmation":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_wait);
                break;
            case "confirmed":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_confirmed);
                holder.btnDetail.setVisibility(View.VISIBLE);
                holder.btnDetail.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDetailClick(obj); // kirim rental ke activity/fragment
                    }
                });
                break;
            case "completed":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                holder.btnDetail.setVisibility(View.VISIBLE);
                holder.btnDetail.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDetailClick(obj); // kirim rental ke activity/fragment
                    }
                });
                break;
            case "canceled":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_canceled);
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_default);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return listPesanan.size();
    }

    public void filterCategory(String category){
        listPesanan.clear();
        if(category.equals("all")){
            listPesanan.addAll(listPesananFull);
        } else if(category.equals("bus")){
            for(Object obj : listPesananFull){
                if(obj instanceof BusRental) listPesanan.add(obj);
            }
        } else if(category.equals("tour")){
            for(Object obj : listPesananFull){
                if(obj instanceof TourRental) listPesanan.add(obj);
            }
        }
        notifyDataSetChanged();
    }

    static class PesananViewHolder extends RecyclerView.ViewHolder {
        TextView tvKode, tvTanggal,tvSewa, tvHari, tvHarga, tvStatus;
        Button btnAksi, btnDetail;

        public PesananViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKode = itemView.findViewById(R.id.tvKode);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvHari = itemView.findViewById(R.id.tvHari);// tvHari di XML
            tvSewa = itemView.findViewById(R.id.tvSewa);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAksi = itemView.findViewById(R.id.btnAksi);
            btnDetail = itemView.findViewById(R.id.btnDetail);


        }
    }
}
