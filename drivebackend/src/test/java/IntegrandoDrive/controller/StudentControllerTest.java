package IntegrandoDrive.controller;

import IntegrandoDrive.model.Student;
import IntegrandoDrive.persistence.DrivePersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {
    @Mock private DrivePersistence persistence;
    @InjectMocks private StudentController controller;

    @Test
    void createStudentFolder_callsPersistence() throws IOException {
        Student s = new Student("Naan");
        controller.createStudentFolder(s, "parent");
        verify(persistence).createFolderIfNotExists("Naan", "parent");
    }

    @Test
    void submitAssignment_setsFlag() {
        Student s = new Student("Naan");
        controller.submitAssignment(s);
        assertTrue(s.hasSubmitted());
    }
}
