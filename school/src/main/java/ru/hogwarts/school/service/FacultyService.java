package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
@Transactional
public class FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException("Faculty not found with id: " + id));
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        facultyRepository.findById(id);
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(Long id) {
        Faculty faculty = getFacultyById(id);
        facultyRepository.deleteById(id);
        return faculty;
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> getFacultiesByNameOrColor(String nameOrColor) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
    }

    public Collection<Student> getStudentsByFacultyId(Long facultyId) {
        Faculty faculty = getFacultyById(facultyId);
        return faculty.getStudents();
    }
}