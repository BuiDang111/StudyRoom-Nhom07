package com.group07.roombooking.repository;

import com.google.gson.reflect.TypeToken;
import com.group07.roombooking.model.Student;
import com.group07.roombooking.utils.JsonFileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Tầng Repository phụ trách đọc/ghi dữ liệu Sinh viên từ file backend/data/students.json.
 * Không chứa logic kiểm tra business rule của đặt phòng.
 */
@Repository
public class StudentRepository {

    private static final Type STUDENT_LIST_TYPE = new TypeToken<List<Student>>() {}.getType();
    private final Path filePath;

    public StudentRepository(@Value("${app.data.dir:data}") String dataDir) {
        this.filePath = Paths.get(dataDir, "students.json");
    }

    public List<Student> findAll() {
        return JsonFileUtils.readListFromFile(filePath, STUDENT_LIST_TYPE);
    }

    public Optional<Student> findById(String studentId) {
        if (studentId == null) {
            return Optional.empty();
        }
        return findAll().stream()
                .filter(student -> student.getId().equalsIgnoreCase(studentId.trim()))
                .findFirst();
    }

    public void saveAll(List<Student> students) {
        JsonFileUtils.writeListToFileAtomically(filePath, students, STUDENT_LIST_TYPE);
    }
}
