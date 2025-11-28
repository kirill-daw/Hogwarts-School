package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long FACULTY_ID = 1L;
    private static final String FACULTY_NAME = "Гриффиндор";
    private static final String FACULTY_COLOR = "красный";
    private static final Long STUDENT_ID = 1L;
    private static final String STUDENT_NAME = "Гарри Поттер";
    private static final int STUDENT_AGE = 17;

    @Test
    void createFaculty_shouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(FACULTY_ID))
                .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
    }

    @Test
    void getFaculty_shouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(facultyService.getFacultyById(FACULTY_ID)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}", FACULTY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(FACULTY_ID))
                .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
    }

    @Test
    void getFaculty_shouldReturnNotFoundForInvalidId() throws Exception {
        when(facultyService.getFacultyById(9999L))
                .thenThrow(new FacultyNotFoundException("Faculty not found with id: 9999"));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() throws Exception {
        Faculty updatedFaculty = new Faculty(FACULTY_ID, "Слизерин", "зеленый");
        when(facultyService.updateFaculty(eq(FACULTY_ID), any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/{id}", FACULTY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Слизерин"))
                .andExpect(jsonPath("$.color").value("зеленый"));
    }

    @Test
    void deleteFaculty_shouldDeleteFaculty() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(facultyService.deleteFaculty(FACULTY_ID)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/{id}", FACULTY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(FACULTY_ID))
                .andExpect(jsonPath("$.name").value(FACULTY_NAME));
    }

    @Test
    void getAllFaculties_shouldReturnAllFaculties() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR),
                new Faculty(2L, "Слизерин", "зеленый")
        );
        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(FACULTY_NAME))
                .andExpect(jsonPath("$[1].name").value("Слизерин"));
    }

    @Test
    void getFacultiesByColor_shouldReturnFilteredFaculties() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR)
        );
        when(facultyService.getFacultiesByColor(FACULTY_COLOR)).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/color")
                        .param("color", FACULTY_COLOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(FACULTY_NAME))
                .andExpect(jsonPath("$[0].color").value(FACULTY_COLOR));
    }

    @Test
    void searchFaculties_shouldReturnMatchingFaculties() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR)
        );
        when(facultyService.getFacultiesByNameOrColor(FACULTY_NAME)).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search")
                        .param("nameOrColor", FACULTY_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(FACULTY_NAME))
                .andExpect(jsonPath("$[0].color").value(FACULTY_COLOR));
    }

    @Test
    void getFacultyStudents_shouldReturnStudents() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        List<Student> students = Arrays.asList(
                new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE, faculty)
        );
        when(facultyService.getStudentsByFacultyId(FACULTY_ID)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}/students", FACULTY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(STUDENT_NAME))
                .andExpect(jsonPath("$[0].age").value(STUDENT_AGE));
    }

    @Test
    void getFacultyStudents_shouldReturnNotFoundForInvalidFaculty() throws Exception {
        when(facultyService.getStudentsByFacultyId(9999L))
                .thenThrow(new FacultyNotFoundException("Faculty not found with id: 9999"));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}/students", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchFaculties_shouldReturnEmptyListForNoMatches() throws Exception {
        when(facultyService.getFacultiesByNameOrColor("Несуществующий")).thenReturn(Arrays.asList());

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search")
                        .param("nameOrColor", "Несуществующий"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}