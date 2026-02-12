package com.example.project88trans.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaketWisata {

    private int id;
    private String nama;
    private String lokasi;
    private String durasi;

    @SerializedName("fixed_price")
    private double hargaAsli;

    @SerializedName("harga_promo")
    private double hargaPromo;

    private double rating;

    @SerializedName("jumlah_ulasan")
    private int jumlahUlasan;

    @SerializedName("image")
    private String gambarUrl;

    @SerializedName("destinasi_utama")
    private List<String> destinasiUtama;

    @SerializedName("detail_destinasi")
    private List<String> detailDestinasi;

    private List<String> fasilitas;

    @SerializedName("fixed_capacity")
    private int fixedCapacity;

    // Constructor
    public PaketWisata(int id, String nama, String lokasi, String durasi, double hargaAsli,
                       double hargaPromo, double rating, int jumlahUlasan, String gambarUrl,
                       List<String> destinasiUtama, List<String> detailDestinasi, List<String> fasilitas,
                       int fixedCapacity) {
        this.id = id;
        this.nama = nama;
        this.lokasi = lokasi;
        this.durasi = durasi;
        this.hargaAsli = hargaAsli;
        this.hargaPromo = hargaPromo;
        this.rating = rating;
        this.jumlahUlasan = jumlahUlasan;
        this.gambarUrl = gambarUrl;
        this.destinasiUtama = destinasiUtama;
        this.detailDestinasi = detailDestinasi;
        this.fasilitas = fasilitas;
        this.fixedCapacity = fixedCapacity;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getDurasi() { return durasi; }
    public void setDurasi(String durasi) { this.durasi = durasi; }

    public double getHargaAsli() { return hargaAsli; }
    public void setHargaAsli(double hargaAsli) { this.hargaAsli = hargaAsli; }

    public double getHargaPromo() { return hargaPromo; }
    public void setHargaPromo(double hargaPromo) { this.hargaPromo = hargaPromo; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getJumlahUlasan() { return jumlahUlasan; }
    public void setJumlahUlasan(int jumlahUlasan) { this.jumlahUlasan = jumlahUlasan; }

    public String getGambarUrl() { return gambarUrl; }
    public void setGambarUrl(String gambarUrl) { this.gambarUrl = gambarUrl; }

    public List<String> getDestinasiUtama() { return destinasiUtama; }
    public void setDestinasiUtama(List<String> destinasiUtama) { this.destinasiUtama = destinasiUtama; }

    public List<String> getDetailDestinasi() { return detailDestinasi; }
    public void setDetailDestinasi(List<String> detailDestinasi) { this.detailDestinasi = detailDestinasi; }

    public List<String> getFasilitas() { return fasilitas; }
    public void setFasilitas(List<String> fasilitas) { this.fasilitas = fasilitas; }

    public int getFixedCapacity() { return fixedCapacity; }
    public void setFixedCapacity(int fixedCapacity) { this.fixedCapacity = fixedCapacity; }
}
