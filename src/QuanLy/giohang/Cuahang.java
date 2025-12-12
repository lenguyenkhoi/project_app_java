// Trong file: QuanLy/giohang/Cuahang.java

package QuanLy.giohang;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.image.BufferedImage;
import QuanLy.model.*;
// ✅ Thêm import này
import dao.DBHelper; 
import QuanLy.logic.*;

// Bạn cần đảm bảo các lớp SanPham, DienThoai, Laptop, TaiNghe, MayTinhBang được định nghĩa và import
// ví dụ: import QuanLy.model.*;

public class Cuahang extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel centerPanel;
    private JPanel topPanel; // ✅ Lưu reference để có thể repaint
    private JButton selectedCategoryButton = null; // Lưu nút đang được chọn
    
    // Khai báo danh sách sản phẩm và trạng thái sắp xếp
    private java.util.List<SanPham> danhSachSanPham;
    private String loaiHienTai = null; // Loại sản phẩm đang được lọc (null là tất cả)
    // private boolean isSortAscending = false; // Bỏ biến này vì đã dùng CheckBox
    private JTextField txtSearch; // Khai báo toàn cục để dễ dàng truy cập từ nút Tìm kiếm

    // Khai báo CheckBox để truy cập trong các hàm khác
    JCheckBox chkThapCao;
    JCheckBox chkCaoThap;
    
    // ✅ Quản lý đăng nhập
    private JButton btnLogin;
    private JButton btnLogout;
    private JLabel lblUserName;
    private JButton btnHistory;


    // SỬA: Hàm main này nên khởi chạy dangnhap.java
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // SỬA: Khởi chạy dangnhap làm cửa sổ đầu tiên
                dangnhap loginDialog = new dangnhap(null);
                loginDialog.setVisible(true);
                
                // Cuahang frame = new Cuahang(); // Bỏ dòng này
                // frame.setVisible(true); // Bỏ dòng này
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Cuahang() {
        // --- Cấu hình cửa sổ ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Cửa hàng");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBounds(100, 100, 900, 600);

        // --- Panel chính ---
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        // === THANH TRÊN ===
        topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Ô tìm kiếm ---
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        searchContainer.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(500, 35));

        txtSearch = new JTextField(); // Sử dụng biến toàn cục
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 15));
        txtSearch.setToolTipText("Nhập sản phẩm cần tìm...");

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setFont(new Font("Arial", Font.BOLD, 13));
        btnSearch.setBackground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                // Nếu ô tìm kiếm trống, hiển thị lại theo bộ lọc loại hiện tại (nếu có)
                hienThiSanPhamTheoLoai(loaiHienTai); 
            } else {
                // Thực hiện tìm kiếm theo từ khóa
                hienThiSanPhamTheoTuKhoa(keyword);
            }
        });

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        searchContainer.add(searchPanel);

        // --- Nút giỏ hàng và đăng nhập ---
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        ImageIcon cartIcon = null;
        try {
            // ✅ BƯỚC MỚI: Tải trực tiếp từ ổ đĩa D
            // THAY THẾ ĐƯỜNG DẪN NÀY BẰNG ĐƯỜNG DẪN THỰC TẾ TRÊN MÁY BẠN
            java.io.File cartFile = new java.io.File("D:\\image\\giohang3.png");
            
            if (cartFile.exists() && cartFile.isFile()) {
                System.out.println("✅ Đã nạp ảnh giỏ hàng từ ổ đĩa D: " + cartFile.getAbsolutePath());
                
                // Chuyển File thành URL để ImageIcon có thể nạp
                java.net.URL cartUrl = cartFile.toURI().toURL(); 
                cartIcon = new ImageIcon(cartUrl);
                
                // Thay đổi kích thước (giữ nguyên logic cũ)
                Image scaledImg = cartIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                cartIcon = new ImageIcon(scaledImg);
            } else {
                throw new Exception("Không tìm thấy ảnh tại: " + cartFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("❌ KHÔNG TÌM THẤY ẢNH GIỎ HÀNG. Dùng icon mặc định. Lỗi: " + e.getMessage());
            // Dùng ảnh dự phòng (ảnh trong suốt)
            cartIcon = new ImageIcon(new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB));
        }


        JButton btnCart = new JButton(cartIcon);
        btnCart.setBackground(Color.WHITE);
        btnCart.setFocusPainted(false);
        btnCart.setToolTipText("Xem giỏ hàng");
        btnCart.addActionListener(e -> {
            // SỬA: Yêu cầu đăng nhập mới cho xem giỏ hàng
            if (!UserManager.getInstance().isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để xem giỏ hàng!", "Yêu cầu đăng nhập", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
        	GioHang gioHangFrame = new GioHang(this); // ✅ Truyền parentFrame
        	gioHangFrame.setVisible(true);
        	gioHangFrame.setLocationRelativeTo(null);
        });

        // ✅ Nút đăng nhập (sẽ ẩn khi đã login)
        btnLogin = new JButton("Đăng nhập/Đăng ký");
        btnLogin.setBackground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(e -> {
            // SỬA: Đóng cửa hàng hiện tại và mở cửa sổ đăng nhập
            this.dispose();
        	dangnhap dn = new dangnhap(this);
        	dn.setVisible(true);
        });

        // ✅ Label hiển thị tên người dùng (sẽ ẩn khi chưa login)
        lblUserName = new JLabel("");
        lblUserName.setFont(new Font("Arial", Font.BOLD, 14));
        lblUserName.setForeground(new Color(0, 102, 204));
        lblUserName.setVisible(false);
        
        // ✅ Nút đăng xuất (sẽ ẩn khi chưa login)
        btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(Color.RED);
        btnLogout.setFocusPainted(false);
        btnLogout.setVisible(false);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserManager.getInstance().logout();
                updateLoginUI();
                JOptionPane.showMessageDialog(this, "Đã đăng xuất thành công!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                
                // SỬA: Đóng cửa hàng và mở lại màn hình đăng nhập
                this.dispose();
                dangnhap dn = new dangnhap(null);
                dn.setVisible(true);
            }
        });

        // Nút lịch sử đơn hàng
        btnHistory = new JButton("Lịch sử đơn hàng");
        btnHistory.setBackground(Color.WHITE);
        btnHistory.setFocusPainted(false);
        btnHistory.addActionListener(e -> {
            LichSuDonHang ls = new LichSuDonHang();
            ls.setVisible(true);
        });

        rightPanel.add(btnCart);
        rightPanel.add(btnHistory);
        rightPanel.add(lblUserName);
        rightPanel.add(btnLogin);
        rightPanel.add(btnLogout);
        
        // ✅ Cập nhật UI ban đầu
        updateLoginUI();

        topPanel.add(searchContainer, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // === THÂN CHÍNH ===
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 248, 255));
        contentPane.add(mainPanel, BorderLayout.CENTER);

        // --- BÊN TRÁI: BỘ LỌC ---
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setBackground(new Color(230, 230, 250));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel lblBoLoc = new JLabel("BỘ LỌC TÌM KIẾM", SwingConstants.LEFT);
        lblBoLoc.setFont(new Font("Times New Roman", Font.BOLD, 22));
        lblBoLoc.setForeground(new Color(0, 102, 204));
        lblBoLoc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBoLoc.setBorder(new EmptyBorder(20, 0, 10, 0));
        leftPanel.add(lblBoLoc);

        // --- Loại sản phẩm ---
        JLabel lblLoaiSP = new JLabel("Loại sản phẩm");
        lblLoaiSP.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLoaiSP.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(lblLoaiSP);
        leftPanel.add(Box.createVerticalStrut(10));

        // Danh sách loại sản phẩm
        String[] loaiSP = {"Điện Thoại", "Laptop", "Tai Nghe", "Máy tính bảng"};
        java.util.List<JButton> categoryButtons = new ArrayList<>();
        
        // Thêm nút "Tất cả"
        JButton btnAll = new JButton("Tất cả Sản phẩm");
        btnAll.setFocusPainted(false);
        btnAll.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Mặc định chọn
        btnAll.setBackground(new Color(204, 229, 255));
        btnAll.setForeground(new Color(0, 102, 204));
        btnAll.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAll.setMaximumSize(new Dimension(180, 35));
        btnAll.setBorder(new LineBorder(new Color(0, 102, 204), 2));
        selectedCategoryButton = btnAll; // Đặt nút này là nút mặc định được chọn
        categoryButtons.add(btnAll);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(btnAll);
        
        btnAll.addActionListener(e -> {
            // Hủy chọn các nút khác
            for (JButton other : categoryButtons) {
                other.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                other.setBackground(Color.WHITE);
                other.setForeground(Color.BLACK);
                other.setBorder(new LineBorder(new Color(180, 180, 180)));
            }
            // Đánh dấu nút đang chọn
            btnAll.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btnAll.setBackground(new Color(204, 229, 255));
            btnAll.setForeground(new Color(0, 102, 204));
            btnAll.setBorder(new LineBorder(new Color(0, 102, 204), 2));
            selectedCategoryButton = btnAll;
            
            loaiHienTai = null; // Đặt loại hiện tại là null để hiển thị tất cả
            txtSearch.setText(""); // Xóa từ khóa tìm kiếm
            hienThiSanPhamTheoLoai(loaiHienTai);
        });


        for (String ten : loaiSP) {
            JButton btn = new JButton(ten);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setBackground(Color.WHITE);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 35));
            btn.setBorder(new LineBorder(new Color(180, 180, 180)));

            btn.addActionListener(e -> {
                // Hủy chọn các nút khác
                for (JButton other : categoryButtons) {
                    other.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    other.setBackground(Color.WHITE);
                    other.setForeground(Color.BLACK);
                    other.setBorder(new LineBorder(new Color(180, 180, 180)));
                }
                // Đánh dấu nút đang chọn
                btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
                btn.setBackground(new Color(204, 229, 255));
                btn.setForeground(new Color(0, 102, 204));
                btn.setBorder(new LineBorder(new Color(0, 102, 204), 2));

                selectedCategoryButton = btn;
                
                // Cập nhật loại hiện tại và hiển thị
                loaiHienTai = ten;
                txtSearch.setText(""); // Xóa từ khóa tìm kiếm
                hienThiSanPhamTheoLoai(loaiHienTai);
            });

            categoryButtons.add(btn);
            leftPanel.add(Box.createVerticalStrut(8));
            leftPanel.add(btn);
        }

        leftPanel.add(Box.createVerticalStrut(25));
         	
        // --- Xếp theo giá ---
        JLabel lblGia = new JLabel("Xếp theo giá");
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGia.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(lblGia);
        leftPanel.add(Box.createVerticalStrut(10));

        JPanel priceSortPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        priceSortPanel.setOpaque(false);
        priceSortPanel.setMaximumSize(new Dimension(200, 80));

        chkThapCao = new JCheckBox("Thấp → Cao");
        chkCaoThap = new JCheckBox("Cao → Thấp");

        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        chkThapCao.setFont(font);
        chkCaoThap.setFont(font);

        chkThapCao.setOpaque(false);
        chkCaoThap.setOpaque(false);

        // Logic sắp xếp Tăng dần
        chkThapCao.addActionListener(e -> {
            if (chkThapCao.isSelected()) {
                chkCaoThap.setSelected(false);
                hienThiSanPhamTheoTuKhoa(txtSearch.getText().trim()); // Gọi lại hàm tìm kiếm/lọc để sắp xếp
            } else {
                hienThiSanPhamTheoTuKhoa(txtSearch.getText().trim()); // Về trạng thái không sắp xếp
            }
        });

        // Logic sắp xếp Giảm dần
        chkCaoThap.addActionListener(e -> {
            if (chkCaoThap.isSelected()) {
                chkThapCao.setSelected(false);
                hienThiSanPhamTheoTuKhoa(txtSearch.getText().trim()); // Gọi lại hàm tìm kiếm/lọc để sắp xếp
            } else {
                hienThiSanPhamTheoTuKhoa(txtSearch.getText().trim()); // Về trạng thái không sắp xếp
            }
        });

        priceSortPanel.add(chkThapCao);
        priceSortPanel.add(chkCaoThap);
        leftPanel.add(priceSortPanel);

        leftPanel.add(Box.createVerticalGlue());
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // ----------------- PANEL TRUNG TÂM -----------------
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 3, 15, 15));
        centerPanel.setBackground(new Color(245, 245, 255));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel scrollContent = new JPanel(new BorderLayout());
        scrollContent.add(centerPanel, BorderLayout.NORTH); // Quan trọng! dùng NORTH để giữ nguyên chiều cao theo nội dung

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // --- ✅ SỬA: KHỞI TẠO DANH SÁCH SẢN PHẨM TỪ DATABASE ---
        try {
            danhSachSanPham = DBHelper.getAllProducts();
            System.out.println("✅ Đã tải " + danhSachSanPham.size() + " sản phẩm từ DB.");
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi DB, tạo danh sách rỗng để tránh NullPointerException
            danhSachSanPham = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải sản phẩm từ Database:\n" + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
     
        // Hiển thị tất cả sản phẩm ban đầu
        hienThiSanPhamTheoLoai(loaiHienTai); 

        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * ✅ Cập nhật UI dựa trên trạng thái đăng nhập
     */
    public void updateLoginUI() {
        UserManager userManager = UserManager.getInstance();
        
        if (userManager.isLoggedIn()) {
            // Đã đăng nhập: ẩn nút login, hiện tên user và nút logout
            btnLogin.setVisible(false);
            lblUserName.setText("Xin chào, " + userManager.getFullName() + "!");
            lblUserName.setVisible(true);
            btnLogout.setVisible(true);
            if (btnHistory != null) btnHistory.setVisible(true);
        } else {
            // Chưa đăng nhập: hiện nút login, ẩn tên user và nút logout
            btnLogin.setVisible(true);
            lblUserName.setVisible(false);
            btnLogout.setVisible(false);
            if (btnHistory != null) btnHistory.setVisible(false);
        }
        
        // Repaint để cập nhật UI
        if (topPanel != null) {
            topPanel.revalidate();
            topPanel.repaint();
        }
    }
    
    // ----------------- HÀM TIỆN ÍCH CHUYỂN ĐỔI GIÁ TIỀN -----------------
    /**
     * Chuyển đổi chuỗi giá tiền (vd: "32.000.000 VNĐ") thành số nguyên để so sánh.
     */
    private long chuyenDoiGiaThanhSo(String gia) {
        try {
            // Loại bỏ dấu chấm, khoảng trắng và ký hiệu tiền tệ
            String soSach = gia.replaceAll("[^0-9]", "");
            return Long.parseLong(soSach);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi chuyển đổi giá tiền: " + gia);
            return 0; // Trả về 0 nếu có lỗi
        }
    }

    // ----------------- HÀM LỌC SẢN PHẨM THEO TỪ KHÓA (ĐÃ CẬP NHẬT) -----------------
    /**
     * Lọc danh sách sản phẩm theo từ khóa, sau đó áp dụng bộ lọc loại sản phẩm hiện tại
     * và gọi hàm hiển thị để sắp xếp và trình bày.
     * @param keyword Từ khóa tìm kiếm (Tên sản phẩm)
     */
    private void hienThiSanPhamTheoTuKhoa(String keyword) {
        // 1. Lọc theo từ khóa trên DANH SÁCH GỐC
        List<SanPham> danhSachLocKeyword = new ArrayList<>();
        String kwLower = keyword.toLowerCase();
        for (SanPham sp : danhSachSanPham) {
            
            // =================== THAY ĐỔI TẠI ĐÂY ===================
            // Kiểm tra xem từ khóa có trong TÊN hoặc trong LOẠI sản phẩm không
            if (sp.getTen().toLowerCase().contains(kwLower) || 
                sp.getLoai().toLowerCase().contains(kwLower)) {
                danhSachLocKeyword.add(sp);
            }
            // ========================================================

        }
        
        // 2. Lọc tiếp theo LOẠI HIỆN TẠI (nếu có)
        List<SanPham> danhSachCuoiCung = new ArrayList<>();
        if (loaiHienTai != null) {
            for (SanPham sp : danhSachLocKeyword) {
                if (sp.getLoai().equalsIgnoreCase(loaiHienTai)) {
                    danhSachCuoiCung.add(sp);
                }
            }
        } else {
            // Nếu không có loại nào được chọn, danh sách cuối cùng là danh sách lọc theo từ khóa
            danhSachCuoiCung = danhSachLocKeyword;
        }

        // 3. Hiển thị danh sách cuối cùng (áp dụng sắp xếp)
        hienThiDanhSachLoc(danhSachCuoiCung);
    }

    // ----------------- HÀM LỌC SẢN PHẨM THEO LOẠI (CẬP NHẬT) -----------------
    /**
     * Lọc danh sách sản phẩm theo loại (sau khi loại bỏ tìm kiếm), sau đó gọi hàm hiển thị.
     * @param loai Loại sản phẩm cần lọc ("Điện Thoại", "Laptop", v.v.) hoặc null để hiển thị tất cả.
     */
    private void hienThiSanPhamTheoLoai(String loai) {
        // 1. Lọc theo loại trên DANH SÁCH GỐC
        List<SanPham> danhSachLoc = new ArrayList<>();
        if (loai == null) {
            danhSachLoc.addAll(danhSachSanPham);
        } else {
            for (SanPham sp : danhSachSanPham) {
                if (sp.getLoai().equalsIgnoreCase(loai)) {
                    danhSachLoc.add(sp);
                }
            }
        }

        // 2. Hiển thị danh sách đã lọc (áp dụng sắp xếp)
        hienThiDanhSachLoc(danhSachLoc);
    }
    
    // ----------------- HÀM HIỂN THỊ VÀ SẮP XẾP CUỐI CÙNG (MỚI) -----------------
    /**
     * Áp dụng sắp xếp giá và hiển thị danh sách sản phẩm đã được lọc lên centerPanel.
     * @param danhSachLoc Danh sách sản phẩm đã được lọc (theo loại hoặc từ khóa).
     */
    private void hienThiDanhSachLoc(List<SanPham> danhSachLoc) {
        // 1. Xóa tất cả sản phẩm hiện tại
        centerPanel.removeAll();

        // 2. Xử lý Sắp xếp
        if (chkThapCao.isSelected() || chkCaoThap.isSelected()) {
            Comparator<SanPham> giaComparator = (sp1, sp2) -> {
                long gia1 = chuyenDoiGiaThanhSo(sp1.getGia());
                long gia2 = chuyenDoiGiaThanhSo(sp2.getGia());
                return Long.compare(gia1, gia2);
            };

            if (chkThapCao.isSelected()) {
                // Sắp xếp Tăng dần (Thấp -> Cao)
                Collections.sort(danhSachLoc, giaComparator);
            } else if (chkCaoThap.isSelected()) {
                // Sắp xếp Giảm dần (Cao -> Thấp)
                Collections.sort(danhSachLoc, Collections.reverseOrder(giaComparator));
            }
        } 

        // 3. Hiển thị lại các sản phẩm
        for (SanPham sp : danhSachLoc) {
            themSanPham(sp.getTen(), sp.getPathAnh(), sp.getGia());
        }
        
        // 4. Cập nhật giao diện
        centerPanel.revalidate();
        centerPanel.repaint();

        // Hiển thị thông báo tìm kiếm nếu có
        if (!txtSearch.getText().trim().isEmpty()) {
            System.out.println("Tìm thấy " + danhSachLoc.size() + " sản phẩm cho từ khóa: " + txtSearch.getText().trim());
        }
    }


    // ----------------- HÀM TẠO 1 SẢN PHẨM -----------------
    private void themSanPham(String ten, String pathAnh, String gia) {
        JButton btnSanPham = new JButton();
        btnSanPham.setLayout(new BorderLayout());
        btnSanPham.setFocusPainted(false);
        btnSanPham.setBackground(Color.WHITE);
        btnSanPham.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        btnSanPham.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

     // Ảnh sản phẩm — có kiểm tra null để tránh lỗi
        ImageIcon icon;
        
        // SỬA: Dùng phương thức tải ảnh ổn định hơn
        java.net.URL imgUrl = null;
        if (pathAnh != null) {
             // 1. Kiểm tra xem đây là URL (từ DB mới)
             if (pathAnh.toLowerCase().startsWith("http")) {
                 try {
                     imgUrl = new java.net.URL(pathAnh);
                 } catch (java.net.MalformedURLException e) {
                     System.out.println("⚠ URL ảnh không hợp lệ: " + pathAnh);
                     imgUrl = null;
                 }
             } 
             
             // 2. Kiểm tra Classpath Resource (Dành cho ảnh được đóng gói, ví dụ: /image/sp1.png)
             // Bạn giữ nguyên logic Classpath cũ
             if (imgUrl == null && pathAnh.startsWith("/")) {
                try {
                    // Xóa dấu / ở đầu để ClassLoader tìm từ root
                    String resourcePath = pathAnh.substring(1); 
                    imgUrl = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
                } catch (Exception e) {
                    // Bỏ qua lỗi Classpath
                }
             }
             
             // 3. ✅ THÊM: Nạp ảnh từ File System (Ổ đĩa D hoặc đường dẫn tuyệt đối)
             if (imgUrl == null) {
                 java.io.File localFile = new java.io.File(pathAnh);
                 // Ví dụ: Nếu pathAnh là "D:/data/sp1.png"
                 if (localFile.exists() && localFile.isFile()) {
                     System.out.println("✅ Nạp ảnh từ File System: " + pathAnh);
                     try {
                         // Chuyển File sang URL để dùng cho ImageIcon
                         imgUrl = localFile.toURI().toURL();
                     } catch (Exception e) {
                         System.out.println("⚠ Lỗi khi nạp ảnh từ File System: " + e.getMessage());
                     }
                 }
             }
        }
        
        if (imgUrl != null) {
            icon = new ImageIcon(imgUrl);
        } else {
            System.out.println("⚠ Không tìm thấy ảnh: " + pathAnh + " → dùng ảnh trắng");
            // Tạo ảnh trắng 200x200
            BufferedImage bImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bImg.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 200, 200);
            g.dispose();
            icon = new ImageIcon(bImg);
        }

        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel lblAnh = new JLabel(new ImageIcon(img), SwingConstants.CENTER);
        lblAnh.setPreferredSize(new Dimension(200, 200));
        JLabel lblTen = new JLabel(ten, SwingConstants.CENTER);
        lblTen.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel lblGia = new JLabel(gia, SwingConstants.CENTER);
        lblGia.setFont(new Font("Arial", Font.BOLD, 14));
        lblGia.setForeground(Color.RED);

        btnSanPham.add(lblAnh, BorderLayout.CENTER);
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.add(lblTen);
        info.add(lblGia);
        btnSanPham.add(info, BorderLayout.SOUTH);

        // Khi click sản phẩm
        btnSanPham.addActionListener(e -> {
            // SỬA: Yêu cầu đăng nhập trước khi xem chi tiết
            if (!UserManager.getInstance().isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để xem chi tiết sản phẩm!", "Yêu cầu đăng nhập", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Tìm sản phẩm trong danh sách gốc
            for (SanPham sp : danhSachSanPham) {
                if (sp.getTen().equals(ten)) {
                    ChiTietSanPham chiTiet = new ChiTietSanPham(this, sp);
                    chiTiet.setVisible(true);
                    break;
                }
            }
        });

        centerPanel.add(btnSanPham);
    }
}