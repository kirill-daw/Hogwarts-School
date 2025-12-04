package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @GetMapping("/{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return facultyService.getFacultyById(id);
    }

    @PutMapping("/{id}")
    public Faculty updateFaculty(@PathVariable Long id, @RequestBody Faculty faculty) {
        return facultyService.updateFaculty(id, faculty);
    }

    @DeleteMapping("/{id}")
    public Faculty deleteFaculty(@PathVariable Long id) {
        return facultyService.deleteFaculty(id);
    }

    @GetMapping
    public Collection<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    @GetMapping("/color")
    public Collection<Faculty> getFacultiesByColor(@RequestParam String color) {
        return facultyService.getFacultiesByColor(color);
    }

    @GetMapping("/search")
    public Collection<Faculty> searchFaculties(@RequestParam String nameOrColor) {
        return facultyService.getFacultiesByNameOrColor(nameOrColor);
    }

    @GetMapping("/{id}/students")
    public Collection<Student> getFacultyStudents(@PathVariable Long id) {
        return facultyService.getStudentsByFacultyId(id);
    }

    @GetMapping("/longest-name")
    public String getLongestFacultyName() {
        return facultyService.getLongestFacultyName();
    }
}