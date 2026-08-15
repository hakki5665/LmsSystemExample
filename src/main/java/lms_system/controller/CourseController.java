package lms_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lms_system.dto.CourseDto;
import lms_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Курсы", description = "Управление учебными курсами")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Добавить новый курс (назначить преподавателя)")
    public ResponseEntity<CourseDto> create(@Valid @RequestBody CourseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Получить список курсов (с пагинацией)")
    public ResponseEntity<Page<CourseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(courseService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить курс по ID")
    public ResponseEntity<CourseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить данные курса")
    public ResponseEntity<CourseDto> update(@PathVariable Long id, @Valid @RequestBody CourseDto dto) {
        return ResponseEntity.ok(courseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить курс")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}