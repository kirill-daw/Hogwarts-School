package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student createStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            Faculty faculty = facultyRepository.findById(student.getFaculty().getId()).orElseThrow(() -> new FacultyNotFoundException("Faculty not found with id: " + student.getFaculty().getId()));
            student.setFaculty(faculty);
        }
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
    }

    public Student updateStudent(Long id, Student student) {
        Student existingStudent = getStudentById(id);
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());

        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            Faculty faculty = facultyRepository.findById(student.getFaculty().getId()).orElseThrow(() -> new FacultyNotFoundException("Faculty not found"));
            existingStudent.setFaculty(faculty);
        } else {
            existingStudent.setFaculty(null);
        }

        return studentRepository.save(existingStudent);
    }

    public Student deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.deleteById(id);
        return student;
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Collection<Student> getStudentsByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        Student student = getStudentById(studentId);
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            throw new FacultyNotFoundException("Student with id " + studentId + " doesn't have a faculty");
        }
        return faculty;
    }

    public Integer getTotalNumberOfStudents() {
        return studentRepository.getTotalNumberOfStudents();
    }

    public Double getAverageAge() {
        return studentRepository.getAverageAge();
    }

    public List<Student> getLastFiveStudents() {
        return studentRepository.findLastFiveStudents();
    }
}