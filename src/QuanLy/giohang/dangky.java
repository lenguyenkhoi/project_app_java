// Trong file: QuanLy/giohang/dangky.java
package QuanLy.giohang;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import QuanLy.giohang.UserData;
import QuanLy.giohang.dangky;
import QuanLy.giohang.dangnhap;
// SỬA: Bỏ import user.java, dùng DBHelper
import dao.DBHelper;

public class dangky extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtTaiKhoan, txtSoDT;
    private JPasswordField txtMatKhau, txtXacNhan;

    public dangky() {
        setTitle("Đăng ký tài khoản");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(248, 248, 255));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitle = new JLabel("DATALAB_HUB");
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 34));
        lblTitle.setForeground(new Color(0, 128, 192));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(150, 20, 300, 50);
        contentPane.add(lblTitle);

        JLabel lblHoTen = new JLabel("Tài Khoản:");
        lblHoTen.setFont(new Font("Times New Roman", Font.BOLD, 14));
        lblHoTen.setBounds(100, 90, 100, 25);
        contentPane.add(lblHoTen);

        JLabel lblSoDT = new JLabel("Số Điện Thoại:");
        lblSoDT.setFont(new Font("Times New Roman", Font.BOLD, 14));
        lblSoDT.setBounds(100, 125, 100, 25);
        contentPane.add(lblSoDT);

        JLabel lblMatKhau = new JLabel("Mật Khẩu:");
        lblMatKhau.setFont(new Font("Times New Roman", Font.BOLD, 14));
        lblMatKhau.setBounds(100, 160, 100, 25);
        contentPane.add(lblMatKhau);


        JLabel lblXacNhan = new JLabel("Xác Nhận Mật Khẩu:");
        lblXacNhan.setFont(new Font("Times New Roman", Font.BOLD, 14));
        lblXacNhan.setBounds(100, 195, 100, 25);
        contentPane.add(lblXacNhan);

     // tạo các component GUI cục bộ
        txtTaiKhoan = new JTextField();
        txtTaiKhoan.setBounds(210, 90, 250, 25);
        contentPane.add(txtTaiKhoan);

        txtSoDT = new JTextField();
        txtSoDT.setBounds(210, 125, 250, 25);
        contentPane.add(txtSoDT);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setEchoChar('*');
        txtMatKhau.setBounds(210, 160, 250, 25);
        contentPane.add(txtMatKhau);

        txtXacNhan = new JPasswordField();
        txtXacNhan.setEchoChar('*');
        txtXacNhan.setBounds(210, 195, 250, 25);
        contentPane.add(txtXacNhan);
        
        
        JButton btnDangKy = new JButton("ĐĂNG KÝ");
        btnDangKy.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnDangKy.setForeground(Color.RED);
        btnDangKy.setBackground(new Color(255, 240, 240));
        btnDangKy.setFocusPainted(false);
        btnDangKy.setBounds(240, 250, 120, 35);
        btnDangKy.addActionListener((ActionEvent e) -> onRegister());
        contentPane.add(btnDangKy);

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Times New Roman", Font.BOLD, 13));
        btnBack.setBackground(new Color(220, 220, 220));
        btnBack.setBounds(20, 15, 110, 30);
        btnBack.addActionListener(e -> {
            // mở dialog đăng nhập dạng modal, đặt this làm parent
            dangnhap dn = new dangnhap(this);
            dn.setVisible(true);
            // không dispose ở đây — user có thể đóng dialog rồi quay lại
        });
        contentPane.add(btnBack);
    }

    // SỬA: Toàn bộ hàm onRegister
    private void onRegister() {
        // Tạo DataUser từ các component GUI
        UserData dataUser = new UserData();
        dataUser.setUsername(txtTaiKhoan.getText());
        dataUser.setPhone(txtSoDT.getText());
        dataUser.setPassword(new String(txtMatKhau.getPassword()));
        dataUser.setConfirmPassword(new String(txtXacNhan.getPassword()));

        if (dataUser.anyEmptyForRegister()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!dataUser.passwordConfirmed()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return;
        }

        if (!dataUser.phoneValid()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (9-12 chữ số)!");
            return;
        }

        try {
            // SỬA: Kiểm tra user đã tồn tại (dùng DBHelper.checkLogin thay vì user.userExists)
            String userCheck = DBHelper.checkLogin(dataUser.getUsername(), ""); // Dùng checkLogin
            if (!userCheck.equals("NO_USER")) {
                 JOptionPane.showMessageDialog(this, "Tài khoản đã tồn tại!");
                 return;
            }

            // SỬA: Lưu user vào DB (dùng DBHelper.registerUser)
            // (Lưu ý: DBHelper.registerUser đã được sửa để nhận (user, pass, phone))
            boolean ok = DBHelper.registerUser(
                dataUser.getUsername(), 
                dataUser.getPassword(), 
                dataUser.getPhone()
            );
            
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Lỗi khi đăng ký (xem console để biết chi tiết)!");
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
                dispose();
                // Tự động mở dialog đăng nhập và truyền username đã đăng ký để tự fill (tuỳ chọn)
                SwingUtilities.invokeLater(() -> {
                    dangnhap dn = new dangnhap(null);
                    // truyền username thông qua setter nếu muốn tự fill:
                    dn.prefillUsername(dataUser.getUsername());
                    dn.setVisible(true);
                });
            }
        } catch (Exception ex) {
            // Bắt mọi ngoại lệ từ DB để không crash UI
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại.");
        }
    }

    // main để test độc lập
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            dangky frame = new dangky();
            frame.setVisible(true);
        });
    }
}