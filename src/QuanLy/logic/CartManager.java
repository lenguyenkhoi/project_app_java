package QuanLy.logic;

import QuanLy.model.SanPham;
import QuanLy.model.ICoThue;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý giỏ hàng chung cho toàn bộ ứng dụng
 * Sử dụng Singleton pattern để đảm bảo chỉ có một instance
 */
public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    
    // Lớp nội để lưu thông tin sản phẩm trong giỏ hàng
    public static class CartItem {
        private SanPham sanPham;
        private int soLuong;
        private String variant; // Màu sắc hoặc biến thể đã chọn
        
        public CartItem(SanPham sanPham, int soLuong, String variant) {
            this.sanPham = sanPham;
            this.soLuong = soLuong;
            this.variant = variant;
        }
        
        public SanPham getSanPham() { return sanPham; }
        public int getSoLuong() { return soLuong; }
        public String getVariant() { return variant; }
        public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
        
        // Tính thành tiền cho item này (bao gồm thuế nếu có)
        public long getThanhTien() {
            try {
                // Chuyển đổi giá từ string "32.000.000 VNĐ" sang số
                if (sanPham == null || sanPham.getGia() == null) {
                    System.err.println("❌ Lỗi: SanPham hoặc giá là null");
                    return 0;
                }
                String giaStr = sanPham.getGia().replaceAll("[^0-9]", "");
                if (giaStr.isEmpty()) {
                    System.err.println("❌ Lỗi: Không thể parse giá từ: " + sanPham.getGia());
                    return 0;
                }
                long donGia = Long.parseLong(giaStr);
                long tongTien = donGia * soLuong;
                
                // ✅ Tính thuế nếu sản phẩm implement ICoThue (ví dụ: Điện thoại)
                if (sanPham instanceof ICoThue) {
                    ICoThue coThue = (ICoThue) sanPham;
                    double thue = coThue.tinhThue(donGia);
                    long thueLong = Math.round(thue * soLuong); // Thuế cho toàn bộ số lượng
                    tongTien += thueLong;
                }
                
                return tongTien;
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi tính thành tiền: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        }
        
        // Tính thuế riêng cho item này (để hiển thị riêng)
        public long getThue() {
            try {
                if (sanPham == null || sanPham.getGia() == null) {
                    return 0;
                }
                String giaStr = sanPham.getGia().replaceAll("[^0-9]", "");
                if (giaStr.isEmpty()) {
                    return 0;
                }
                long donGia = Long.parseLong(giaStr);
                
                // ✅ Tính thuế nếu sản phẩm implement ICoThue
                if (sanPham instanceof ICoThue) {
                    ICoThue coThue = (ICoThue) sanPham;
                    double thue = coThue.tinhThue(donGia);
                    return Math.round(thue * soLuong); // Thuế cho toàn bộ số lượng
                }
                return 0;
            } catch (Exception e) {
                return 0;
            }
        }
        
        // Lấy đơn giá
        public long getDonGia() {
            try {
                if (sanPham == null || sanPham.getGia() == null) {
                    System.err.println("❌ Lỗi: SanPham hoặc giá là null");
                    return 0;
                }
                String giaStr = sanPham.getGia().replaceAll("[^0-9]", "");
                if (giaStr.isEmpty()) {
                    System.err.println("❌ Lỗi: Không thể parse giá từ: " + sanPham.getGia());
                    return 0;
                }
                return Long.parseLong(giaStr);
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi lấy đơn giá: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        }
    }
    
    private CartManager() {
        cartItems = new ArrayList<>();
    }
    
    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     * Nếu sản phẩm đã tồn tại (cùng tên và variant), tăng số lượng
     */
    public void addToCart(SanPham sanPham, int soLuong, String variant) {
        if (sanPham == null) {
            System.err.println("❌ Lỗi: Không thể thêm sản phẩm null vào giỏ hàng");
            return;
        }
        
        System.out.println("✅ Thêm vào giỏ: " + sanPham.getTen() + " x " + soLuong + " (" + variant + ")");
        
        // Tìm xem sản phẩm đã có trong giỏ chưa
        for (CartItem item : cartItems) {
            if (item.getSanPham().getTen().equals(sanPham.getTen()) 
                && item.getVariant().equals(variant)) {
                // Tăng số lượng
                item.setSoLuong(item.getSoLuong() + soLuong);
                System.out.println("✅ Đã tăng số lượng. Tổng: " + item.getSoLuong());
                return;
            }
        }
        // Nếu chưa có, thêm mới
        cartItems.add(new CartItem(sanPham, soLuong, variant));
        System.out.println("✅ Đã thêm mới. Tổng items trong giỏ: " + cartItems.size());
    }
    
    /**
     * Xóa một item khỏi giỏ hàng
     */
    public void removeItem(CartItem item) {
        cartItems.remove(item);
    }
    
    /**
     * Lấy danh sách tất cả items trong giỏ hàng
     */
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems); // Trả về copy để an toàn
    }
    
    /**
     * Tính tổng tiền giỏ hàng (bao gồm thuế)
     */
    public long getTotalAmount() {
        long total = 0;
        for (CartItem item : cartItems) {
            total += item.getThanhTien(); // getThanhTien() đã bao gồm thuế
        }
        return total;
    }
    
    /**
     * Tính tổng tiền trước thuế
     */
    public long getTotalBeforeTax() {
        long total = 0;
        for (CartItem item : cartItems) {
            long donGia = item.getDonGia();
            total += donGia * item.getSoLuong();
        }
        return total;
    }
    
    /**
     * Tính tổng thuế
     */
    public long getTotalTax() {
        long totalTax = 0;
        for (CartItem item : cartItems) {
            totalTax += item.getThue();
        }
        return totalTax;
    }
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    public void clearCart() {
        cartItems.clear();
    }
    
    /**
     * Kiểm tra giỏ hàng có trống không
     */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
    
    /**
     * Lấy số lượng sản phẩm trong giỏ hàng
     */
    public int getTotalItems() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getSoLuong();
        }
        return count;
    }
}

