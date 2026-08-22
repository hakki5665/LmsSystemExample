package lms_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private Long id;

    @NotBlank(message = "Название курса не должно быть пустым")
    private String name;

    private String description;

    @NotNull(message = "Курсу должен быть назначен преподаватель")
    private Long teacherId;
}