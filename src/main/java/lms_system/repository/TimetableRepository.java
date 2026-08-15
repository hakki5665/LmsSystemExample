package lms_system.repository;

import lms_system.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findByGroupId(Long groupId);

    List<Timetable> findByTeacherId(Long teacherId);

    @Modifying
    @Query("DELETE FROM Timetable t WHERE t.startTime < :targetDate")
    void deleteOlderThan(@Param("targetDate") LocalDateTime targetDate);
}