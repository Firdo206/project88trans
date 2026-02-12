package com.example.project88trans.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.R;
import com.example.project88trans.model.InvoiceData;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceFragment extends Fragment {

    private TextView tvNama, tvTelp, tvEmail;
    private TextView tvDetail1, tvDetail2, tvDetail3, tvDetail4;
    private TextView tvHarga, tvPromo, tvTotal, tvMetode, tvKodeTransaksi;
    private Button btnDownload;

    private InvoiceData dataInvoice;
    private String type;
    private int id;

    public InvoiceFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invoice, container, false);

        Bundle args = getArguments();
        if (args != null) {
            type = args.getString("type", "bus");
            try { id = Integer.parseInt(args.getString("id", "0")); }
            catch (Exception e) { id = 0; }
        } else {
            type = "bus"; id = 0;
        }

        tvNama = v.findViewById(R.id.tvNama);
        tvTelp = v.findViewById(R.id.tvTelp);
        tvEmail = v.findViewById(R.id.tvEmail);

        tvDetail1 = v.findViewById(R.id.tvDetail1);
        tvDetail2 = v.findViewById(R.id.tvDetail2);
        tvDetail3 = v.findViewById(R.id.tvDetail3);
        tvDetail4 = v.findViewById(R.id.tvDetail4);

        tvMetode = v.findViewById(R.id.tvMetode);
        tvHarga = v.findViewById(R.id.tvHarga);
        tvPromo = v.findViewById(R.id.tvPromo);
        tvTotal = v.findViewById(R.id.tvTotal);
        tvKodeTransaksi = v.findViewById(R.id.tvInvoiceTitle);

        btnDownload = v.findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(v2 -> {
            if (dataInvoice == null) {
                Toast.makeText(getContext(), "Data belum siap", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                File pdfFile = generateTempPdf();
                saveToDownloadFolder(pdfFile);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Gagal membuat PDF", Toast.LENGTH_SHORT).show();
            }
        });

        loadUserData();

        if (id != 0) loadInvoiceRetrofit();

        return v;
    }

    private void loadUserData() {
        SharedPreferences sp = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        tvNama.setText(sp.getString("nama", "-"));
        tvEmail.setText(sp.getString("email", "-"));
        tvTelp.setText(sp.getString("phone", "-"));
    }

    private void loadInvoiceRetrofit() {
        ApiService service = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<InvoiceData>> call = service.getInvoice(type, String.valueOf(id));
        call.enqueue(new Callback<ApiResponse<InvoiceData>>() {
            @Override
            public void onResponse(Call<ApiResponse<InvoiceData>> call, Response<ApiResponse<InvoiceData>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<InvoiceData> res = response.body();
                if (!res.isSuccess()) {
                    Toast.makeText(getContext(), res.getMessage() != null ? res.getMessage() : "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    return;
                }

                InvoiceData d = res.getData();
                if (d == null) { Toast.makeText(getContext(), "Data kosong", Toast.LENGTH_SHORT).show(); return; }
                dataInvoice = d;

                tvNama.setText(nullSafe(d.getUserName()));
                tvEmail.setText(nullSafe(d.getEmail()));
                tvTelp.setText(nullSafe(d.getPhoneNumber()));

                tvKodeTransaksi.setText("Kode Transaksi: " + nullSafe(d.getRentalCode()));

                if ("bus".equals(type)) {
                    tvDetail1.setText("Jenis Bus : " + nullSafe(d.getBusType()));
                    tvDetail2.setText("Tanggal Berangkat : " + nullSafe(d.getStartDate()));
                    tvDetail3.setText("Tanggal Kembali : " + nullSafe(d.getEndDate()));
                    tvDetail4.setText("Tujuan : " + nullSafe(d.getDestination()));
                } else {
                    tvDetail1.setText("Paket Tour : " + nullSafe(d.getTourName()));
                    tvDetail2.setText("Durasi : " + (d.getDurationDays() == null ? "-" : d.getDurationDays() + " hari"));
                    tvDetail3.setText("Jumlah Orang : " + (d.getPeople() == null ? "-" : d.getPeople()));
                    tvDetail4.setText("Tanggal : " + nullSafe(d.getStartDate()));
                }

                tvMetode.setText(nullSafe(d.getPaymentMethod()));
                tvHarga.setText("Rp " + formatRupiah(d.getOriginalPrice()));
                tvPromo.setText(d.getPromoAmount() == 0 ? "-" : "- Rp " + formatRupiah(d.getPromoAmount()));
                tvTotal.setText("Rp " + formatRupiah(d.getFinalPrice()));
            }

            @Override
            public void onFailure(Call<ApiResponse<InvoiceData>> call, Throwable t) {
                Log.e("Invoice", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Koneksi gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String nullSafe(String s) { return s == null || s.isEmpty() ? "-" : s; }
    private String formatRupiah(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.US));
        return formatter.format(number).replace(",", ".");
    }

    private File generateTempPdf() throws Exception {
        InvoiceData d = dataInvoice;
        File cacheDir = requireContext().getCacheDir();
        String filename = "Invoice_" + System.currentTimeMillis() + ".pdf";
        File file = new File(cacheDir, filename);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font subFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font headerTableFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

        Paragraph title = new Paragraph("IND's 88 TRANS", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("E-TICKET / INVOICE", subFont));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Kode Transaksi: " + nullSafe(d.getRentalCode()), normalFont));
        document.add(new Paragraph("\n"));

        PdfPTable tableUser = new PdfPTable(2);
        tableUser.setWidthPercentage(100);
        tableUser.setWidths(new int[]{1, 2});
        PdfPCell userHeader = new PdfPCell(new Paragraph("Data Penumpang", headerTableFont));
        userHeader.setBackgroundColor(new BaseColor(220, 220, 220));
        userHeader.setColspan(2);
        tableUser.addCell(userHeader);

        tableUser.addCell("Nama"); tableUser.addCell(nullSafe(d.getUserName()));
        tableUser.addCell("Nomor Telepon"); tableUser.addCell(nullSafe(d.getPhoneNumber()));
        tableUser.addCell("Email"); tableUser.addCell(nullSafe(d.getEmail()));
        document.add(tableUser);

        document.add(new Paragraph("\n"));
        PdfPTable tableTrip = new PdfPTable(2);
        tableTrip.setWidthPercentage(100);
        tableTrip.setWidths(new int[]{1, 2});
        PdfPCell tripHeader = new PdfPCell(new Paragraph("Detail Perjalanan", headerTableFont));
        tripHeader.setBackgroundColor(new BaseColor(220, 220, 220));
        tripHeader.setColspan(2); tableTrip.addCell(tripHeader);

        if ("bus".equals(type)) {
            tableTrip.addCell("Jenis Bus"); tableTrip.addCell(nullSafe(d.getBusType()));
            tableTrip.addCell("Tanggal Berangkat"); tableTrip.addCell(nullSafe(d.getStartDate()));
            tableTrip.addCell("Tanggal Kembali"); tableTrip.addCell(nullSafe(d.getEndDate()));
            tableTrip.addCell("Tujuan"); tableTrip.addCell(nullSafe(d.getDestination()));
        } else {
            tableTrip.addCell("Nama Tour"); tableTrip.addCell(nullSafe(d.getTourName()));
            tableTrip.addCell("Durasi Tour"); tableTrip.addCell(d.getDurationDays() == null ? "-" : d.getDurationDays() + " hari");
            tableTrip.addCell("Jumlah Orang"); tableTrip.addCell(d.getPeople() == null ? "-" : String.valueOf(d.getPeople()));
            tableTrip.addCell("Tanggal Mulai"); tableTrip.addCell(nullSafe(d.getStartDate()));
        }
        document.add(tableTrip);

        document.add(new Paragraph("\n"));
        PdfPTable tablePay = new PdfPTable(2);
        tablePay.setWidthPercentage(100);
        tablePay.setWidths(new int[]{1, 2});
        PdfPCell payHeader = new PdfPCell(new Paragraph("Detail Pembayaran", headerTableFont));
        payHeader.setBackgroundColor(new BaseColor(220, 220, 220));
        payHeader.setColspan(2); tablePay.addCell(payHeader);

        tablePay.addCell("Metode Pembayaran"); tablePay.addCell(nullSafe(d.getPaymentMethod()));
        tablePay.addCell("Harga Awal"); tablePay.addCell("Rp " + formatRupiah(d.getOriginalPrice()));
        tablePay.addCell("Diskon / Promo"); tablePay.addCell(d.getPromoAmount() == 0 ? "-" : "- Rp " + formatRupiah(d.getPromoAmount()));
        tablePay.addCell("Total Bayar"); tablePay.addCell("Rp " + formatRupiah(d.getFinalPrice()));
        tablePay.addCell("Status"); tablePay.addCell("LUNAS");

        document.add(tablePay);
        document.add(new Paragraph("\n\nTerima kasih telah menggunakan layanan kami.", normalFont));
        document.close();

        return file;
    }

    private void saveToDownloadFolder(File tempFile) {
        try {
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloads.exists()) downloads.mkdirs();

            File outFile = new File(downloads, tempFile.getName());

            InputStream fis = new FileInputStream(tempFile);
            OutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[4096]; int length;
            while ((length = fis.read(buffer)) > 0) { fos.write(buffer, 0, length); }
            fis.close(); fos.close();

            MediaScannerConnection.scanFile(getContext(), new String[]{ outFile.getAbsolutePath() },
                    new String[]{"application/pdf"}, null);

            Toast.makeText(getContext(), "PDF tersimpan di folder Download", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
