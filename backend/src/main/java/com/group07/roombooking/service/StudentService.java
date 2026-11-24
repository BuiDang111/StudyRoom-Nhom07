package com.group07.roombooking.service;

import com.group07.roombooking.exception.ResourceNotFoundException;
import com.group07.roombooking.model.Student;
import com.group07.roombooking.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Tầng Service xử lý nghiệp vụ liên quan đến Sinh viên.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentByIdOrThrow(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ResourceNotFoundException("STUDENT_NOT_FOUND", "Mã sinh viên không được để trống.");
        }
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "STUDENT_NOT_FOUND",
                        "Không tìm thấy sinh viên với mã: " + studentId
                ));
    }
}
