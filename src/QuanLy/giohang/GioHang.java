package QuanLy.giohang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import QuanLy.model.*; // Model sản phẩm
import QuanLy.dto.CheckoutData; // DTO dữ liệu thanh toán
import QuanLy.logic.GioHangLogic; // Logic của giỏ hàng
import QuanLy.logic.CartManager; // Quản lý giỏ hàng (Singleton)
import QuanLy.giohang.ThanhToan; // Giao diện thanh toán
public class GioHang extends JFrame {
   private static final long serialVersionUID = 1L;
   private JPanel contentPane;
   private JCheckBox selectAllCheckBox;
   private JPanel cartItemsPanel;
   private JButton purchaseButton;
   private JLabel totalLabel;
  
   private List<ProductPanel> productPanels = new ArrayList<>();
   private JFrame parentFrame;
  
   // Khai báo logic (vẫn như cũ)
   private GioHangLogic logic;
   // (Hàm main và constructor public GioHang() giữ nguyên)
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				GioHang frame = new GioHang();
				frame.setVisible(true);
			}
		});
	}
	public GioHang() {
		this(null);
	}
  
   public GioHang(JFrame parent) {
       this.parentFrame = parent;
      
       // Khởi tạo logic (vẫn như cũ)
       this.logic = new GioHangLogic();
      
       setTitle("Giỏ Hàng Của Bạn");
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       // (Phần còn lại của constructor giữ nguyên y hệt)
       // ...
       setSize(1000, 700);
       setLocationRelativeTo(null);
       contentPane = new JPanel(new BorderLayout());
       // ... (topBar, ...)
       // ...
      
       // 3. Panel Tổng kết và Mua hàng (ở dưới cùng)
       contentPane.add(createFooterPanel(), BorderLayout.SOUTH);
       // Panel chính ở giữa
       JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
       mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
       mainPanel.setBackground(new Color(245, 245, 245));
       // 1. Panel Tiêu đề cột
       mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
       // 2. Panel Danh sách sản phẩm (có thể cuộn)
       cartItemsPanel = new JPanel();
       cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
       cartItemsPanel.setBackground(Color.WHITE);
       // Load sản phẩm (vẫn như cũ)
       loadCartItems();
       
       // =================== LỖI ĐƯỢC SỬA TẠI ĐÂY ===================
       // Lỗi gốc: new JScrollPane(scrollPane);
       JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
       // ========================================================

       scrollPane.setBorder(BorderFactory.createEmptyBorder());
       mainPanel.add(scrollPane, BorderLayout.CENTER);
       contentPane.add(mainPanel, BorderLayout.CENTER);
      
       // Cập nhật tổng tiền ban đầu (vẫn như cũ)
       updateTotal();
       
       // Thêm contentPane vào JFrame
       setContentPane(contentPane);
   }
  
   /**
    * Load sản phẩm từ Logic và hiển thị
    */
   private void loadCartItems() {
       try {
           System.out.println("Bắt đầu load giỏ hàng...");
           cartItemsPanel.removeAll();
           productPanels.clear();
          
           if (logic.isCartEmpty()) {
               System.out.println("Giỏ hàng trống");
               JLabel emptyLabel = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
                       "<h2>Giỏ hàng của bạn đang trống</h2>" +
                       "<p>Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm!</p>" +
                       "</div></html>", SwingConstants.CENTER);
               emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
               cartItemsPanel.add(emptyLabel);
           } else {
               List<CartManager.CartItem> items = logic.getCartItems();
               System.out.println("Giỏ hàng có " + items.size() + " sản phẩm");
              
               JPanel shopPanel = createShopSectionPanel();
              
               for (CartManager.CartItem item : items) {
                   try {
                       SanPham sp = item.getSanPham();
                       if (sp == null) {
                           System.err.println("Lỗi: CartItem có SanPham null");
                           continue;
                       }
                      
                       // Tạo ProductPanel và truyền CartItem (Model)
                       ProductPanel p = new ProductPanel(item);
                      
                       productPanels.add(p);
                       shopPanel.add(p);
                   } catch (Exception e) {
                       System.err.println("Lỗi khi tạo ProductPanel: " + e.getMessage());
                       e.printStackTrace();
                   }
               }
              
               cartItemsPanel.add(shopPanel);
           }
          
           cartItemsPanel.revalidate();
           cartItemsPanel.repaint();
           System.out.println("Hoàn tất load giỏ hàng");
       } catch (Exception e) {
           System.err.println("LỖI NGHIÊM TRỌNG khi load giỏ hàng: " + e.getMessage());
           e.printStackTrace();
           JLabel errorLabel = new JLabel("<html><div style='text-align: center; padding: 50px; color: red;'>" +
                   "<h2>Lỗi khi tải giỏ hàng</h2>" +
                   "<p>" + e.getMessage() + "</p>" +
                   "</div></html>", SwingConstants.CENTER);
           errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
           cartItemsPanel.add(errorLabel);
           cartItemsPanel.revalidate();
           cartItemsPanel.repaint();
       }
   }
  
   // (createHeaderPanel và createShopSectionPanel giữ nguyên)
   private JPanel createHeaderPanel() {
       // ... (Không thay đổi)
       GridBagLayout gbl_headerPanel = new GridBagLayout();
       gbl_headerPanel.columnWidths = new int[]{40, 0, 0, 0, 0, 49, 0, 0, 0, 0};
       JPanel headerPanel = new JPanel(gbl_headerPanel);
       headerPanel.setBackground(Color.WHITE);
       headerPanel.setBorder(BorderFactory.createCompoundBorder(
           new MatteBorder(0, 0, 1, 0, new Color(224, 224, 224)),
           new EmptyBorder(10, 10, 10, 10)
       ));
      
       Font headerFont = new Font("Times New Roman", Font.BOLD, 12);
       Color headerColor = Color.GRAY;
      
       GridBagConstraints gbc_empty = new GridBagConstraints();
       gbc_empty.gridy = 0;
       gbc_empty.fill = GridBagConstraints.HORIZONTAL;
       gbc_empty.insets = new Insets(0, 5, 0, 5);
       gbc_empty.gridx = 0;
       gbc_empty.weightx = 0.05;
       headerPanel.add(new JLabel(""), gbc_empty);
      
       GridBagConstraints gbc_sanPham = new GridBagConstraints();
       gbc_sanPham.gridy = 0;
       gbc_sanPham.fill = GridBagConstraints.HORIZONTAL;
       gbc_sanPham.insets = new Insets(0, 5, 0, 5);
       gbc_sanPham.gridx = 1;
       gbc_sanPham.weightx = 0.35;
       JLabel sanPhamLabel = new JLabel("Sản Phẩm", SwingConstants.LEFT);
       sanPhamLabel.setFont(headerFont);
       sanPhamLabel.setForeground(headerColor);
       headerPanel.add(sanPhamLabel, gbc_sanPham);
       GridBagConstraints gbc_donGia = new GridBagConstraints();
       gbc_donGia.gridy = 0;
       gbc_donGia.fill = GridBagConstraints.HORIZONTAL;
       gbc_donGia.insets = new Insets(0, 5, 0, 5);
       gbc_donGia.gridx = 3;
       gbc_donGia.weightx = 0.15;
       JLabel donGiaLabel = new JLabel("Đơn Giá", SwingConstants.RIGHT);
       donGiaLabel.setFont(headerFont);
       donGiaLabel.setForeground(headerColor);
       headerPanel.add(donGiaLabel, gbc_donGia);
      
       GridBagConstraints gbc_soLuong = new GridBagConstraints();
       gbc_soLuong.gridy = 0;
       gbc_soLuong.insets = new Insets(0, 5, 0, 5);
       gbc_soLuong.gridx = 6;
       gbc_soLuong.weightx = 0.15;
       JLabel soLuongLabel = new JLabel("Số Lượng", SwingConstants.CENTER);
       soLuongLabel.setFont(headerFont);
       soLuongLabel.setForeground(headerColor);
       headerPanel.add(soLuongLabel, gbc_soLuong);
       GridBagConstraints gbc_soTien = new GridBagConstraints();
       gbc_soTien.gridy = 0;
       gbc_soTien.fill = GridBagConstraints.HORIZONTAL;
       gbc_soTien.insets = new Insets(0, 5, 0, 5);
       gbc_soTien.gridx = 8;
       gbc_soTien.weightx = 0.15;
       JLabel soTienLabel = new JLabel("Số Tiền", SwingConstants.CENTER);
       soTienLabel.setFont(headerFont);
       soTienLabel.setForeground(headerColor);
       headerPanel.add(soTienLabel, gbc_soTien);
       GridBagConstraints gbc_thaoTac = new GridBagConstraints();
       gbc_thaoTac.gridy = 0;
       gbc_thaoTac.fill = GridBagConstraints.HORIZONTAL;
       gbc_thaoTac.insets = new Insets(0, 5, 0, 0);
       gbc_thaoTac.gridx = 9;
       gbc_thaoTac.weightx = 0.15;
       JLabel thaoTacLabel = new JLabel("Thao Tác", SwingConstants.CENTER);
       thaoTacLabel.setFont(headerFont);
       thaoTacLabel.setForeground(headerColor);
       headerPanel.add(thaoTacLabel, gbc_thaoTac);
       return headerPanel;
   }
   private JPanel createShopSectionPanel() {
       JPanel shopPanel = new JPanel();
       shopPanel.setLayout(new BoxLayout(shopPanel, BoxLayout.Y_AXIS));
       shopPanel.setBackground(Color.WHITE);
       shopPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
      
       return shopPanel;
   }
   /**
    * Tạo Panel tổng kết ở dưới cùng
    */
   private JPanel createFooterPanel() {
       JPanel footerPanel = new JPanel(new BorderLayout());
       footerPanel.setBackground(Color.WHITE);
       footerPanel.setBorder(BorderFactory.createCompoundBorder(
           new MatteBorder(1, 0, 0, 0, new Color(224, 224, 224)),
           new EmptyBorder(10, 20, 10, 20)
       ));
       // (leftPanel và selectAllCheckBox giữ nguyên)
       JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
       leftPanel.setOpaque(false);
      
       selectAllCheckBox = new JCheckBox("Chọn Tất Cả");
       selectAllCheckBox.setFont(new Font("Times New Roman", Font.PLAIN, 14));
       selectAllCheckBox.addActionListener(e -> {
           boolean isSelected = selectAllCheckBox.isSelected();
           for (ProductPanel p : productPanels) {
               p.setSelected(isSelected);
           }
           updateTotal();
       });
      
       leftPanel.add(selectAllCheckBox);
       footerPanel.add(leftPanel, BorderLayout.WEST);
       // (rightPanel và purchaseButton ActionListener giữ nguyên)
       JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
       rightPanel.setOpaque(false);
       totalLabel = new JLabel("Tổng cộng (0 sản phẩm): 0đ");
       totalLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
      
       purchaseButton = new JButton("Mua Hàng");
       purchaseButton.setBackground(new Color(238, 77, 45));
       purchaseButton.setForeground(Color.BLUE);
       purchaseButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
       purchaseButton.setFocusPainted(false);
       purchaseButton.setBorder(new EmptyBorder(12, 25, 12, 25));
      
       purchaseButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
              
               // 1. Gọi logic
               CheckoutData data = logic.prepareCheckoutData(productPanels);
               // 2. Kiểm tra
               if (data.isEmpty()) {
                   totalLabel.setText("<html>Vui lòng chọn sản phẩm!</html>");
                   return;
               }
               // 3. Mở ThanhToan (truyền dữ liệu từ logic)
               // Lỗi gốc: Lớp ThanhToan trống
               ThanhToan thanhToanFrame = new ThanhToan(data.cartData, data.totalAmount);
               thanhToanFrame.setVisible(true);
               thanhToanFrame.setLocationRelativeTo(null);
              
               // 4. Đóng cửa sổ giỏ hàng
               GioHang.this.dispose();
           }
       });
       rightPanel.add(totalLabel);
       rightPanel.add(Box.createHorizontalStrut(10));
       rightPanel.add(purchaseButton);
       footerPanel.add(rightPanel, BorderLayout.EAST);
       return footerPanel;
   }
  
   /**
    * Cập nhật tổng tiền (vẫn như cũ)
    */
   private void updateTotal() {
       if (totalLabel != null && logic != null) {
           String totalHtml = logic.calculateTotal(this.productPanels);
           totalLabel.setText(totalHtml);
       }
   }
  
   // ==================================================================
   // LỚP NỘI TẠI (INNER CLASS)
   // ==================================================================
  
   // =================== LỖI ĐƯỢC SỬA TẠI ĐÂY ===================
   // Lỗi gốc: "class ProductPanel" (package-private)
   // Sửa thành "public class ProductPanel"
   public class ProductPanel extends JPanel {
   // ========================================================

       private JCheckBox checkBox;
       private JLabel productNameLabel;
       private JLabel productVariantLabel;
       private JLabel productPriceLabel;
       private JLabel productTotalLabel;
       private JTextField quantityField;
      
       private CartManager.CartItem cartItem;
       ProductPanel(CartManager.CartItem item) {
           this.cartItem = item;
          
           String name = item.getSanPham().getTen();
           String variant = "Phân Loại: " + item.getVariant();
           String imageUrl = item.getSanPham().getPathAnh();
           long unitPrice = item.getDonGia();
           int initialQuantity = item.getSoLuong();
           setLayout(new GridBagLayout());
           setBackground(Color.WHITE);
           setBorder(new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
           // Cột 0: Checkbox
           GridBagConstraints gbc_checkBox = new GridBagConstraints();
           gbc_checkBox.insets = new Insets(20, 10, 20, 10);
           gbc_checkBox.fill = GridBagConstraints.NONE;
           gbc_checkBox.anchor = GridBagConstraints.CENTER;
           gbc_checkBox.gridx = 0;
           gbc_checkBox.weightx = 0.05;
           checkBox = new JCheckBox();
           checkBox.setOpaque(false);
           checkBox.setSelected(true);
           checkBox.addActionListener(e -> updateTotal());
           add(checkBox, gbc_checkBox);
           // Cột 1: Ảnh và Tên sản phẩm
           GridBagConstraints gbc_productInfo = new GridBagConstraints();
           gbc_productInfo.insets = new Insets(20, 10, 20, 10);
           gbc_productInfo.fill = GridBagConstraints.HORIZONTAL;
           gbc_productInfo.anchor = GridBagConstraints.WEST;
           gbc_productInfo.gridx = 1;
           gbc_productInfo.weightx = 0.35;
           add(createProductInfoPanel(name, variant, imageUrl), gbc_productInfo);
           // Cột 2: Đơn Giá
           GridBagConstraints gbc_priceLabel = new GridBagConstraints();
           gbc_priceLabel.insets = new Insets(20, 10, 20, 10);
           gbc_priceLabel.fill = GridBagConstraints.NONE;
           gbc_priceLabel.anchor = GridBagConstraints.WEST;
           gbc_priceLabel.gridx = 2;
           gbc_priceLabel.weightx = 0.15;
           productPriceLabel = new JLabel(String.format("%,dđ", unitPrice), SwingConstants.LEFT);
           productPriceLabel.setForeground(Color.RED);
           productPriceLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
           add(productPriceLabel, gbc_priceLabel);
           // Cột 3: Số Lượng
           GridBagConstraints gbc_quantityPanel = new GridBagConstraints();
           gbc_quantityPanel.insets = new Insets(20, 10, 20, 10);
           gbc_quantityPanel.fill = GridBagConstraints.NONE;
           gbc_quantityPanel.anchor = GridBagConstraints.WEST;
           gbc_quantityPanel.gridx = 3;
           gbc_quantityPanel.weightx = 0.15;
           add(createQuantityPanel(initialQuantity), gbc_quantityPanel);
           // Cột 4: Số Tiền
           GridBagConstraints gbc_totalLabel = new GridBagConstraints();
           gbc_totalLabel.insets = new Insets(20, 10, 20, 10);
           gbc_totalLabel.fill = GridBagConstraints.NONE;
           gbc_totalLabel.anchor = GridBagConstraints.WEST;
           gbc_totalLabel.gridx = 4;
           gbc_totalLabel.weightx = 0.15;
           productTotalLabel = new JLabel("", SwingConstants.LEFT);
           productTotalLabel.setForeground(Color.RED);
           productTotalLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
           add(productTotalLabel, gbc_totalLabel);
           // Cột 5: Thao Tác (Xóa)
           GridBagConstraints gbc_deleteButton = new GridBagConstraints();
           gbc_deleteButton.insets = new Insets(20, 10, 20, 10);
           gbc_deleteButton.fill = GridBagConstraints.NONE;
           gbc_deleteButton.anchor = GridBagConstraints.WEST;
           gbc_deleteButton.gridx = 5;
           gbc_deleteButton.weightx = 0.15;
           JButton deleteButton = new JButton("Xóa");
           deleteButton.setHorizontalAlignment(SwingConstants.LEFT);
           deleteButton.setForeground(Color.RED);
           deleteButton.setOpaque(false);
           deleteButton.setContentAreaFilled(false);
           deleteButton.setBorderPainted(false);
           deleteButton.setFont(new Font("Times New Roman", Font.PLAIN, 14));
           deleteButton.addActionListener(e -> {
               int confirm = JOptionPane.showConfirmDialog(
                   this,
                   "Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?",
                   "Xác nhận xóa",
                   JOptionPane.YES_NO_OPTION
               );
               if (confirm == JOptionPane.YES_OPTION) {
                   logic.removeItem(cartItem);
                   this.setVisible(false);
                   productPanels.remove(this);
                   ((JPanel)this.getParent()).revalidate();
                   ((JPanel)this.getParent()).repaint();
                   updateTotal();
               }
           });
           add(deleteButton, gbc_deleteButton);
           updateItemTotal();
       }
       private JPanel createProductInfoPanel(String name, String variant, String imageUrl) {
           // ... (Không thay đổi)
           JPanel infoPanel = new JPanel(new BorderLayout(10, 0));
           infoPanel.setOpaque(false);
           JLabel imageLabel = new JLabel();
           imageLabel.setPreferredSize(new Dimension(80, 80));
           imageLabel.setBorder(new LineBorder(Color.LIGHT_GRAY));
           imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
          
           java.net.URL imgUrl = getClass().getResource(imageUrl);
           if (imgUrl != null) {
               ImageIcon icon = new ImageIcon(imgUrl);
               Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
               imageLabel.setIcon(new ImageIcon(scaled));
           } else {
               imageLabel.setText("IMG");
               imageLabel.setBackground(new Color(250, 250, 250));
               imageLabel.setOpaque(true);
           }
          
           infoPanel.add(imageLabel, BorderLayout.WEST);
           JPanel textPanel = new JPanel();
           textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
           textPanel.setOpaque(false);
           productNameLabel = new JLabel("<html><body style='width: 200px'>" + name + "</body></html>");
           productNameLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
          
           productVariantLabel = new JLabel(variant);
           productVariantLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
           productVariantLabel.setForeground(Color.GRAY);
           textPanel.add(productNameLabel);
           textPanel.add(Box.createVerticalStrut(5));
           textPanel.add(productVariantLabel);
          
           infoPanel.add(textPanel, BorderLayout.CENTER);
           return infoPanel;
       }
       private JPanel createQuantityPanel(int initialQuantity) {
           // ... (Không thay đổi)
           JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
           quantityPanel.setBackground(Color.WHITE);
           quantityPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
           JButton decreaseButton = new JButton("-");
           decreaseButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
           decreaseButton.setPreferredSize(new Dimension(30, 30));
           decreaseButton.setBackground(Color.WHITE);
           decreaseButton.setBorder(null);
           decreaseButton.setFocusPainted(false);
          
           decreaseButton.addActionListener(e -> {
               int currentQty = cartItem.getSoLuong();
               if (currentQty > 1) {
                   currentQty--;
                   cartItem.setSoLuong(currentQty);
                   quantityField.setText(String.valueOf(currentQty));
                   updateItemTotal();
               }
           });
           quantityField = new JTextField(String.valueOf(initialQuantity), 3);
           quantityField.setHorizontalAlignment(SwingConstants.CENTER);
           quantityField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
           quantityField.setPreferredSize(new Dimension(40, 30));
           quantityField.setBorder(new MatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY));
           quantityField.addActionListener(e -> updateQuantityFromField());
           JButton increaseButton = new JButton("+");
           increaseButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
           increaseButton.setPreferredSize(new Dimension(30, 30));
           increaseButton.setBackground(Color.WHITE);
           increaseButton.setBorder(null);
           increaseButton.setFocusPainted(false);
          
           increaseButton.addActionListener(e -> {
               int currentQty = cartItem.getSoLuong();
               currentQty++;
               cartItem.setSoLuong(currentQty);
               quantityField.setText(String.valueOf(currentQty));
               updateItemTotal();
           });
           quantityPanel.add(decreaseButton);
           quantityPanel.add(quantityField);
           quantityPanel.add(increaseButton);
           return quantityPanel;
       }
      
       private void updateQuantityFromField() {
           // ... (Không thay đổi)
           int newQuantity;
           try {
               newQuantity = Integer.parseInt(quantityField.getText());
               if (newQuantity < 1) {
                   newQuantity = 1;
               }
           } catch (NumberFormatException ex) {
               newQuantity = cartItem.getSoLuong();
           }
          
           cartItem.setSoLuong(newQuantity);
           quantityField.setText(String.valueOf(newQuantity));
           updateItemTotal();
       }
       private void updateItemTotal() {
           // ... (Không thay đổi)
           long itemTotal = cartItem.getDonGia() * cartItem.getSoLuong();
           productTotalLabel.setText(String.format("%,dđ", itemTotal));
           updateTotal();
       }
      
       public boolean isSelected() {
           // ... (Không thay đổi)
           return checkBox.isSelected();
       }
      
       public void setSelected(boolean selected) {
           // ... (Không thay đổi)
           checkBox.setSelected(selected);
       }
      
       public CartManager.CartItem getCartItem() {
           // ... (Không thay đổi)
           return this.cartItem;
       }
   }
}