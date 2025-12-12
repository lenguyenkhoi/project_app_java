package QuanLy.model;

public class Laptop extends SanPham {
    // ✅ THÊM CÁC TRƯỜNG CHI TIẾT
    private String chip;
    private int ram;
    private double sizeScreen;
    
    // ✅ CONSTRUCTOR ĐÃ CẬP NHẬT
    public Laptop(String ten, String pathAnh, String gia, String hang, String chip, int ram, double sizeScreen) {
        super(ten, pathAnh, gia, "Laptop", hang); // ✅ Truyền "hang" lên lớp cha
        this.chip = chip;
        this.ram = ram;
        this.sizeScreen = sizeScreen;
    }

    // ✅ THÊM GETTERS
    public String getChip() { return chip; }
    public int getRam() { return ram; }
    public double getSizeScreen() { return sizeScreen; }
}