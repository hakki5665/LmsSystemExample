package lms_system.service;

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

        Set<Group> groups = fetchGroupsByIds(dto.getGroupIds());
        student.setGroups(groups);

        return studentMapper.toDto(studentRepository.save(student));
    }

    public Page<StudentDto> getAll(Pageable pageable) {
        return studentRepository.findAll(pageable).map(studentMapper::toDto);
    }

    public StudentDto getById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Студент с id " + id + " не найден"));
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentDto update(Long id, StudentDto dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Студент с id " + id + " не найден"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());

        Set<Group> groups = fetchGroupsByIds(dto.getGroupIds());
        existing.setGroups(groups);

        return studentMapper.toDto(studentRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Студент с id " + id + " не найден");
        }
        studentRepository.deleteById(id);
    }

    private Set<Group> fetchGroupsByIds(Set<Long> ids) {
        List<Group> foundGroups = groupRepository.findAllById(ids);
        if (foundGroups.size() != ids.size()) {
            throw new ResourceNotFoundException("Одна или несколько указанных групп не найдены");
        }
        return new HashSet<>(foundGroups);
    }
}