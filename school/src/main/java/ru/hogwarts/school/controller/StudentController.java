package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    @DeleteMapping("/{id}")
    public Student deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }

    @GetMapping
    public Collection<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/age")
    public Collection<Student> getStudentsByAge(@RequestParam int age) {
        return studentService.getStudentsByAge(age);
    }

    @GetMapping("/age-between")
    public Collection<Student> getStudentsByAgeRange(@RequestParam int min, @RequestParam int max) {
        return studentService.getStudentsByAgeBetween(min, max);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getStudentFaculty(@PathVariable Long id) {
        return studentService.getFacultyByStudentId(id);
    }

    @GetMapping("/count")
    public Integer getTotalNumberOfStudents() {
        return studentService.getTotalNumberOfStudents();
    }

    @GetMapping("/average-age")
    public Double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last-five")
    public Collection<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }

    @GetMapping("/names-starting-with-a")
    public List<String> getStudentsNamesStartingWithA() {
        return studentService.getStudentsNamesStartingWithA();
    }

    @GetMapping("/average-age-stream")
    public Double getAverageAgeUsingStream() {
        return studentService.getAverageAgeUsingFindAll();
    }

    @GetMapping("/print-parallel")
    public String printStudentsParallel() {
        studentService.printStudentsParallel();
        return "Parallel printing completed. Check console for output.";
    }

    @GetMapping("/print-synchronized")
    public String printStudentsSynchronized() {
        studentService.printStudentsSynchronized();
        return "Synchronized printing completed. Check console for output.";
    }
}