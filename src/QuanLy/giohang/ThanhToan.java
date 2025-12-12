package QuanLy.giohang;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import QuanLy.logic.UserManager;
import dao.DBHelper;
// Đổi tên class thành ThanhToan
public class ThanhToan extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tableDonHang;
	private JTextField txtHoTen, txtDiaChi, txtSoDienThoai, txtGhiChu;
    private JTextField txtSoThe, txtHetHan, txtCVV;
    private List<Object[]> cartDataRef;
    private long totalAmountRef;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				// SỬA: Cung cấp dữ liệu mẫu rỗng khi chạy riêng lẻ
				ThanhToan frame = new ThanhToan(new ArrayList<>(), 0);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	// SỬA: Constructor nhận dữ liệu từ GioHang1
    public ThanhToan(List<Object[]> cartData, long totalAmount) {
		setTitle("Trang Thanh Toán");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 850, 650); // SỬA: Tăng chiều cao một chút cho nút Back

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BorderLayout(10, 10));
		setContentPane(contentPane);

		// === PANEL THÔNG TIN KHÁCH HÀNG ===
		JPanel panelThongTinKhachHang = new JPanel(new GridBagLayout());
		panelThongTinKhachHang.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
		
		// SỬA: Thêm nút Back
		JButton btnBack = new JButton("< Quay lại Giỏ hàng");
		GridBagConstraints gbc_btnBack = new GridBagConstraints();
		gbc_btnBack.insets = new Insets(5, 5, 5, 5);
		gbc_btnBack.anchor = GridBagConstraints.WEST;
		gbc_btnBack.gridx = 0;
		gbc_btnBack.gridy = 0; // Đặt ở hàng đầu tiên, cột đầu tiên
		panelThongTinKhachHang.add(btnBack, gbc_btnBack);
		
		btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mở lại giỏ hàng
                GioHang gioHangFrame = new GioHang();
                gioHangFrame.setVisible(true);
                gioHangFrame.setLocationRelativeTo(null);
                // Đóng trang thanh toán
                ThanhToan.this.dispose();
            }
        });

		
		JLabel lblHoTen = new JLabel("Họ tên người nhận:");
		GridBagConstraints gbc_lblHoTen = new GridBagConstraints();
		gbc_lblHoTen.insets = new Insets(5, 5, 5, 5);
		gbc_lblHoTen.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblHoTen.gridx = 0; gbc_lblHoTen.gridy = 1; // SỬA: Chuyển xuống hàng 1
		panelThongTinKhachHang.add(lblHoTen, gbc_lblHoTen);

		txtHoTen = new JTextField(20);
		GridBagConstraints gbc_txtHoTen = new GridBagConstraints();
		gbc_txtHoTen.insets = new Insets(5, 5, 5, 5);
		gbc_txtHoTen.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtHoTen.gridx = 1; gbc_txtHoTen.gridy = 1; // SỬA: Chuyển xuống hàng 1
		panelThongTinKhachHang.add(txtHoTen, gbc_txtHoTen);

		JLabel lblDiaChi = new JLabel("Địa chỉ:");
		GridBagConstraints gbc_lblDiaChi = new GridBagConstraints();
		gbc_lblDiaChi.insets = new Insets(5, 5, 5, 5);
		gbc_lblDiaChi.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblDiaChi.gridx = 2; gbc_lblDiaChi.gridy = 1; // SỬA: Chuyển xuống hàng 1
		panelThongTinKhachHang.add(lblDiaChi, gbc_lblDiaChi);

		txtDiaChi = new JTextField(20);
		GridBagConstraints gbc_txtDiaChi = new GridBagConstraints();
		gbc_txtDiaChi.insets = new Insets(5, 5, 5, 5);
		gbc_txtDiaChi.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDiaChi.gridx = 3; gbc_txtDiaChi.gridy = 1; // SỬA: Chuyển xuống hàng 1
		panelThongTinKhachHang.add(txtDiaChi, gbc_txtDiaChi);

		JLabel lblSoDienThoai = new JLabel("Số điện thoại:");
		GridBagConstraints gbc_lblSoDienThoai = new GridBagConstraints();
		gbc_lblSoDienThoai.insets = new Insets(5, 5, 5, 5);
		gbc_lblSoDienThoai.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSoDienThoai.gridx = 0; gbc_lblSoDienThoai.gridy = 2; // SỬA: Chuyển xuống hàng 2
		panelThongTinKhachHang.add(lblSoDienThoai, gbc_lblSoDienThoai);

		txtSoDienThoai = new JTextField(20);
		GridBagConstraints gbc_txtSoDienThoai = new GridBagConstraints();
		gbc_txtSoDienThoai.insets = new Insets(5, 5, 5, 5);
		gbc_txtSoDienThoai.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSoDienThoai.gridx = 1; gbc_txtSoDienThoai.gridy = 2; // SỬA: Chuyển xuống hàng 2
		panelThongTinKhachHang.add(txtSoDienThoai, gbc_txtSoDienThoai);

		JLabel lblGhiChu = new JLabel("Ghi chú:");
		GridBagConstraints gbc_lblGhiChu = new GridBagConstraints();
		gbc_lblGhiChu.insets = new Insets(5, 5, 5, 5);
		gbc_lblGhiChu.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblGhiChu.gridx = 2; gbc_lblGhiChu.gridy = 2; // SỬA: Chuyển xuống hàng 2
		panelThongTinKhachHang.add(lblGhiChu, gbc_lblGhiChu);

		txtGhiChu = new JTextField(20);
		GridBagConstraints gbc_txtGhiChu = new GridBagConstraints();
		gbc_txtGhiChu.insets = new Insets(5, 5, 5, 5);
		gbc_txtGhiChu.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtGhiChu.gridx = 3; gbc_txtGhiChu.gridy = 2; // SỬA: Chuyển xuống hàng 2
		panelThongTinKhachHang.add(txtGhiChu, gbc_txtGhiChu);

		contentPane.add(panelThongTinKhachHang, BorderLayout.NORTH);

        // Lưu dữ liệu đơn hàng
        this.cartDataRef = cartData;
        this.totalAmountRef = totalAmount;

        // === PANEL ĐƠN HÀNG ===
		JPanel panelDonHang = new JPanel(new BorderLayout());
		panelDonHang.setBorder(BorderFactory.createTitledBorder("Đơn hàng"));

		String[] columnNames = {"Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		
		// SỬA: Dùng dữ liệu từ cartData để điền vào bảng
        for (Object[] row : cartData) {
            model.addRow(row);
        }

		tableDonHang = new JTable(model);
		panelDonHang.add(new JScrollPane(tableDonHang), BorderLayout.CENTER);

		// SỬA: Dùng totalAmount đã được truyền vào
		JPanel panelTongCong = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel lblTong = new JLabel("Tổng cộng: ");
		JLabel lblGiaTriTong = new JLabel(String.format("%,d VND", totalAmount)); // Dùng totalAmount
		lblGiaTriTong.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblGiaTriTong.setForeground(Color.RED);
		panelTongCong.add(lblTong);
		panelTongCong.add(lblGiaTriTong);

		panelDonHang.add(panelTongCong, BorderLayout.SOUTH);
		contentPane.add(panelDonHang, BorderLayout.CENTER);

		// === PANEL THANH TOÁN ===
		JPanel panelThanhToan = new JPanel(new GridBagLayout());
		panelThanhToan.setBorder(BorderFactory.createTitledBorder("Hình thức thanh toán"));

		
		JRadioButton rdbtnCOD = new JRadioButton("Ship COD");
		JRadioButton rdbtnCard = new JRadioButton("Thẻ VISA/MasterCard");
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnCOD);
		group.add(rdbtnCard);
		
		GridBagConstraints gbc_rdbtnCOD = new GridBagConstraints();
		gbc_rdbtnCOD.insets = new Insets(5, 5, 5, 5);
		gbc_rdbtnCOD.fill = GridBagConstraints.HORIZONTAL;
		gbc_rdbtnCOD.gridx = 0; gbc_rdbtnCOD.gridy = 0;
		panelThanhToan.add(rdbtnCOD, gbc_rdbtnCOD);
		
		GridBagConstraints gbc_rdbtnCard = new GridBagConstraints();
		gbc_rdbtnCard.insets = new Insets(5, 5, 5, 5);
		gbc_rdbtnCard.fill = GridBagConstraints.HORIZONTAL;
		gbc_rdbtnCard.gridy = 1;
		gbc_rdbtnCard.gridx = 0;
		panelThanhToan.add(rdbtnCard, gbc_rdbtnCard);

		JLabel lblSoThe = new JLabel("Số thẻ:");
		txtSoThe = new JTextField(15);
		JLabel lblHetHan = new JLabel("Ngày hết hạn:");
		txtHetHan = new JTextField(10);
		JLabel lblCVV = new JLabel("CVV:");
		txtCVV = new JTextField(5);
		
		GridBagConstraints gbc_lblSoThe = new GridBagConstraints();
		gbc_lblSoThe.insets = new Insets(5, 5, 5, 5);
		gbc_lblSoThe.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSoThe.gridx = 1; gbc_lblSoThe.gridy = 1;
		panelThanhToan.add(lblSoThe, gbc_lblSoThe);
		
		GridBagConstraints gbc_txtSoThe = new GridBagConstraints();
		gbc_txtSoThe.insets = new Insets(5, 5, 5, 5);
		gbc_txtSoThe.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSoThe.gridx = 2; gbc_txtSoThe.gridy = 1;
		panelThanhToan.add(txtSoThe, gbc_txtSoThe);

		GridBagConstraints gbc_lblHetHan = new GridBagConstraints();
		gbc_lblHetHan.insets = new Insets(5, 5, 5, 5);
		gbc_lblHetHan.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblHetHan.gridx = 1; gbc_lblHetHan.gridy = 2;
		panelThanhToan.add(lblHetHan, gbc_lblHetHan);
		
		GridBagConstraints gbc_txtHetHan = new GridBagConstraints();
		gbc_txtHetHan.insets = new Insets(5, 5, 5, 5);
		gbc_txtHetHan.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtHetHan.gridx = 2; gbc_txtHetHan.gridy = 2;
		panelThanhToan.add(txtHetHan, gbc_txtHetHan);
		
		GridBagConstraints gbc_lblCVV = new GridBagConstraints();
		gbc_lblCVV.insets = new Insets(5, 5, 5, 5);
		gbc_lblCVV.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblCVV.gridx = 1; gbc_lblCVV.gridy = 3;
		panelThanhToan.add(lblCVV, gbc_lblCVV);

		GridBagConstraints gbc_txtCVV = new GridBagConstraints();
		gbc_txtCVV.insets = new Insets(5, 5, 5, 5);
		gbc_txtCVV.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCVV.gridx = 2; gbc_txtCVV.gridy = 3;
		panelThanhToan.add(txtCVV, gbc_txtCVV);


		// Mặc định ẩn ô nhập thẻ
		lblSoThe.setVisible(false);
		txtSoThe.setVisible(false);
		lblHetHan.setVisible(false);
		txtHetHan.setVisible(false);
		lblCVV.setVisible(false);
		txtCVV.setVisible(false);

		// Hiện/ẩn theo lựa chọn
		rdbtnCard.addActionListener(e -> {
			lblSoThe.setVisible(true);
			txtSoThe.setVisible(true);
			lblHetHan.setVisible(true);
			txtHetHan.setVisible(true);
			lblCVV.setVisible(true);
			txtCVV.setVisible(true);
		});
		rdbtnCOD.addActionListener(e -> {
			lblSoThe.setVisible(false);
			txtSoThe.setVisible(false);
			lblHetHan.setVisible(false);
			txtHetHan.setVisible(false);
			lblCVV.setVisible(false);
			txtCVV.setVisible(false);
		});
		
		rdbtnCOD.setSelected(true); // Chọn COD làm mặc định

		contentPane.add(panelThanhToan, BorderLayout.SOUTH);

		// NÚT XÁC NHẬN
		JButton btnXacNhan = new JButton("XÁC NHẬN ĐƠN HÀNG");
		btnXacNhan.setFont(new Font("Times New Roman", Font.BOLD, 16));
		
		GridBagConstraints gbc_btnXacNhan = new GridBagConstraints();
		gbc_btnXacNhan.insets = new Insets(5, 5, 5, 5);
		gbc_btnXacNhan.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnXacNhan.gridx = 0;
		gbc_btnXacNhan.gridy = 4; // Thêm vào hàng mới
		gbc_btnXacNhan.gridwidth = 3; // Kéo dài 3 cột
		panelThanhToan.add(btnXacNhan, gbc_btnXacNhan);


        btnXacNhan.addActionListener(e -> {
            String ten = txtHoTen.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            String ghiChu = txtGhiChu.getText().trim();

            if (ten.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ họ tên, số điện thoại và địa chỉ!");
                return;
            }

            // Xác định hình thức thanh toán
            String paymentMethod;
            boolean isCard = rdbtnCard.isSelected();
            if (isCard) {
                paymentMethod = "CARD";
                if (txtSoThe.getText().trim().length() < 12 || txtCVV.getText().trim().length() < 3) {
                    JOptionPane.showMessageDialog(this, "Thông tin thẻ không hợp lệ!");
                    return;
                }
            } else {
                paymentMethod = "COD";
            }

            String summary = String.format(
                "Xác nhận đặt hàng?\nKhách: %s\nSĐT: %s\nĐ/c: %s\nHình thức: %s\nTổng: %,d VND",
                ten, sdt, diaChi, paymentMethod, totalAmountRef
            );
            int choice = JOptionPane.showConfirmDialog(this, summary, "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) return;

            // Lưu DB
            try {
                Integer userId = null;
                try {
                    UserManager um = UserManager.getInstance();
                    if (um != null && um.isLoggedIn()) {
                        userId = um.getUserId();
                    }
                } catch (Exception ignore) {}

                int orderId = dao.DBHelper.saveOrderByName(
                    userId,
                    ten,
                    diaChi,
                    sdt,
                    ghiChu,
                    paymentMethod,
                    (double) totalAmountRef,
                    cartDataRef
                );

                JOptionPane.showMessageDialog(this, "Đặt hàng thành công! Mã đơn: " + orderId);
                // Đóng và quay lại cửa hàng
                this.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi lưu đơn hàng: " + ex.getMessage());
            }
        });
	}
}
