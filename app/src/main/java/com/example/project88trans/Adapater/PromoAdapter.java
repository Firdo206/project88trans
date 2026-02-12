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
import com.example.project88trans.model.Promo;

import java.util.List;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.ViewHolder> {

    Context context;
    List<Promo> list;
    OnPromoClickListener listener;

    public interface OnPromoClickListener { void onPromoClick(Promo promo); }

    public PromoAdapter(Context context, List<Promo> list, OnPromoClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Promo promo = list.get(position);
        holder.tvTitle.setText(promo.getName());
        holder.tvCode.setText("Kode: " + promo.getCode());
        holder.tvDescription.setText("Berlaku " + promo.getStart_date() + " – " + promo.getEnd_date());
        holder.btnGunakan.setOnClickListener(v -> {
            if (listener != null) listener.onPromoClick(promo);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvCode;
        Button btnGunakan;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCode = itemView.findViewById(R.id.tvCode);
            btnGunakan = itemView.findViewById(R.id.btnGunakan);
        }
    }
}
