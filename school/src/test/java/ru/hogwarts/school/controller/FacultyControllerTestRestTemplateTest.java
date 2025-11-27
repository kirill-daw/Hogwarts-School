package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FacultyService facultyService;

    private static final Long FACULTY_ID = 1L;
    private static final String FACULTY_NAME = "Гриффиндор";
    private static final String FACULTY_COLOR = "красный";
    private static final Long STUDENT_ID = 1L;
    private static final String STUDENT_NAME = "Гарри Поттер";
    private static final int STUDENT_AGE = 17;

    @Test
    void createFaculty_shouldReturnFaculty() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(faculty);

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                getBaseUrl(), faculty, Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FACULTY_ID, response.getBody().getId());
        assertEquals(FACULTY_NAME, response.getBody().getName());
        assertEquals(FACULTY_COLOR, response.getBody().getColor());
    }

    @Test
    void getFaculty_shouldReturnFaculty() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(facultyService.getFacultyById(FACULTY_ID)).thenReturn(faculty);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + FACULTY_ID, Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FACULTY_ID, response.getBody().getId());
        assertEquals(FACULTY_NAME, response.getBody().getName());
    }

    @Test
    void getFaculty_shouldReturnNotFoundForInvalidId() {
        when(facultyService.getFacultyById(9999L))
                .thenThrow(new FacultyNotFoundException("Faculty not found with id: 9999"));

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/9999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Faculty not found"));
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() {
        Faculty updatedFaculty = new Faculty(FACULTY_ID, "Слизерин", "зеленый");
        when(facultyService.updateFaculty(eq(FACULTY_ID), any(Faculty.class))).thenReturn(updatedFaculty);

        HttpEntity<Faculty> request = new HttpEntity<>(updatedFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                getBaseUrl() + "/" + FACULTY_ID,
                HttpMethod.PUT,
                request,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Слизерин", response.getBody().getName());
        assertEquals("зеленый", response.getBody().getColor());
    }

    @Test
    void deleteFaculty_shouldDeleteFaculty() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(facultyService.deleteFaculty(FACULTY_ID)).thenReturn(faculty);

        when(facultyService.getFacultyById(FACULTY_ID))
                .thenThrow(new FacultyNotFoundException("Faculty not found with id: " + FACULTY_ID));

        ResponseEntity<Faculty> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/" + FACULTY_ID,
                HttpMethod.DELETE,
                null,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + FACULTY_ID, String.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void getAllFaculties_shouldReturnAllFaculties() {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR),
                new Faculty(2L, "Слизерин", "зеленый")
        );
        when(facultyService.getAllFaculties()).thenReturn(faculties);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getBaseUrl(), Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getFacultiesByColor_shouldReturnFilteredFaculties() {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR)
        );
        when(facultyService.getFacultiesByColor(FACULTY_COLOR)).thenReturn(faculties);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getBaseUrl() + "/color?color=" + FACULTY_COLOR, Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void searchFaculties_shouldReturnMatchingFaculties() {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR)
        );
        when(facultyService.getFacultiesByNameOrColor(FACULTY_NAME)).thenReturn(faculties);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getBaseUrl() + "/search?nameOrColor=" + FACULTY_NAME, Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getFacultyStudents_shouldReturnStudents() {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty)
        );
        when(facultyService.getStudentsByFacultyId(FACULTY_ID)).thenReturn(students);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + FACULTY_ID + "/students", Collection.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getFacultyStudents_shouldReturnNotFoundForInvalidFaculty() {
        when(facultyService.getStudentsByFacultyId(9999L))
                .thenThrow(new FacultyNotFoundException("Faculty not found with id: 9999"));

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/9999/students", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/faculty";
    }
}