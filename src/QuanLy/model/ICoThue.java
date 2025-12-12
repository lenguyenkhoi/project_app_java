package QuanLy.model;

/**
 * Interface để đánh dấu sản phẩm có thuế
 */
public interface ICoThue {
    /**
     * Tính thuế cho sản phẩm
     * @param donGia Đơn giá của sản phẩm (đã chuyển đổi sang số)
     * @return Số tiền thuế cần trả (10% cho điện thoại)
     */
    double tinhThue(long donGia);
}