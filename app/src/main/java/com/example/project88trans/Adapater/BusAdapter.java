package com.example.project88trans.Adapater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project88trans.Activity.BookingActivity;
import com.example.project88trans.R;
import com.example.project88trans.model.bus;

import java.util.ArrayList;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> implements Filterable {

    private Context context;
    private List<bus> busList;
    private List<bus> fullList;
    private OnItemClick listener;

    public interface OnItemClick {
        void onClick(int busId);
        void onBookingClick(bus busItem); // Tambahkan interface untuk booking
    }

    public BusAdapter(Context context, List<bus> busList, OnItemClick listener) {
        this.context = context;
        this.busList = busList;
        this.fullList = new ArrayList<>(busList);
        this.listener = listener;
    }

    public void setData(List<bus> newList) {
        busList.clear();
        busList.addAll(newList);
        fullList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bus_card, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        bus b = busList.get(position);

        holder.tvName.setText(b.getName());
        holder.tvDesc.setText(b.getType_bus());

        // Item click untuk detail
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(b.getId());
            }
        });

        // Booking button click
        holder.btnBooking.setOnClickListener(v -> {
            handleBookingClick(b);
        });
    }

    @Override
    public int getItemCount() {
        return busList != null ? busList.size() : 0;
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvDesc;
        public Button btnBooking; // Tambahkan button booking

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBus);
            tvDesc = itemView.findViewById(R.id.tvBusDesc);
            btnBooking = itemView.findViewById(R.id.btnBooking); // Pastikan ada di layout
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence keyword) {
            List<bus> result = new ArrayList<>();

            if (keyword == null || keyword.length() == 0) {
                result.addAll(fullList);
            } else {
                String filter = keyword.toString().toLowerCase().trim();
                for (bus b : fullList) {
                    if (b.getName().toLowerCase().contains(filter) ||
                            b.getType_bus().toLowerCase().contains(filter)) {
                        result.add(b);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = result;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            busList.clear();
            if (results.values != null) {
                busList.addAll((List<bus>) results.values);
            }
            notifyDataSetChanged();
        }
    };

    private void handleBookingClick(bus busItem) {
        // Cek ketersediaan bus
        if (busItem.getStatus() != null && !busItem.getStatus().equalsIgnoreCase("available")) {
            Toast.makeText(context, "Bus tidak tersedia untuk booking", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pilihan 1: Kirim melalui interface callback
        if (listener != null) {
            listener.onBookingClick(busItem);
            return;
        }

        // Pilihan 2: Langsung start activity dari adapter
        Intent intent = new Intent(context, BookingActivity.class);

        // Kirim tipe booking
        intent.putExtra("type", "bus");

        // Kirim data bus
        intent.putExtra("bus_id", busItem.getId());
        intent.putExtra("bus_name", busItem.getName());
        intent.putExtra("bus_price", busItem.getPrice());
        intent.putExtra("bus_type", busItem.getType_bus());
        intent.putExtra("bus_capacity", busItem.getCapacity());

        // Data tambahan jika ada
        if (busItem.getDescription() != null) {
            intent.putExtra("bus_description", busItem.getDescription());
        }

        if (busItem.getFeatures() != null && !busItem.getFeatures().isEmpty()) {
            intent.putStringArrayListExtra("bus_features", new ArrayList<>(busItem.getFeatures()));
        }

        intent.putExtra("bus_rating", busItem.getRating());
        intent.putExtra("bus_reviews_count", busItem.getReviews_count());

        context.startActivity(intent);
    }
}