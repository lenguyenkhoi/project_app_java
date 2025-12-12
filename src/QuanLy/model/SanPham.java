package QuanLy.model;

public class SanPham {
    private String ten;
    private String pathAnh;
    private String gia;
    private String loai; 
    private String hang; // ✅ ĐÃ THÊM

    // ✅ CONSTRUCTOR ĐÃ CẬP NHẬT
    public SanPham(String ten, String pathAnh, String gia, String loai, String hang) {
        this.ten = ten;
        this.pathAnh = pathAnh;
        this.gia = gia;
        this.loai = loai;
        this.hang = hang; // ✅
    }
    
    // Getters
    public String getTen() { return ten; }
    public String getPathAnh() { return pathAnh; }
    public String getGia() { return gia; }
    public String getLoai() { return loai; }
    public String getHang() { return hang; } // ✅
}