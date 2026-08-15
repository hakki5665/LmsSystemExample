package lms_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableDto {
    private Long id;

    @NotNull(message = "Группа обязательна для расписания")
    private Long groupId;

    @NotNull(message = "Преподаватель обязателен для расписания")
    private Long teacherId;

    @NotNull(message = "Курс обязателен для расписания")
    private Long courseId;

    @NotNull(message = "Дата начала занятия обязательна")
    private LocalDateTime startTime;

    @NotNull(message = "Дата окончания занятия обязательна")
    private LocalDateTime endTime;
}