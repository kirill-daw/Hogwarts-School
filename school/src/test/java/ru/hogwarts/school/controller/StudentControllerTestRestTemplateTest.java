package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private StudentService studentService;

    private static final Long STUDENT_ID = 1L;
    private static final String STUDENT_NAME = "Гарри Поттер";
    private static final int STUDENT_AGE = 17;
    private static final Long FACULTY_ID = 1L;
    private static final String FACULTY_NAME = "Гриффиндор";
    private static final String FACULTY_COLOR = "красный";

    @Test
    void createStudent_shouldReturnStudent() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        Student student = new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty);
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                getBaseUrl(), student, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(STUDENT_ID, response.getBody().getId());
        assertEquals(STUDENT_NAME, response.getBody().getName());
        assertEquals(STUDENT_AGE, response.getBody().getAge());
    }

    @Test
    void getStudent_shouldReturnStudent() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        Student student = new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty);
        when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + STUDENT_ID, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(STUDENT_ID, response.getBody().getId());
        assertEquals(STUDENT_NAME, response.getBody().getName());
    }

    @Test
    void getStudent_shouldReturnNotFoundForInvalidId() {
        when(studentService.getStudentById(9999L))
                .thenThrow(new StudentNotFoundException("Student not found with id: 9999"));

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/9999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Student not found"));
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        Student updatedStudent = new Student(STUDENT_ID, "Рон Уизли", 16, faculty);
        when(studentService.updateStudent(eq(STUDENT_ID), any(Student.class))).thenReturn(updatedStudent);

        HttpEntity<Student> request = new HttpEntity<>(updatedStudent);
        ResponseEntity<Student> response = restTemplate.exchange(
                getBaseUrl() + "/" + STUDENT_ID,
                HttpMethod.PUT,
                request,
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Рон Уизли", response.getBody().getName());
        assertEquals(16, response.getBody().getAge());
    }

    @Test
    void deleteStudent_shouldDeleteStudent() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        Student student = new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty);
        when(studentService.deleteStudent(STUDENT_ID)).thenReturn(student);

        when(studentService.getStudentById(STUDENT_ID))
                .thenThrow(new StudentNotFoundException("Student not found with id: " + STUDENT_ID));

        ResponseEntity<Student> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/" + STUDENT_ID,
                HttpMethod.DELETE,
                null,
                Student.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + STUDENT_ID, String.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void getAllStudents_shouldReturnAllStudents() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty),
                new Student(2L, "Гермиона Грейнджер", 17, faculty)
        );
        when(studentService.getAllStudents()).thenReturn(students);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getBaseUrl(), Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getStudentsByAge_shouldReturnFilteredStudents() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty)
        );
        when(studentService.getStudentsByAge(STUDENT_AGE)).thenReturn(students);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getBaseUrl() + "/age?age=" + STUDENT_AGE, Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnFilteredStudents() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty)
        );
        when(studentService.getStudentsByAgeBetween(16, 20)).thenReturn(students);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getBaseUrl() + "/age-between?min=16&max=20", Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(studentService.getFacultyByStudentId(STUDENT_ID)).thenReturn(faculty);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + STUDENT_ID + "/faculty", Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FACULTY_NAME, response.getBody().getName());
    }

    @Test
    void getStudentFaculty_shouldReturnNotFoundWhenNoFaculty() {
        when(studentService.getFacultyByStudentId(STUDENT_ID))
                .thenThrow(new StudentNotFoundException("Student doesn't have a faculty"));

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + STUDENT_ID + "/faculty", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/student";
    }
}