package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
        logger.debug("FacultyService initialized with repository");
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty with data: name={}, color={}", faculty.getName(), faculty.getColor());

        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty created successfully with id: {}", savedFaculty.getId());
        return savedFaculty;
    }

    public Faculty getFacultyById(Long id) {
        logger.info("Was invoked method for get faculty by id = {}", id);
        logger.debug("Fetching faculty with id: {}", id);

        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not faculty with id = {}", id);
                    logger.warn("Attempt to access non-existent faculty with id: {}", id);
                    return new FacultyNotFoundException("Faculty not found with id: " + id);
                });
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        logger.info("Was invoked method for update faculty with id = {}", id);
        logger.debug("Updating faculty {} with new data: name={}, color={}", id, faculty.getName(), faculty.getColor());

        getFacultyById(id);

        faculty.setId(id);
        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty with id {} updated successfully", id);
        return updatedFaculty;
    }

    public Faculty deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty with id = {}", id);
        logger.debug("Deleting faculty with id: {}", id);

        Faculty faculty = getFacultyById(id);
        facultyRepository.deleteById(id);
        logger.info("Faculty with id {} deleted successfully", id);
        logger.debug("Deleted faculty details: name={}, color={}", faculty.getName(), faculty.getColor());
        return faculty;
    }

    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        logger.debug("Fetching all faculties from database");

        Collection<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Found {} faculties in database", faculties.size());
        return faculties;
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for get faculties by color = {}", color);
        logger.debug("Filtering faculties by color: {}", color);

        Collection<Faculty> faculties = facultyRepository.findByColor(color);
        logger.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    public Collection<Faculty> getFacultiesByNameOrColor(String nameOrColor) {
        logger.info("Was invoked method for get faculties by name or color: {}", nameOrColor);
        logger.debug("Searching faculties by name or color: {}", nameOrColor);

        Collection<Faculty> faculties = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
        logger.debug("Found {} faculties matching name or color '{}'", faculties.size(), nameOrColor);
        return faculties;
    }

    public Collection<Student> getStudentsByFacultyId(Long facultyId) {
        logger.info("Was invoked method for get students by faculty id = {}", facultyId);
        logger.debug("Getting students for faculty with id: {}", facultyId);

        Faculty faculty = getFacultyById(facultyId);
        Collection<Student> students = faculty.getStudents();
        logger.debug("Found {} students for faculty id {}", students.size(), facultyId);
        return students;
    }
}