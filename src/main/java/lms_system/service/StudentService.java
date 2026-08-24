package lms_system.service;

import lms_system.dto.PageResponse;
import lms_system.dto.StudentDto;
import lms_system.entity.Group;
import lms_system.entity.Student;
import lms_system.exception.ResourceNotFoundException;
import lms_system.mapper.StudentMapper;
import lms_system.repository.GroupRepository;
import lms_system.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentDto create(StudentDto dto) {
        Student student = studentMapper.toEntity(dto);
        student.setGroups(fetchGroupsByIds(dto.getGroupIds()));
        return studentMapper.toDto(studentRepository.save(student));
    }

    public PageResponse<StudentDto> getAll(Pageable pageable) {
        Page<StudentDto> page = studentRepository.findAll(pageable).map(studentMapper::toDto);
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getTotalPages(), page.getTotalElements());
    }

    public StudentDto getById(Long id) {
        return studentMapper.toDto(studentRepository.getOrThrow(id));
    }

    @Transactional
    public StudentDto update(Long id, StudentDto dto) {
        Student existing = studentRepository.getOrThrow(id);
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setGroups(fetchGroupsByIds(dto.getGroupIds()));
        return studentMapper.toDto(studentRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.getOrThrow(id);
        student.setDeleted(true);
        studentRepository.save(student);
    }

    private Set<Group> fetchGroupsByIds(Set<Long> ids) {
        List<Group> foundGroups = groupRepository.findAllById(ids);
        if (foundGroups.size() != ids.size()) {
            throw new ResourceNotFoundException("Одна или несколько указанных групп не найдены");
        }
        return new HashSet<>(foundGroups);
    }
}