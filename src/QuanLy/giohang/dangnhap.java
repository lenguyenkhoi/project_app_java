// Trong file: QuanLy/giohang/dangnhap.java
package QuanLy.giohang;


import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import dao.DBHelper;
import QuanLy.logic.UserManager;

// SỬA: Import DBHelper và UserManager
import dao.DBHelper;

public class dangnhap extends JDialog {
    private static final long serialVersionUID = 1L;

    // GUI components (cục bộ)
    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;

    public dangnhap(Frame parent) {
        super(parent, "Đăng nhập", true);
        setSize(650, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(248, 248, 255));
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("DATALAB_HUB", SwingConstants.CENTER);
        lblTitle.setForeground(new Color(0, 128, 192));
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 42));
        lblTitle.setBounds(150, 20, 350, 60);
        contentPane.add(lblTitle);

        JLabel lblUser = new JLabel("Tài Khoản:");
        lblUser.setFont(new Font("Times New Roman", Font.BOLD, 16));
        lblUser.setBounds(120, 130, 100, 25);
        contentPane.add(lblUser);

        txtTaiKhoan = new JTextField();
        txtTaiKhoan.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        txtTaiKhoan.setBounds(230, 130, 300, 30);
        contentPane.add(txtTaiKhoan);

        JLabel lblPass = new JLabel("Mật Khẩu:");
        lblPass.setFont(new Font("Times New Roman", Font.BOLD, 16));
        lblPass.setBounds(120, 180, 100, 25);
        contentPane.add(lblPass);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setEchoChar('*');
        txtMatKhau.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        txtMatKhau.setBounds(230, 180, 300, 30);
        contentPane.add(txtMatKhau);

        JButton btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setFont(new Font("Times New Roman", Font.BOLD, 20));
        btnDangNhap.setBackground(new Color(230, 230, 230));
        btnDangNhap.setForeground(new Color(0, 128, 192));
        btnDangNhap.setBounds(240, 240, 160, 40);
        btnDangNhap.addActionListener((ActionEvent e) -> onLogin());
        contentPane.add(btnDangNhap);

        JLabel lblQuestion = new JLabel("Bạn chưa có tài khoản?");
        lblQuestion.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        lblQuestion.setBounds(220, 300, 160, 20);
        contentPane.add(lblQuestion);

        JButton btnDangKy = new JButton("ĐĂNG KÝ");
        btnDangKy.setFont(new Font("Times New Roman", Font.BOLD, 14));
        btnDangKy.setForeground(Color.RED);
        btnDangKy.setBounds(380, 297, 130, 28);
        btnDangKy.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                dangky dk = new dangky();
                dk.setVisible(true);
            });
        });
        contentPane.add(btnDangKy);
    }

    // Tuỳ chọn: prefill username (sẽ được gọi từ dangky khi muốn tự điền)
    public void prefillUsername(String username) {
        if (username != null) {
            txtTaiKhoan.setText(username);
        }
    }

    // SỬA: Toàn bộ hàm onLogin
    private void onLogin() {
        String tk = txtTaiKhoan.getText().trim();
        String mk = new String(txtMatKhau.getPassword());

        if (tk.isEmpty() || mk.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        try {
            // SỬA: Dùng DBHelper.checkLogin đã được sửa
            String loginResult = DBHelper.checkLogin(tk, mk);
            
            switch (loginResult) {
                case "SUCCESS":
                    // SỬA: Đăng nhập thành công -> Cập nhật UserManager
                    int userId = DBHelper.getUserIdByUsername(tk);
                    String fullName = DBHelper.getFullNameByUsername(tk); // Sẽ trả về taiKhoan
                    
                    UserManager.getInstance().login(tk, fullName, userId);
                    
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                    dispose(); // Đóng cửa sổ đăng nhập

                    // Mở giao diện chính
                    SwingUtilities.invokeLater(() -> {
                        Cuahang main = new Cuahang();
                        main.setLocationRelativeTo(null);
                        main.setVisible(true);
                    });
                    break;
                case "NO_USER":
                case "WRONG_PASS":
                    JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
                    break;
                case "ERROR":
                default:
                    JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi đăng nhập.");
                    break;
            }
        } catch (Exception ex) {
            // Bắt lỗi từ DB (SQLException hoặc các lỗi khác) để không crash UI
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi đăng nhập. Vui lòng thử lại hoặc liên hệ admin.");
        }
    }

    // main để test độc lập (VÀ LÀ ENTRY POINT CỦA ỨNG DỤNG)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            dangnhap dialog = new dangnhap(null);
            dialog.setVisible(true);
        });
    }
}