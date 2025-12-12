package QuanLy.dto;

import java.util.List;
/**
* Lớp đơn giản để chứa dữ liệu chuẩn bị cho thanh toán.
*/
public class CheckoutData {
   public final List<Object[]> cartData;
   public final long totalAmount;
   public CheckoutData(List<Object[]> cartData, long totalAmount) {
       this.cartData = cartData;
       this.totalAmount = totalAmount;
   }
   public boolean isEmpty() {
       return cartData == null || cartData.isEmpty();
   }
}