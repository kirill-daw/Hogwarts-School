package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        logger.debug("StudentService initialized with repositories");
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student with data: name={}, age={}", student.getName(), student.getAge());

        if (student == null) {
            logger.error("Attempt to create null student");
            throw new IllegalArgumentException("Student cannot be null");
        }

        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            logger.debug("Looking for faculty with id: {}", student.getFaculty().getId());
            Faculty faculty = facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> {
                        logger.error("Faculty not found with id: {}", student.getFaculty().getId());
                        return new FacultyNotFoundException("Faculty not found with id: " + student.getFaculty().getId());
                    });
            student.setFaculty(faculty);
            logger.debug("Faculty found and set for student");
        }

        Student savedStudent = studentRepository.save(student);
        logger.info("Student created successfully with id: {}", savedStudent.getId());
        return savedStudent;
    }

    public Student getStudentById(Long id) {
        logger.info("Was invoked method for get student by id = {}", id);
        logger.debug("Fetching student with id: {}", id);

        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is not student with id = {}", id);
                    logger.warn("Attempt to access non-existent student with id: {}", id);
                    return new StudentNotFoundException("Student not found with id: " + id);
                });
    }

    public Student updateStudent(Long id, Student student) {
        logger.info("Was invoked method for update student with id = {}", id);
        logger.debug("Updating student {} with new data: name={}, age={}", id, student.getName(), student.getAge());

        Student existingStudent = getStudentById(id);
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());

        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            logger.debug("Setting faculty for student. Faculty id: {}", student.getFaculty().getId());
            Faculty faculty = facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> {
                        logger.error("Faculty not found with id: {}", student.getFaculty().getId());
                        return new FacultyNotFoundException("Faculty not found");
                    });
            existingStudent.setFaculty(faculty);
            logger.debug("Faculty set successfully");
        } else {
            logger.debug("No faculty provided, setting faculty to null");
            existingStudent.setFaculty(null);
        }

        Student updatedStudent = studentRepository.save(existingStudent);
        logger.info("Student with id {} updated successfully", id);
        return updatedStudent;
    }

    public Student deleteStudent(Long id) {
        logger.info("Was invoked method for delete student with id = {}", id);
        logger.debug("Deleting student with id: {}", id);

        Student student = getStudentById(id);
        studentRepository.deleteById(id);
        logger.info("Student with id {} deleted successfully", id);
        logger.debug("Deleted student details: name={}, age={}", student.getName(), student.getAge());
        return student;
    }

    public Collection<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        logger.debug("Fetching all students from database");

        Collection<Student> students = studentRepository.findAll();
        logger.debug("Found {} students in database", students.size());
        return students;
    }

    public Collection<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method for get students by age = {}", age);
        logger.debug("Filtering students by age: {}", age);

        Collection<Student> students = studentRepository.findByAge(age);
        logger.debug("Found {} students with age {}", students.size(), age);
        return students;
    }

    public Collection<Student> getStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for get students by age between {} and {}", min, max);
        logger.debug("Filtering students by age range: {} - {}", min, max);

        Collection<Student> students = studentRepository.findByAgeBetween(min, max);
        logger.debug("Found {} students in age range {} - {}", students.size(), min, max);
        return students;
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        logger.info("Was invoked method for get faculty by student id = {}", studentId);
        logger.debug("Getting faculty for student with id: {}", studentId);

        Student student = getStudentById(studentId);
        Faculty faculty = student.getFaculty();

        if (faculty == null) {
            logger.warn("Student with id {} doesn't have a faculty", studentId);
            logger.error("Faculty not found for student id = {}", studentId);
            throw new FacultyNotFoundException("Student with id " + studentId + " doesn't have a faculty");
        }

        logger.debug("Found faculty for student: faculty id={}, name={}", faculty.getId(), faculty.getName());
        return faculty;
    }

    public Integer getTotalNumberOfStudents() {
        logger.info("Was invoked method for get total number of students");

        Integer count = studentRepository.getTotalNumberOfStudents();
        logger.debug("Total number of students: {}", count);
        return count;
    }

    public Double getAverageAge() {
        logger.info("Was invoked method for get average age of students");

        Double averageAge = studentRepository.getAverageAge();
        logger.debug("Average age of students: {}", averageAge);

        if (averageAge == null) {
            logger.warn("Average age calculation returned null - no students in database?");
        }

        return averageAge;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");

        List<Student> students = studentRepository.findLastFiveStudents();
        logger.debug("Found {} last students (max 5)", students.size());
        return students;
    }

    public List<String> getStudentsNamesStartingWithA() {
        logger.info("Was invoked method for get students names starting with 'A'");

        List<String> names = studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && !name.isEmpty() &&
                        (name.toUpperCase().startsWith("А") || name.toUpperCase().startsWith("A")))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());

        logger.debug("Found {} students names starting with 'A'", names.size());
        return names;
    }

    public Double getAverageAgeUsingFindAll() {
        logger.info("Was invoked method for get average age using findAll()");

        List<Student> students = studentRepository.findAll();

        if (students.isEmpty()) {
            logger.warn("No students found in database");
            return 0.0;
        }

        double averageAge = students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);

        logger.debug("Average age calculated using findAll: {}", averageAge);
        return averageAge;
    }
}