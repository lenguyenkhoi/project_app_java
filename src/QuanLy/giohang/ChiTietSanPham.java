package QuanLy.giohang;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import QuanLy.model.SanPham;
import QuanLy.logic.CartManager;
import java.net.URL;
import java.io.File;

public class ChiTietSanPham extends JDialog {

    private final List<JButton> listOps = new ArrayList<>();
    private int soLuong = 1;
    private JLabel lblSoLuong;
    private String selectedVariant = "Màu Xám"; // Màu mặc định
    private SanPham sanPham;

    public ChiTietSanPham(JFrame parent, SanPham sp) {
        super(parent, "Chi tiết sản phẩm", true);
        setSize(900, 700);
        setLocationRelativeTo(parent);
        this.sanPham = sp; // Lưu sản phẩm để dùng khi thêm vào giỏ

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        // ======== THANH TRÊN ========
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(245, 250, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setForeground(new Color(0, 51, 102));
        btnBack.addActionListener(e -> dispose());
        topPanel.add(btnBack, BorderLayout.WEST);

        JLabel lblTen = new JLabel(sp.getTen(), SwingConstants.CENTER);
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTen.setForeground(Color.BLACK);
        topPanel.add(lblTen, BorderLayout.CENTER);

        contentPane.add(topPanel, BorderLayout.NORTH);

        // ======== PHẦN CHÍNH ========
        JPanel centerPanel = new JPanel(new BorderLayout(20, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

     // ẢNH SẢN PHẨM
        JLabel lblAnh = new JLabel();
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        URL imgUrl = null;
        ImageIcon icon;

     // Lấy đường dẫn ảnh từ SanPham (ví dụ: "/image/DT01.png")
        String tenAnh = sp.getPathAnh(); 
        
        if (tenAnh != null && !tenAnh.trim().isEmpty()) {
            try {
                // Thử nạp ảnh từ Classpath (thư mục bin/image)
                imgUrl = getClass().getResource(tenAnh);
                
                if (imgUrl != null) {
                    System.out.println("✅ Nạp ảnh chi tiết từ Classpath: " + tenAnh);
                } else {
                    System.out.println("❌ KHÔNG tìm thấy ảnh trong Classpath: " + tenAnh);
                }
            } catch (Exception e) {
                System.out.println("⚠ Lỗi khi nạp ảnh từ Classpath: " + e.getMessage());
                imgUrl = null;
            }
        }

        // --- HIỂN THỊ ẢNH (Dùng ảnh đã nạp hoặc ảnh trắng dự phòng) ---

        if (imgUrl != null) {
            // Nạp ảnh thành công
            icon = new ImageIcon(imgUrl);
        } else {
            System.out.println("⚠ Đang dùng ảnh trắng dự phòng...");
            
            // Tạo ảnh trắng 420x300
            BufferedImage img = new BufferedImage(420, 300, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, 420, 300);
            
            // Vẽ thêm một chữ cảnh báo để dễ nhận biết ảnh dự phòng
            g.setColor(Color.DARK_GRAY);
            g.drawString("KHÔNG CÓ ẢNH", 160, 150); 
            
            g.dispose();
            icon = new ImageIcon(img);
        }

        // Thay đổi kích thước và hiển thị
        Image scaled = icon.getImage().getScaledInstance(420, 300, Image.SCALE_SMOOTH);
        lblAnh.setIcon(new ImageIcon(scaled));
        centerPanel.add(lblAnh, BorderLayout.WEST);
        // ======== BÊN PHẢI ========
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // GIÁ TIỀN
        JLabel lblGiaTitle = new JLabel("Giá tiền");
        lblGiaTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblGiaTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblGia = new JLabel(sp.getGia(), SwingConstants.CENTER);
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGia.setForeground(Color.RED);
        lblGia.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblGia.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        rightPanel.add(lblGiaTitle);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(lblGia);
        rightPanel.add(Box.createVerticalStrut(25));

        // OPS
        JPanel opsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        opsPanel.setBackground(Color.WHITE);
        String[] ops = {"Màu Xám", "Màu Đen", "Màu Trắng"};
        for (String s : ops) {
            JButton btn = new JButton(s);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btn.setPreferredSize(new Dimension(90, 40));
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                chonOps(btn);
                selectedVariant = s; // Cập nhật màu đã chọn
            });
            listOps.add(btn);
            opsPanel.add(btn);
        }
        // Chọn màu đầu tiên làm mặc định
        if (!listOps.isEmpty()) {
            chonOps(listOps.get(0));
        }
        rightPanel.add(opsPanel);

        // ======== SỐ LƯỢNG ========
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        quantityPanel.setBackground(Color.WHITE);

        JButton btnTru = new JButton("-");
        btnTru.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTru.setPreferredSize(new Dimension(45, 35));
        btnTru.setFocusPainted(false);
        btnTru.setBackground(Color.WHITE);
        btnTru.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnTru.addActionListener(e -> giamSoLuong());

        lblSoLuong = new JLabel(String.valueOf(soLuong), SwingConstants.CENTER);
        lblSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSoLuong.setPreferredSize(new Dimension(50, 35));
        lblSoLuong.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton btnCong = new JButton("+");
        btnCong.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCong.setPreferredSize(new Dimension(45, 35));
        btnCong.setFocusPainted(false);
        btnCong.setBackground(Color.WHITE);
        btnCong.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnCong.addActionListener(e -> tangSoLuong());

        quantityPanel.add(btnTru);
        quantityPanel.add(lblSoLuong);
        quantityPanel.add(btnCong);

        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(quantityPanel);
        rightPanel.add(Box.createVerticalStrut(25));

        // NÚT THÊM GIỎ HÀNG
        JButton btnThem = new JButton("THÊM VÀO GIỎ HÀNG");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btnThem.setFocusPainted(false);
        btnThem.setBackground(new Color(0, 102, 204));
        btnThem.setForeground(Color.WHITE);
        btnThem.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnThem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThem.setPreferredSize(new Dimension(260, 45));
        btnThem.addActionListener(e -> {
            try {
                System.out.println("🛒 Đang thêm vào giỏ hàng...");
                System.out.println("  - Sản phẩm: " + sanPham.getTen());
                System.out.println("  - Số lượng: " + soLuong);
                System.out.println("  - Variant: " + selectedVariant);
                System.out.println("  - Giá: " + sanPham.getGia());
                
                // ✅ Thêm sản phẩm vào CartManager
                CartManager.getInstance().addToCart(sanPham, soLuong, selectedVariant);
                
                System.out.println("✅ Đã thêm thành công!");
                
                JOptionPane.showMessageDialog(this,
                        "Đã thêm " + soLuong + " " + sanPham.getTen() + " (" + selectedVariant + ") vào giỏ hàng!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                System.err.println("❌ Lỗi khi thêm vào giỏ hàng: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Có lỗi xảy ra khi thêm vào giỏ hàng: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        rightPanel.add(btnThem);
        centerPanel.add(rightPanel, BorderLayout.CENTER);

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // ======== THÔNG TIN ========
        JPanel thongTinPanel = new JPanel(new BorderLayout());
        thongTinPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        thongTinPanel.setBackground(Color.WHITE);

        JTextArea txtThongTin = new JTextArea(
                "Tên sản phẩm: " + sp.getTen() + "\n" +
                "Loại: " + sp.getLoai() + "\n" +
                "Mô tả: Đây là sản phẩm chất lượng cao, thiết kế tinh tế và hiệu năng vượt trội.\n" +
                "Phù hợp với nhu cầu học tập, làm việc và giải trí."
        );
        txtThongTin.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtThongTin.setLineWrap(true);
        txtThongTin.setWrapStyleWord(true);
        txtThongTin.setEditable(false);
        txtThongTin.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(txtThongTin);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        thongTinPanel.add(scroll, BorderLayout.CENTER);

        contentPane.add(thongTinPanel, BorderLayout.SOUTH);
    }

    // ======== ĐỔI MÀU OPS ========
    private void chonOps(JButton btn) {
        for (JButton b : listOps) {
            b.setBackground(Color.WHITE);
            b.setForeground(Color.BLACK);
            b.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 51, 153), 2));
    }

    // ======== XỬ LÝ SỐ LƯỢNG ========
    private void tangSoLuong() {
        soLuong++;
        lblSoLuong.setText(String.valueOf(soLuong));
    }

    private void giamSoLuong() {
        if (soLuong > 1) {
            soLuong--;
            lblSoLuong.setText(String.valueOf(soLuong));
        }
    }
}