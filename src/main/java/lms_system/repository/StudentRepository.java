package lms_system.repository;

import lms_system.entity.Student;
import lms_system.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    default Student getOrThrow(Long id) {
        return findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Студент с id " + id + " не найден"));
    }
}