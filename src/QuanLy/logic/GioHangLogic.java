package QuanLy.logic;
import java.util.ArrayList;
import java.util.List;
// import QuanLy.model.SanPham; // Không cần import vì đã cùng package
import QuanLy.giohang.GioHang; // Chỉ import lớp cần thiết
import QuanLy.logic.CartManager; // Import đúng file CartManager
import QuanLy.dto.CheckoutData; // Import đúng file CheckoutData
import QuanLy.model.SanPham; // Đảm bảo trỏ đến QuanLy.model

/**
 * Lớp này chứa logic xử lý cho Giao diện Giỏ Hàng (GioHang.java).
 * Nó giao tiếp với CartManager (Model) và tính toán dữ liệu cho GioHang (View).
 */
public class GioHangLogic {

    private CartManager cartManager;

    public GioHangLogic() {
        this.cartManager = CartManager.getInstance();
    }

    /**
     * Lấy danh sách các sản phẩm hiện có trong giỏ hàng.
     */
    public List<CartManager.CartItem> getCartItems() {
        return cartManager.getCartItems();
    }

    /**
     * Kiểm tra xem giỏ hàng có trống không.
     */
    public boolean isCartEmpty() {
        return cartManager.isEmpty();
    }

    /**
     * Xóa một sản phẩm khỏi CartManager.
     */
    public void removeItem(CartManager.CartItem item) {
        if (item != null) {
            cartManager.removeItem(item);
        }
    }

    /**
     * Tính toán tổng tiền dựa trên các panel sản phẩm đang được chọn trong GUI.
     * @param productPanels Danh sách các GUI panel sản phẩm từ GioHang.java
     * @return Một chuỗi HTML đã định dạng để hiển thị trên totalLabel.
     */
    // THAY ĐỔI: Phải sử dụng tên đầy đủ "GioHang.ProductPanel"
    public String calculateTotal(List<GioHang.ProductPanel> productPanels) {
        long totalAmount = 0;
        long totalTax = 0;
        int selectedCount = 0;
        
        // Lặp qua các GUI panels
        for (GioHang.ProductPanel p : productPanels) {
            // Chỉ tính tiền các panel được check
            if (p.isSelected()) {
                CartManager.CartItem item = p.getCartItem();
                if (item != null) {
                    selectedCount++;
                    totalAmount += item.getThanhTien(); // Đã bao gồm thuế
                    totalTax += item.getThue();     // Thuế riêng
                }
            }
        }

        // Định dạng chuỗi HTML trả về
        if (totalTax > 0) {
            long totalBeforeTax = totalAmount - totalTax;
            return String.format(
                "<html>Tổng cộng (%d loại sản phẩm):<br/>" +
                "Tổng tiền: <b>%,dđ</b><br/>" +
                "Thuế (10%% cho điện thoại): <b style='color:blue;'>%,dđ</b><br/>" +
                "<b style='color:#EE4D2D; font-size:16px;'>Tổng thanh toán: %,dđ</b></html>", 
                selectedCount,
                totalBeforeTax,
                totalTax,
                totalAmount
            );
        } else {
            return String.format(
                "<html>Tổng cộng (%d loại sản phẩm): <b style='color:#EE4D2D;'>%,dđ</b></html>", 
                selectedCount, 
                totalAmount
            );
        }
    }

    /**
     * Chuẩn bị dữ liệu cho trang ThanhToan.
     * @param productPanels Danh sách các GUI panel sản phẩm từ GioHang.java
     * @return Một đối tượng CheckoutData chứa danh sách hàng và tổng tiền.
     */
    // THAY ĐỔI: Phải sử dụng tên đầy đủ "GioHang.ProductPanel"
    public CheckoutData prepareCheckoutData(List<GioHang.ProductPanel> productPanels) {
        List<Object[]> cartData = new ArrayList<>();
        long totalAmount = 0;

        for (GioHang.ProductPanel p : productPanels) {
            if (p.isSelected()) {
                CartManager.CartItem ci = p.getCartItem();
                if (ci == null) continue;

                SanPham sp = ci.getSanPham(); // SanPham đã cùng package
                if (sp == null) continue;

                String tenSP = sp.getTen();
                int soLuong = ci.getSoLuong();
                long donGia = ci.getDonGia();
                long thanhTien = ci.getThanhTien(); // Đã bao gồm thuế
                long thueItem = ci.getThue();
                
                // Lấy Mã SP từ path ảnh (ví dụ: /anh/DT01.png -> DT01)
                String maSP = "";
                try {
                    String path = sp.getPathAnh();
                    int slash = path.lastIndexOf('/') >= 0 ? path.lastIndexOf('/') : path.lastIndexOf('\\');
                    String file = slash >= 0 ? path.substring(slash + 1) : path;
                    int dot = file.lastIndexOf('.');
                    maSP = (dot > 0) ? file.substring(0, dot) : file;
                } catch (Exception ignore) {}

                cartData.add(new Object[]{
                    tenSP, 
                    soLuong, 
                    String.format("%,d", donGia),
                    String.format("%,d", thanhTien - thueItem), // Thành tiền (chưa thuế)
                    maSP,
                    String.format("%,d", thueItem)
                });
                
                totalAmount += thanhTien;
            }
        }
        
        // Trả về đối tượng CheckoutData (cùng package)
        return new CheckoutData(cartData, totalAmount);
    }
}

