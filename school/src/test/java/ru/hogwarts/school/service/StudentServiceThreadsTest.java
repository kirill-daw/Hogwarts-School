package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceThreadsTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getFirstSixStudents_shouldReturnSixStudents() {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        List<Student> students = Arrays.asList(
                new Student(1L, "Harry Potter", 17, faculty),
                new Student(2L, "Ron Weasley", 17, faculty),
                new Student(3L, "Hermione Granger", 17, faculty),
                new Student(4L, "Draco Malfoy", 17, faculty),
                new Student(5L, "Neville Longbottom", 17, faculty),
                new Student(6L, "Luna Lovegood", 16, faculty),
                new Student(7L, "Ginny Weasley", 16, faculty)
        );

        when(studentRepository.findAll()).thenReturn(students);

        List<Student> result = studentService.getFirstSixStudents();

        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals("Harry Potter", result.get(0).getName());
        assertEquals("Luna Lovegood", result.get(5).getName());
    }

    @Test
    void printStudentsParallel_shouldNotThrowException() {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        List<Student> students = Arrays.asList(
                new Student(1L, "Harry", 17, faculty),
                new Student(2L, "Ron", 17, faculty),
                new Student(3L, "Hermione", 17, faculty),
                new Student(4L, "Draco", 17, faculty),
                new Student(5L, "Neville", 17, faculty),
                new Student(6L, "Luna", 16, faculty)
        );

        when(studentRepository.findAll()).thenReturn(students);

        assertDoesNotThrow(() -> studentService.printStudentsParallel());
    }

    @Test
    void printStudentsSynchronized_shouldNotThrowException() {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        List<Student> students = Arrays.asList(
                new Student(1L, "Harry", 17, faculty),
                new Student(2L, "Ron", 17, faculty),
                new Student(3L, "Hermione", 17, faculty),
                new Student(4L, "Draco", 17, faculty),
                new Student(5L, "Neville", 17, faculty),
                new Student(6L, "Luna", 16, faculty)
        );

        when(studentRepository.findAll()).thenReturn(students);

        assertDoesNotThrow(() -> studentService.printStudentsSynchronized());
    }
}