package QuanLy.giohang;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import QuanLy.logic.UserManager;
import dao.DBHelper;

public class LichSuDonHang extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tblOrders;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LichSuDonHang frame = new LichSuDonHang();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LichSuDonHang() {
        // Chỉ cho phép vào khi đã đăng nhập
        try {
            UserManager um = UserManager.getInstance();
            if (um == null || !um.isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để xem lịch sử đơn hàng.");
                dispose();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để xem lịch sử đơn hàng.");
            dispose();
            return;
        }

        setTitle("Lịch sử đơn hàng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("LỊCH SỬ ĐƠN HÀNG", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 18));
        top.add(title, BorderLayout.CENTER);
        JButton btnRefresh = new JButton("Tải lại");
        btnRefresh.addActionListener(e -> loadOrders());
        top.add(btnRefresh, BorderLayout.EAST);
        contentPane.add(top, BorderLayout.NORTH);

        // Orders table with action button
        String[] orderCols = {"Mã ĐH", "Khách hàng", "SĐT", "Địa chỉ", "HTTT", "Tổng", "Ngày tạo", ""};
        DefaultTableModel orderModel = new DefaultTableModel(orderCols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        tblOrders = new JTable(orderModel);
        tblOrders.setRowHeight(22);
        tblOrders.getColumnModel().getColumn(7).setPreferredWidth(120);
        tblOrders.getColumnModel().getColumn(7).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JButton btn = new JButton("Xem chi tiết");
                btn.setFont(new Font("Times New Roman", Font.PLAIN, 12));
                return btn;
            }
        });
        tblOrders.getColumnModel().getColumn(7).setCellEditor(new javax.swing.DefaultCellEditor(new javax.swing.JCheckBox()) {
            private static final long serialVersionUID = 1L;
            private JButton editorButton;
            private int editingRow = -1;
            {
                editorButton = new JButton("Xem chi tiết");
                editorButton.setFont(new Font("Times New Roman", Font.PLAIN, 12));
                editorButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (editingRow >= 0) {
                            int orderId = Integer.parseInt(String.valueOf(tblOrders.getValueAt(editingRow, 0)));
                            openDetail(orderId);
                        }
                    }
                });
            }
            @Override
            public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                editingRow = row;
                return editorButton;
            }
            @Override
            public Object getCellEditorValue() { return null; }
        });
        JScrollPane spOrders = new JScrollPane(tblOrders);
        spOrders.setPreferredSize(new Dimension(900, 520));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(spOrders, BorderLayout.CENTER);
        contentPane.add(center, BorderLayout.CENTER);

        loadOrders();
    }

    private void loadOrders() {
        try {
            DefaultTableModel model = (DefaultTableModel) tblOrders.getModel();
            model.setRowCount(0);
            Integer userId = null;
            try {
                UserManager um = UserManager.getInstance();
                if (um != null && um.isLoggedIn()) userId = um.getUserId();
            } catch (Exception ignore) {}

            List<Map<String, Object>> orders = DBHelper.getOrdersByUserId(userId);
            for (Map<String, Object> m : orders) {
                model.addRow(new Object[] {
                    m.get("OrderId"),
                    m.get("ReceiverName"),
                    m.get("Phone"),
                    m.get("Address"),
                    m.get("PaymentMethod"),
                    String.format("%,.0f", (Double) m.get("Total")),
                    m.get("CreatedAt"),
                    "Xem chi tiết"
                });
            }

            // nothing else; details moved to separate screen
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải lịch sử: " + e.getMessage());
        }
    }

    private void openDetail(int orderId) {
        OrderDetail frame = new OrderDetail(orderId);
        frame.setVisible(true);
    }
}
