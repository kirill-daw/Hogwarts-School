package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.*;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long id = 0;

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++id);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty getFacultyById(Long id) {
        return faculties.get(id);
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        if (!faculties.containsKey(id)) {
            return null;
        }
        faculty.setId(id);
        faculties.put(id, faculty);
        return faculty;
    }

    public Faculty deleteFaculty(Long id) {
        return faculties.remove(id);
    }

    public Collection<Faculty> getAllFaculties() {
        return Collections.unmodifiableCollection(faculties.values());
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        List<Faculty> result = new ArrayList<>();
        for (Faculty faculty : faculties.values()) {
            if (faculty.getColor().equalsIgnoreCase(color)) {
                result.add(faculty);
            }
        }
        return result;
    }
}
