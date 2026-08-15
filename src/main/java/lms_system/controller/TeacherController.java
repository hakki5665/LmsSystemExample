package lms_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lms_system.dto.TeacherDto;
import lms_system.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Преподаватели", description = "Управление преподавателями учебного центра")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @Operation(summary = "Добавить нового преподавателя")
    public ResponseEntity<TeacherDto> create(@Valid @RequestBody TeacherDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Получить список преподавателей (с пагинацией)")
    public ResponseEntity<Page<TeacherDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(teacherService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить преподавателя по ID")
    public ResponseEntity<TeacherDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить данные преподавателя")
    public ResponseEntity<TeacherDto> update(@PathVariable Long id, @Valid @RequestBody TeacherDto dto) {
        return ResponseEntity.ok(teacherService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить преподавателя")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}