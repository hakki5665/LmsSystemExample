package lms_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDto {
    private Long id;

    @NotBlank(message = "Название группы не должно быть пустым")
    private String name;
}