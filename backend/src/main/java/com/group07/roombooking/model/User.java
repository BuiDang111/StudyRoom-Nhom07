package com.group07.roombooking.model;

/**
 * Abstract class User đại diện cho đối tượng người dùng chung trong hệ thống.
 * Áp dụng:
 * - Abstraction (Trừu tượng): Không cho phép khởi tạo trực tiếp đối tượng User chung chung.
 * - Encapsulation (Đóng gói): Toàn bộ thuộc tính là private, kiểm tra hợp lệ trong setter/constructor.
 */
public abstract class User {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String email;

    protected User() {
    }

    protected User(String id, String fullName, String phoneNumber, String email) {
        setId(id);
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
    }

    public abstract String getRoleName();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã định danh người dùng không được để trống.");
        }
        this.id = id.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        this.fullName = fullName.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        this.phoneNumber = phoneNumber.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
        this.email = email.trim();
    }
}
