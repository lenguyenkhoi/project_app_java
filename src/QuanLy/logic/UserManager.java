package QuanLy.logic;

/**
 * Quản lý thông tin người dùng đã đăng nhập
 */
public class UserManager {
    private static UserManager instance;
    private String username;
    private String fullName;
    private int userId;
    private boolean isLoggedIn;
    
    private UserManager() {
        isLoggedIn = false;
        username = null;
        fullName = null;
        userId = -1;
    }
    
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    public void login(String username, String fullName, int userId) {
        this.username = username;
        this.fullName = fullName;
        this.userId = userId;
        this.isLoggedIn = true;
    }
    
    public void logout() {
        this.username = null;
        this.fullName = null;
        this.userId = -1;
        this.isLoggedIn = false;
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public int getUserId() {
        return userId;
    }
}


