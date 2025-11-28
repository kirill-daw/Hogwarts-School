package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long STUDENT_ID = 1L;
    private static final String STUDENT_NAME = "Гарри Поттер";
    private static final int STUDENT_AGE = 17;
    private static final Long FACULTY_ID = 1L;
    private static final String FACULTY_NAME = "Гриффиндор";
    private static final String FACULTY_COLOR = "красный";

    @Test
    void createStudent_shouldReturnStudent() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        Student student = new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty);
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STUDENT_ID))
                .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                .andExpect(jsonPath("$.age").value(STUDENT_AGE));
    }

    @Test
    void getStudent_shouldReturnStudent() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        Student student = new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty);
        when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STUDENT_ID))
                .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                .andExpect(jsonPath("$.age").value(STUDENT_AGE));
    }

    @Test
    void getStudent_shouldReturnNotFoundForInvalidId() throws Exception {
        // ИСПРАВЛЕНО: бросаем правильное исключение
        when(studentService.getStudentById(9999L))
                .thenThrow(new StudentNotFoundException("Student not found with id: 9999"));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        Student updatedStudent = new Student(STUDENT_ID, "Рон Уизли", 16, faculty);
        when(studentService.updateStudent(eq(STUDENT_ID), any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/{id}", STUDENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Рон Уизли"))
                .andExpect(jsonPath("$.age").value(16));
    }

    @Test
    void getAllStudents_shouldReturnAllStudents() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty),
                new Student(2L, "Гермиона Грейнджер", 17, faculty)
        );
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(STUDENT_NAME))
                .andExpect(jsonPath("$[1].name").value("Гермиона Грейнджер"));
    }

    @Test
    void getStudentsByAge_shouldReturnFilteredStudents() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty)
        );
        when(studentService.getStudentsByAge(STUDENT_AGE)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age")
                        .param("age", String.valueOf(STUDENT_AGE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(STUDENT_NAME))
                .andExpect(jsonPath("$[0].age").value(STUDENT_AGE));
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnFilteredStudents() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty)
        );
        when(studentService.getStudentsByAgeBetween(16, 20)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age-between")
                        .param("min", "16")
                        .param("max", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(STUDENT_NAME))
                .andExpect(jsonPath("$[0].age").value(STUDENT_AGE));
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(studentService.getFacultyByStudentId(STUDENT_ID)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}/faculty", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
    }

    @Test
    void getStudentFaculty_shouldReturnNotFoundWhenNoFaculty() throws Exception {
        // ИСПРАВЛЕНО: бросаем правильное исключение
        when(studentService.getFacultyByStudentId(STUDENT_ID))
                .thenThrow(new StudentNotFoundException("Student doesn't have a faculty"));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}/faculty", STUDENT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTotalNumberOfStudents_shouldReturnCount() throws Exception {
        when(studentService.getTotalNumberOfStudents()).thenReturn(100);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void getAverageAge_shouldReturnAverage() throws Exception {
        when(studentService.getAverageAge()).thenReturn(17.5);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/average-age"))
                .andExpect(status().isOk())
                .andExpect(content().string("17.5"));
    }

    @Test
    void getLastFiveStudents_shouldReturnFiveStudents() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(1L, "Студент 1", 17, faculty),
                new Student(2L, "Студент 2", 18, faculty),
                new Student(3L, "Студент 3", 16, faculty),
                new Student(4L, "Студент 4", 19, faculty),
                new Student(5L, "Студент 5", 17, faculty)
        );
        when(studentService.getLastFiveStudents()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/last-five"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Студент 1"));
    }
}