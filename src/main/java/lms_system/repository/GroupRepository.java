package lms_system.repository;

import lms_system.entity.Group;
import lms_system.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    default Group getOrThrow(Long id) {
        return findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Группа с id " + id + " не найдена"));
    }
}