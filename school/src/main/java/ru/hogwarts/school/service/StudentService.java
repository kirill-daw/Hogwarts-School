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

    public List<Student> getFirstSixStudents() {
        logger.info("Was invoked method for get first six students");

        List<Student> allStudents = studentRepository.findAll();

        if (allStudents.size() < 6) {
            logger.warn("Not enough students in database. Need at least 6, but found {}", allStudents.size());
            return allStudents;
        }

        List<Student> firstSix = allStudents.subList(0, Math.min(6, allStudents.size()));
        logger.debug("Returning {} students for parallel/synchronized printing", firstSix.size());
        return firstSix;
    }

    public void printStudentsParallel() {
        logger.info("Was invoked method for parallel printing of student names");

        try {
            List<Student> students = getFirstSixStudents();

            if (students.size() < 6) {
                logger.warn("Not enough students for parallel printing (need 6, have {})", students.size());
                System.out.println("Not enough students in database. Need at least 6 for parallel printing.");
                return;
            }

            System.out.println("Main thread - Student 1: " + students.get(0).getName());
            System.out.println("Main thread - Student 2: " + students.get(1).getName());

            Thread thread1 = new Thread(() -> {
                try {
                    System.out.println("Thread 1 - Student 3: " + students.get(2).getName());
                    System.out.println("Thread 1 - Student 4: " + students.get(3).getName());
                } catch (Exception e) {
                    logger.error("Error in thread 1 during parallel printing", e);
                }
            });

            Thread thread2 = new Thread(() -> {
                try {
                    System.out.println("Thread 2 - Student 5: " + students.get(4).getName());
                    System.out.println("Thread 2 - Student 6: " + students.get(5).getName());
                } catch (Exception e) {
                    logger.error("Error in thread 2 during parallel printing", e);
                }
            });

            thread1.start();
            thread2.start();

            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread was interrupted during parallel printing", e);
            }

            logger.info("Parallel printing completed successfully");
        } catch (Exception e) {
            logger.error("Error in printStudentsParallel method", e);
            System.out.println("Error occurred during parallel printing: " + e.getMessage());
        }
    }

    private synchronized void printStudentNameSynchronized(String threadName, String studentName) {
        try {
            System.out.println(threadName + " - Student: " + studentName);
        } catch (Exception e) {
            logger.error("Error in synchronized print method", e);
        }
    }

    public void printStudentsSynchronized() {
        logger.info("Was invoked method for synchronized printing of student names");

        try {
            List<Student> students = getFirstSixStudents();

            if (students.size() < 6) {
                logger.warn("Not enough students for synchronized printing (need 6, have {})", students.size());
                System.out.println("Not enough students in database. Need at least 6 for synchronized printing.");
                return;
            }

            printStudentNameSynchronized("Main thread", students.get(0).getName());
            printStudentNameSynchronized("Main thread", students.get(1).getName());

            Thread thread1 = new Thread(() -> {
                try {
                    printStudentNameSynchronized("Thread 1", students.get(2).getName());
                    printStudentNameSynchronized("Thread 1", students.get(3).getName());
                } catch (Exception e) {
                    logger.error("Error in thread 1 during synchronized printing", e);
                }
            });

            Thread thread2 = new Thread(() -> {
                try {
                    printStudentNameSynchronized("Thread 2", students.get(4).getName());
                    printStudentNameSynchronized("Thread 2", students.get(5).getName());
                } catch (Exception e) {
                    logger.error("Error in thread 2 during synchronized printing", e);
                }
            });

            thread1.start();
            thread2.start();

            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread was interrupted during synchronized printing", e);
            }

            logger.info("Synchronized printing completed successfully");
        } catch (Exception e) {
            logger.error("Error in printStudentsSynchronized method", e);
            System.out.println("Error occurred during synchronized printing: " + e.getMessage());
        }
    }
}