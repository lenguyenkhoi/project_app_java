package QuanLy.giohang;

/**
 * DataUser: POJO thuần chứa dữ liệu người dùng và các helper validate.
 * Không chứa bất kỳ component GUI nào.
 */
public class UserData {
    private String username;
    private String phone;
    private String password;
    private String confirmPassword;

    public static final String PHONE_REGEX = "\\d{9,12}";

    public UserData() {
        this.username = "";
        this.phone = "";
        this.password = "";
        this.confirmPassword = "";
    }

    public UserData(String username, String phone, String password, String confirmPassword) {
        this.username = username == null ? "" : username.trim();
        this.phone = phone == null ? "" : phone.trim();
        this.password = password == null ? "" : password;
        this.confirmPassword = confirmPassword == null ? "" : confirmPassword;
    }

    // --- Getters / Setters ---
    public String getUsername() {
        return username == null ? "" : username.trim();
    }

    public void setUsername(String username) {
        this.username = username == null ? "" : username.trim();
    }

    public String getPhone() {
        return phone == null ? "" : phone.trim();
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? "" : phone.trim();
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password == null ? "" : password;
    }

    public String getConfirmPassword() {
        return confirmPassword == null ? "" : confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword == null ? "" : confirmPassword;
    }

    // --- Helpers / validation ---
    public boolean anyEmptyForRegister() {
        return getUsername().isEmpty() || getPhone().isEmpty() || getPassword().isEmpty() || getConfirmPassword().isEmpty();
    }

    public boolean phoneValid() {
        return getPhone().matches(PHONE_REGEX);
    }

    public boolean passwordConfirmed() {
        return getPassword().equals(getConfirmPassword());
    }

    // Reset data
    public void reset() {
        this.username = "";
        this.phone = "";
        this.password = "";
        this.confirmPassword = "";
    }

    @Override
    public String toString() {
        return "DataUser{" +
                "username='" + getUsername() + '\'' +
                ", phone='" + getPhone() + '\'' +
                '}';
    }
}