package model.user;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User extends AId implements Serializable {
    @ColumnName("full_name")
    private String fullName;
    @ColumnName("phone_number")
    private String phone;
    private String email;
    @ColumnName("password_hash")
    private String password;
    @ColumnName("created_at")
    private LocalDateTime createdAt;
    @ColumnName("account_status")
    private String status;
    @ColumnName("role_user")
    private String role; // 'user', 'admin'
    @ColumnName("auth_provider")
    private String authProvider;
    @ColumnName("firebase_uid")
    private String firebaseUID;
    @ColumnName("verify_token")
    private String verifyToken;

    public User(int id, String fullName, String phone, String email, String password, LocalDateTime createdAt,
            String status, String role, String authProvider, String firebaseUID) {
        super(id);
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.status = status;
        this.role = role;
        this.authProvider = authProvider;
        this.firebaseUID = firebaseUID;
    }

    public User() {
        super();
    }

    public String getFullName() {
        return fullName;
    }

    @ColumnName("phone_number")
    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @ColumnName("account_status")
    public String getStatus() {
        return status;
    }

    @ColumnName("role_user")
    public String getRole() {
        return role;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public String getFirebaseUID() {
        return firebaseUID;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @ColumnName("phone_number")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ColumnName("password_hash")
    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @ColumnName("account_status")
    public void setStatus(String status) {
        this.status = status;
    }

    @ColumnName("role_user")
    public void setRole(String role) {
        this.role = role;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }

    public String getVerifyToken() {
        return verifyToken;
    }

    public void setVerifyToken(String verifyToken) {
        this.verifyToken = verifyToken;
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null)
            return "";
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public String getFormattedCreatedDate() {
        if (createdAt == null)
            return "";
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
