package QuanLy.model;

public class MayTinhBang extends SanPham {
    // ✅ THÊM CÁC TRƯỜNG CHI TIẾT
    private double sizeScreen;
    private int ram;
    private String congSac;
    
    // ✅ CONSTRUCTOR ĐÃ CẬP NHẬT
    public MayTinhBang(String ten, String pathAnh, String gia, String hang, double sizeScreen, int ram, String congSac) {
        super(ten, pathAnh, gia, "Máy tính bảng", hang); // ✅ Truyền "hang" lên lớp cha
        this.sizeScreen = sizeScreen;
        this.ram = ram;
        this.congSac = congSac;
    }
    
    // ✅ THÊM GETTERS
    public double getSizeScreen() { return sizeScreen; }
    public int getRam() { return ram; }
    public String getCongSac() { return congSac; }
}