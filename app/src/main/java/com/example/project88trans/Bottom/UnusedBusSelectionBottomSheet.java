package com.example.project88trans.Bottom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project88trans.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UnusedBusSelectionBottomSheet extends BottomSheetDialogFragment {

    private final String paketName;

    public UnusedBusSelectionBottomSheet(String paketName) {
        this.paketName = paketName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unused_bottomsheet_bus_selection, container, false);

        TextView txtJudul = view.findViewById(R.id.txtJudul);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroupBus);
        Button btnLanjutBooking = view.findViewById(R.id.btnLanjutBooking);

        txtJudul.setText("Pilih Bus untuk " + paketName);

        btnLanjutBooking.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Pilih jenis bus terlebih dahulu", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selected = view.findViewById(selectedId);
                String pilihanBus = selected.getText().toString();

                Toast.makeText(getContext(),
                        "Paket: " + paketName + "\n" + pilihanBus,
                        Toast.LENGTH_LONG).show();

                dismiss();
            }
        });

        return view;
    }
}
