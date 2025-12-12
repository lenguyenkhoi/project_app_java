package QuanLy.model;

public class TaiNghe extends SanPham {
    // ✅ THÊM CÁC TRƯỜNG CHI TIẾT
    private String congSac;
    private String noiSanXuat;

    // ✅ CONSTRUCTOR ĐÃ CẬP NHẬT
    public TaiNghe(String ten, String pathAnh, String gia, String hang, String congSac, String noiSanXuat) {
        super(ten, pathAnh, gia, "Tai Nghe", hang); // ✅ Truyền "hang" lên lớp cha
        this.congSac = congSac;
        this.noiSanXuat = noiSanXuat;
    }
    
    // ✅ THÊM GETTERS
    public String getCongSac() { return congSac; }
    public String getNoiSanXuat() { return noiSanXuat; }
}