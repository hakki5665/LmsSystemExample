package lms_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lms_system.dto.TimetableDto;
import lms_system.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
@Tag(name = "Расписание", description = "Управление временем занятий и просмотр графиков")
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    @Operation(summary = "Назначить время проведения курса для определённой группы")
    public ResponseEntity<TimetableDto> create(@Valid @RequestBody TimetableDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить время проведения курса для определённой группы")
    public ResponseEntity<TimetableDto> update(@PathVariable Long id, @Valid @RequestBody TimetableDto dto) {
        return ResponseEntity.ok(timetableService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить время проведения курса")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timetableService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "Просмотреть график проведения курсов для конкретной группы")
    public ResponseEntity<List<TimetableDto>> getScheduleForGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(timetableService.getScheduleForGroup(groupId));
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Просмотреть график занятий для конкретного преподавателя")
    public ResponseEntity<List<TimetableDto>> getScheduleForTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(timetableService.getScheduleForTeacher(teacherId));
    }
}