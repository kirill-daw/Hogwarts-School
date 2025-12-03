package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.service.StudentService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerThreadsWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void printStudentsParallel_shouldInvokeServiceMethod() throws Exception {
        doNothing().when(studentService).printStudentsParallel();

        mockMvc.perform(MockMvcRequestBuilders.get("/student/print-parallel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Parallel printing completed. Check console for output."));

        verify(studentService, times(1)).printStudentsParallel();
    }

    @Test
    void printStudentsSynchronized_shouldInvokeServiceMethod() throws Exception {
        doNothing().when(studentService).printStudentsSynchronized();

        mockMvc.perform(MockMvcRequestBuilders.get("/student/print-synchronized"))
                .andExpect(status().isOk())
                .andExpect(content().string("Synchronized printing completed. Check console for output."));

        verify(studentService, times(1)).printStudentsSynchronized();
    }
}