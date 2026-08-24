package lms_system.scheduler;

import lms_system.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TimetableCleanupScheduler {

    private final TimetableRepository timetableRepository;

    @Scheduled(cron = "${app.timetable-cleanup-cron}")
    @SchedulerLock(name = "TimetableCleanup_lock", lockAtLeastFor = "1m", lockAtMostFor = "5m")
    @Transactional
    public void removeOldTimetables() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        timetableRepository.deleteOlderThan(oneYearAgo);
    }
}