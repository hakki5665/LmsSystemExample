package lms_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDto {
    private Long id;

    @NotBlank(message = "Имя преподавателя не должно быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия преподавателя не должна быть пустой")
    private String lastName;
}