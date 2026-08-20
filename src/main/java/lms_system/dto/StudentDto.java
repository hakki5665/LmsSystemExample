package lms_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private Long id;

    @NotBlank(message = "Имя студента не должно быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия студента не должна быть пустой")
    private String lastName;

    @NotEmpty(message = "Студент должен быть привязан минимум к одной группе")
    private Set<Long> groupIds;
}