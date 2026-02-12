package com.example.project88trans.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project88trans.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class HelpBottomSheetFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //WA
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        LinearLayout btnWhatsapp = view.findViewById(R.id.btnWhatsApp);
        btnWhatsapp.setOnClickListener(v -> {
            String phoneNumber = "6282230725758";
            String message = "Halo CS, saya butuh bantuan terkait bus 88Trans.";
            String url = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message);

            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
        });
        //Email
        LinearLayout btnEmail = view.findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@travelyuk.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bantuan bus 88Trans");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Halo CS, saya ingin menanyakan tentang...");
            startActivity(emailIntent);
        });
        //TLP
        LinearLayout btnHotline = view.findViewById(R.id.btnHotline);
        btnHotline.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:0211234567"));
            startActivity(callIntent);
        });

        return view;
    }
}
