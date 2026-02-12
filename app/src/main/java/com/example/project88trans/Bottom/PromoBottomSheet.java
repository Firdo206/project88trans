package com.example.project88trans.Bottom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project88trans.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PromoBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CODE = "code";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_START = "start";
    private static final String ARG_END = "end";
    private static final String ARG_SLOT = "slot";

    public static PromoBottomSheet newInstance(String title, String code, int amount,
                                               String start, String end, int slot) {
        PromoBottomSheet fragment = new PromoBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CODE, code);
        args.putInt(ARG_AMOUNT, amount);
        args.putString(ARG_START, start);
        args.putString(ARG_END, end);
        args.putInt(ARG_SLOT, slot);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo_bottomsheet, container, false);

        TextView tvTitle = view.findViewById(R.id.tvPromoTitle);
        TextView tvCode = view.findViewById(R.id.tvPromoCode);
        TextView tvInfo = view.findViewById(R.id.tvPromoInfo);
        TextView tvTerms = view.findViewById(R.id.tvPromoTerms);
        ImageView btnSalin = view.findViewById(R.id.btnSalin);
        TextView tvDiscount = view.findViewById(R.id.tvPromoDiscount);

        if (getArguments() != null) {
            // Ambil semua data dari bundle
            String title = getArguments().getString(ARG_TITLE);
            String code = getArguments().getString(ARG_CODE);
            int amount = getArguments().getInt(ARG_AMOUNT);
            String start = getArguments().getString(ARG_START);
            String end = getArguments().getString(ARG_END);
            int slot = getArguments().getInt(ARG_SLOT);

            // Set data ke view
            tvTitle.setText(title);
            tvCode.setText(code);
            tvDiscount.setText(amount + "%");
            tvInfo.setText("Berlaku: " + start + " – " + end);
            tvTerms.setText(
                    "• Berlaku untuk semua paket wisata\n" +
                            "• Mendapatkan diskon sebesar " + amount + "%\n" +
                            "• Slot hanya tersisa " + slot
            );

            // Tombol salin kode promo
            btnSalin.setOnClickListener(v -> {
                android.content.ClipboardManager clipboard =
                        (android.content.ClipboardManager) requireContext().getSystemService(requireContext().CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Promo Code", code);
                clipboard.setPrimaryClip(clip);
            });
        }

        return view;
    }
}
