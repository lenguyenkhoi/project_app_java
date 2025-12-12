// Trong file: dao/DBHelper.java

package dao;

import java.sql.*;
import java.text.DecimalFormat; 
import java.util.*; 

// Import các model của giao diện (QuanLy.model)
import QuanLy.model.SanPham;
import QuanLy.model.Laptop;
import QuanLy.model.DienThoai;
import QuanLy.model.TaiNghe;
import QuanLy.model.MayTinhBang;
// Import model của console (dùng cho saveOrder)
import QuanLy.model.*; 

public class DBHelper {

    // ✅ ĐÃ CẬP NHẬT THEO YÊU CẦU CỦA BẠN
	private static final String URL = "jdbc:sqlserver://KHOI-PC:1433;databaseName=GioHangDB_TX;encrypt=false;sendStringParametersAsUnicode=true;";
	private static final String USER = "sa";
    private static final String PASSWORD = "1234567";

    // 🔹 Hàm lấy kết nối (tái sử dụng nhiều chỗ)
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 🔹 Hàm test kết nối
    public static void main(String[] args) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = getConnection();
            System.out.println("✅ Kết nối thành công đến " + URL);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 	// 🔹 SỬA: Kiểm tra đăng nhập (dùng bảng NguoiDung)
    public static String checkLogin(String username, String password) {
        // SỬA: Dùng NguoiDung, taiKhoan, matKhau
        String checkUserSql = "SELECT matKhau FROM NguoiDung WHERE taiKhoan = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(checkUserSql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    // Không tìm thấy username
                    return "NO_USER";
                }

                String storedPassword = rs.getString("matKhau"); // SỬA: Cột matKhau

                if (!storedPassword.equals(password)) {
                    // Sai mật khẩu
                    return "WRONG_PASS";
                }

                // Đúng username và password
                return "SUCCESS";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    // 🔹 SỬA: Lấy ID (từ NguoiDung)
	public static int getUserIdByUsername(String username) {
	    // SỬA: Dùng NguoiDung, taiKhoan, id
	    String sql = "SELECT id FROM NguoiDung WHERE taiKhoan=?";
	    try (Connection conn = getConnection();	
	         PreparedStatement p = conn.prepareStatement(sql)) {	
	        p.setString(1, username);	
	        try (ResultSet rs = p.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("id"); // SỬA: Cột id
	            }
	        }	
	    } catch (SQLException e) {
	        e.printStackTrace();	
	    }
	    return -1; // không tìm thấy
	}
	
    // 🔹 SỬA: Lấy Tên (từ NguoiDung)
	public static String getFullNameByUsername(String username) {
	    // SỬA: Bảng NguoiDung không có FullName, chúng ta sẽ tạm dùng taiKhoan
	    String sql = "SELECT taiKhoan FROM NguoiDung WHERE taiKhoan=?";
	    try (Connection conn = getConnection();	
	         PreparedStatement p = conn.prepareStatement(sql)) {	
	        p.setString(1, username);	
	        try (ResultSet rs = p.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("taiKhoan"); // SỬA: Trả về taiKhoan
	            }
	        }	
	    } catch (SQLException e) {
	        e.printStackTrace();	
	    }
	    return null; // không tìm thấy
	}

    // 🔹 SỬA: Đăng ký (dùng NguoiDung)
    // (Hàm này cũng cần sửa để đồng bộ, mặc dù dangky.java đang dùng user.java)
    public static boolean registerUser(String username, String password, String phone) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("❌ Tên đăng nhập không được để trống!");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            System.err.println("❌ Mật khẩu không được để trống!");
            return false;
        }
         if (phone == null || phone.trim().isEmpty()) {
            System.err.println("❌ Số điện thoại không được để trống!");
            return false;
        }

        // SỬA: Dùng NguoiDung
        String sql = "INSERT INTO NguoiDung(taiKhoan, matKhau, soDienThoai) VALUES(?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {

            p.setString(1, username.trim());
            p.setString(2, password); // SQL của bạn không yêu cầu trim
            p.setString(3, phone.trim());

            int r = p.executeUpdate();
            return r > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đăng ký (DBHelper): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy TẤT CẢ sản phẩm cho giao diện CuaHang (QuanLy.model)
     */
    public static List<SanPham> getAllProducts() {
        List<SanPham> list = new ArrayList<>();
        // Định dạng giá tiền (Vd: 28,000,000 VNĐ)
        DecimalFormat df = new DecimalFormat("###,###,### VNĐ"); 

        try (Connection conn = DBHelper.getConnection()) {
            // SỬA: Lấy path ảnh từ bảng ProductImages
            String sql = "SELECT p.*, img.ImagePath "
                       + "FROM Products p "
                       + "LEFT JOIN ProductImages img ON p.ProductId = img.ProductId AND img.IsPrimary = 1"; 
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String category = rs.getString("LoaiSP");
                SanPham sp = null;
                
                // Lấy thông tin chung
                String tenSP = rs.getString("TenSP");
                double giaDouble = rs.getDouble("Gia");
                String maSP = rs.getString("MaSP");
                String giaString = df.format(giaDouble);
                
                // SỬA: Lấy ImagePath từ CSDL (đã JOIN)
                String pathAnh = rs.getString("ImagePath");
                
                // SỬA: Nếu CSDL không có ảnh, dùng logic cũ (phòng hờ)
                if (pathAnh == null || pathAnh.isEmpty()) {
                     pathAnh = "/image/" + maSP + ".png"; 
                }

                // Lấy thông tin chi tiết
                String hang = rs.getString("Hang");
                String chip = rs.getString("Chip");
                int ram = rs.getInt("Ram");
                double sizeScreen = rs.getDouble("SizeScreen");
                String congSac = rs.getString("Cổng Sạc");
                String noiSanXuat = rs.getString("Nơi sản xuất");

                // Tạo đối tượng model tương ứng
                switch (category.toLowerCase()) {
                    case "laptop":
                        sp = new Laptop(
                        	    tenSP, pathAnh, giaString,
                        	    hang, chip, ram, sizeScreen
                        );
                        break;
                    case "điện thoại":
                        sp = new DienThoai(
                        	    tenSP, pathAnh, giaString,
                        	    hang, chip, ram, congSac
                        );
                        break;
                    case "tai nghe":
                        sp = new TaiNghe(
	                    	    tenSP, pathAnh, giaString,
	                    	    hang, congSac, noiSanXuat
                        );
                        break;
                    default: // "máy tính bảng"
                        sp = new MayTinhBang(
	                    	    tenSP, pathAnh, giaString,
	                    	    hang, sizeScreen, ram, congSac
                        );
                        break;
                }
                list.add(sp);
            }

        } catch (SQLException e) {
            System.err.println("❌ LỖI NGHIÊM TRỌNG KHI TẢI SẢN PHẨM TỪ DB:");
            System.err.println(e.getMessage());
            e.printStackTrace(); 
        }
        return list;
    }


    // 🔹 Lưu đơn hàng & chi tiết đơn hàng (Dùng cho Main.java console)
    // (Hàm này dùng model cũ, giữ nguyên để không lỗi Main.java)
    public static int saveOrder(int userId, double total, List<OderItemDT0> items) throws SQLException {
        Connection conn = null;
        PreparedStatement pOrder = null;
        PreparedStatement pItem = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Tạo đơn hàng
            String sqlOrder = "INSERT INTO Orders(UserId, Total) OUTPUT INSERTED.OrderId VALUES(?, ?)";
            pOrder = conn.prepareStatement(sqlOrder);
            pOrder.setInt(1, userId);
            pOrder.setDouble(2, total);

            rs = pOrder.executeQuery();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            
            // ✅ SỬA: Cần lấy ProductId từ MaSP (vì OderItemDT0 dùng ProductId)
            // Tuy nhiên, Main.java đang dùng logic cũ (sp.getProductId()).
            // Chúng ta tạm thời giữ nguyên logic này, nhưng nó sẽ
            // thất bại nếu sp.getProductId() không khớp với DB mới.
            // Lý tưởng nhất là Main.java cũng nên được nâng cấp.

            // Thêm chi tiết sản phẩm
            String sqlItem = "INSERT INTO OrderItems(OrderId, ProductCode, Quantity, PriceAtBuy) VALUES(?, (SELECT MaSP FROM Products WHERE ProductId=?), ?, ?)";
            pItem = conn.prepareStatement(sqlItem);
            for (OderItemDT0 it : items) {
                pItem.setInt(1, orderId);
                pItem.setInt(2, it.getProductId()); // ProductId từ model cũ
                pItem.setInt(3, it.getQuantity());
                pItem.setDouble(4, it.getPriceAtBuy());
                pItem.addBatch();
            }
            pItem.executeBatch();

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (pOrder != null) pOrder.close();
            if (pItem != null) pItem.close();
            if (conn != null) conn.close();
        }
    }

    // 🔹 Lưu đơn hàng theo tên sản phẩm (Dùng cho giao diện Swing ThanhToan.java)
    // (Hàm này đã dùng ProductCode (MaSP) nên vẫn hoạt động tốt)
    public static int saveOrderByName(Integer userId,
                                      String receiverName,
                                      String address,
                                      String phone,
                                      String note,
                                      String paymentMethod,
                                      double total,
                                      List<Object[]> cartData) throws SQLException {
        Connection conn = null;
        PreparedStatement pOrder = null;
        PreparedStatement pItem = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sqlOrder = "INSERT INTO Orders(UserId, ReceiverName, Address, Phone, Note, PaymentMethod, Total) "
                    + "OUTPUT INSERTED.OrderId VALUES(?, ?, ?, ?, ?, ?, ?)";
            pOrder = conn.prepareStatement(sqlOrder);
            if (userId == null || userId <= 0) {
                pOrder.setNull(1, Types.INTEGER);
            } else {
                pOrder.setInt(1, userId);
            }
            pOrder.setString(2, receiverName);
            pOrder.setString(3, address);
            pOrder.setString(4, phone);
            pOrder.setString(5, note);
            pOrder.setString(6, paymentMethod);
            pOrder.setDouble(7, total);

            rs = pOrder.executeQuery();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            String sqlItem = "INSERT INTO OrderItems(OrderId, ProductCode, Quantity, PriceAtBuy, TaxAmount) VALUES(?, ?, ?, ?, ?)";
            pItem = conn.prepareStatement(sqlItem);

            for (Object[] row : cartData) {
                // Expect: [String name, Integer qty, String unitPriceStr, String lineTotalStr, String maSP, String taxStr]
                int qty = Integer.parseInt(String.valueOf(row[1]));
                String unitPriceStr = String.valueOf(row[2]).replaceAll("[^0-9]", "");
                double unitPrice = 0;
                try { unitPrice = Double.parseDouble(unitPriceStr); } catch (Exception ignore) {}
                String maSP = row.length >= 5 ? String.valueOf(row[4]) : null;
                double taxAmount = 0;
                if (row.length >= 6) {
                    String taxStr = String.valueOf(row[5]).replaceAll("[^0-9]", "");
                    try { taxAmount = Double.parseDouble(taxStr); } catch (Exception ignore) {}
                }

                pItem.setInt(1, orderId);
                pItem.setString(2, maSP); // Dùng MaSP (ProductCode)
                pItem.setInt(3, qty);
                pItem.setDouble(4, unitPrice);
                pItem.setDouble(5, taxAmount);
                pItem.addBatch();
            }
            pItem.executeBatch();

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (pOrder != null) pOrder.close();
            if (pItem != null) pItem.close();
            if (conn != null) conn.close();
        }
    }

    // 🔹 Lấy danh sách đơn hàng theo UserId (null => tất cả)
    public static List<Map<String, Object>> getOrdersByUserId(Integer userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT OrderId, ISNULL(UserId, 0) AS UserId, ReceiverName, Address, Phone, PaymentMethod, Total, CreatedAt "
                + "FROM Orders "
                + (userId != null && userId > 0 ? "WHERE UserId = ? " : "")
                + "ORDER BY CreatedAt DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null && userId > 0) {
                ps.setInt(1, userId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("OrderId", rs.getInt("OrderId"));
                    m.put("UserId", rs.getInt("UserId"));
                    m.put("ReceiverName", rs.getString("ReceiverName"));
                    m.put("Address", rs.getString("Address"));
                    m.put("Phone", rs.getString("Phone"));
                    m.put("PaymentMethod", rs.getString("PaymentMethod"));
                    m.put("Total", rs.getDouble("Total"));
                    m.put("CreatedAt", rs.getTimestamp("CreatedAt"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 🔹 Lấy danh sách item theo OrderId
    public static List<Object[]> getOrderItemsByOrderId(int orderId) {
        List<Object[]> items = new ArrayList<>();
        String sql = "SELECT ProductCode, Quantity, PriceAtBuy, (Quantity * PriceAtBuy) AS LineTotal, TaxAmount "
                + "FROM OrderItems WHERE OrderId = ? ORDER BY ProductCode";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Object[] {
                        rs.getString("ProductCode"),
                        rs.getInt("Quantity"),
                        rs.getDouble("PriceAtBuy"),
                        rs.getDouble("LineTotal"),
                        rs.getDouble("TaxAmount")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}