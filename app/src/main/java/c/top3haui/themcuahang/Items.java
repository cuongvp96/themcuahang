package c.top3haui.themcuahang;

import java.io.Serializable;

/**
 * Created by VanCuong on 28/11/2016.
 */

public class Items implements Serializable {

    private String ten;
    private String loaigas;
    private String motagia;
    private String sdt;
    private String chucuahang;
    private String diadiem;
    private String latlng;
    private String user_id;
    public Items() {
    }

    public Items(String ten, String loaigas, String motagia, String sdt, String chucuahang, String diadiem, String latlng, String user_id) {
        this.ten = ten;
        this.loaigas = loaigas;
        this.motagia = motagia;
        this.sdt = sdt;
        this.chucuahang = chucuahang;
        this.diadiem = diadiem;
        this.latlng = latlng;
        this.user_id = user_id;
    }

    public Items(String ten, String loaigas, String motagia, String sdt, String chucuahang, String latlng, String user_id) {
        this.ten = ten;
        this.loaigas = loaigas;
        this.motagia = motagia;
        this.sdt = sdt;
        this.chucuahang = chucuahang;
        this.latlng = latlng;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getLoaigas() {
        return loaigas;
    }

    public void setLoaigas(String loaigas) {
        this.loaigas = loaigas;
    }

    public String getMotagia() {
        return motagia;
    }

    public void setMotagia(String motagia) {
        this.motagia = motagia;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getChucuahang() {
        return chucuahang;
    }

    public void setChucuahang(String chucuahang) {
        this.chucuahang = chucuahang;
    }

    public String getDiadiem() {
        return diadiem;
    }

    public void setDiadiem(String diadiem) {
        this.diadiem = diadiem;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }
}
