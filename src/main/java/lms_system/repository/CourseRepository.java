package lms_system.repository;

import lms_system.entity.Course;
import lms_system.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    default Course getOrThrow(Long id) {
        return findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Курс с id " + id + " не найден"));
    }
}