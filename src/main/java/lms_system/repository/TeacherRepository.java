package lms_system.repository;

import lms_system.entity.Teacher;
import lms_system.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    default Teacher getOrThrow(Long id) {
        return findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Преподаватель с id " + id + " не найден"));
    }
}