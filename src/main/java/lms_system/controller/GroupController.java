package lms_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lms_system.dto.GroupDto;
import lms_system.dto.PageResponse;
import lms_system.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Группы")
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    @Operation(summary = "Добавить группу")
    public ResponseEntity<GroupDto> create(@Valid @RequestBody GroupDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Получить список групп")
    public ResponseEntity<PageResponse<GroupDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(groupService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> update(@PathVariable Long id, @Valid @RequestBody GroupDto dto) {
        return ResponseEntity.ok(groupService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}