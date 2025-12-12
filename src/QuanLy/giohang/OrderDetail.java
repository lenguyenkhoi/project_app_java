package QuanLy.giohang;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.DBHelper;

public class OrderDetail extends JFrame {
    private static final long serialVersionUID = 1L;
    private int orderId;
    private JTable tblItems;
    private JLabel lblSummary;

    public OrderDetail(int orderId) {
        this.orderId = orderId;
        setTitle("Chi tiết đơn hàng #" + orderId);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        lblSummary = new JLabel("", SwingConstants.LEFT);
        lblSummary.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        content.add(lblSummary, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Số lượng", "Đơn giá", "Thành tiền", "Thuế"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblItems = new JTable(model);
        JScrollPane sp = new JScrollPane(tblItems);
        content.add(sp, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        try {
            // Load order info
            Map<String, Object> order = null;
            for (Map<String, Object> m : DBHelper.getOrdersByUserId(null)) {
                if (((Integer) m.get("OrderId")) == orderId) { order = m; break; }
            }
            if (order != null) {
                String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(order.get("CreatedAt"));
                lblSummary.setText(String.format(
                    "<html><b>Mã ĐH:</b> %d &nbsp;&nbsp; <b>Khách:</b> %s &nbsp;&nbsp; <b>SĐT:</b> %s &nbsp;&nbsp; <b>Đ/c:</b> %s<br/>"
                    + "<b>HTTT:</b> %s &nbsp;&nbsp; <b>Tổng:</b> %,.0f &nbsp;&nbsp; <b>Ngày:</b> %s</html>",
                    order.get("OrderId"), order.get("ReceiverName"), order.get("Phone"), order.get("Address"),
                    order.get("PaymentMethod"), (Double) order.get("Total"), createdAt
                ));
            } else {
                lblSummary.setText("Không tìm thấy đơn hàng.");
            }

            // Load items
            DefaultTableModel model = (DefaultTableModel) tblItems.getModel();
            model.setRowCount(0);
            List<Object[]> items = DBHelper.getOrderItemsByOrderId(orderId);
            for (Object[] r : items) {
                String code = String.valueOf(r[0]);
                int qty = Integer.parseInt(String.valueOf(r[1]));
                double price = Double.parseDouble(String.valueOf(r[2]));
                double line = Double.parseDouble(String.valueOf(r[3]));
                double tax = r.length >= 5 ? Double.parseDouble(String.valueOf(r[4])) : 0;
                model.addRow(new Object[] { code, qty, String.format("%,.0f", price), String.format("%,.0f", line), String.format("%,.0f", tax) });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết: " + e.getMessage());
        }
    }
}


