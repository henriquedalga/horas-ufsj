package IntegrandoDrive.controller;

import java.io.IOException;

import IntegrandoDrive.model.Student;
import IntegrandoDrive.persistence.DrivePersistence;

public class StudentController {
    private DrivePersistence persistence;

    public StudentController(DrivePersistence persistence) {
        this.persistence = persistence;
    }
    public String createStudentFolder(Student student, String parentFolderId) throws IOException {
        return persistence.createFolderIfNotExists(student.getName(), parentFolderId);
    }
    public void submitAssignment(Student student) {
        student.setSubmitted(true);
    }
}