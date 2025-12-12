package QuanLy.model;

public class DienThoai extends SanPham implements ICoThue {
    // ✅ THÊM CÁC TRƯỜNG CHI TIẾT
    private String chip;
    private int ram;
    private String congSac;
    
    // ✅ CONSTRUCTOR ĐÃ CẬP NHẬT
    public DienThoai(String ten, String pathAnh, String gia, String hang, String chip, int ram, String congSac) {
        super(ten, pathAnh, gia, "Điện Thoại", hang); // ✅ Truyền "hang" lên lớp cha
        this.chip = chip;
        this.ram = ram;
        this.congSac = congSac;
    }
    
    // ✅ THÊM GETTERS
    public String getChip() { return chip; }
    public int getRam() { return ram; }
    public String getCongSac() { return congSac; }

    @Override
    public double tinhThue(long donGia) {
        // Thuế 10% cho điện thoại
        return donGia * 0.10;
    }
}