package com.group07.roombooking.model;

/**
 * Class Student đại diện cho Sinh viên đặt phòng học nhóm.
 * Áp dụng:
 * - Inheritance (Kế thừa): Student extends User, tái sử dụng id, fullName, phoneNumber, email qua super().
 * - Encapsulation (Đóng gói): Thuộc tính className là private và kiểm tra không rỗng.
 */
public class Student extends User {
    private String className;

    public Student() {
        super();
    }

    public Student(String id, String fullName, String phoneNumber, String className, String email) {
        super(id, fullName, phoneNumber, email);
        setClassName(className);
    }

    @Override
    public String getRoleName() {
        return "STUDENT";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Lớp sinh hoạt của sinh viên không được để trống.");
        }
        this.className = className.trim();
    }
}
