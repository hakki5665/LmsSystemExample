package lms_system.service;

import lms_system.dto.TeacherDto;
import lms_system.entity.Teacher;
import lms_system.exception.ResourceNotFoundException;
import lms_system.mapper.TeacherMapper;
import lms_system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @Transactional
    public TeacherDto create(TeacherDto dto) {
        Teacher teacher = teacherMapper.toEntity(dto);
        Teacher saved = teacherRepository.save(teacher);
        return teacherMapper.toDto(saved);
    }

    public Page<TeacherDto> getAll(Pageable pageable) {
        return teacherRepository.findAll(pageable)
                .map(teacherMapper::toDto);
    }

    public TeacherDto getById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Преподаватель с id " + id + " не найден"));
        return teacherMapper.toDto(teacher);
    }

    @Transactional
    public TeacherDto update(Long id, TeacherDto dto) {
        Teacher existing = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Преподаватель с id " + id + " не найден"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());

        return teacherMapper.toDto(teacherRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Преподаватель с id " + id + " не найден");
        }
        teacherRepository.deleteById(id);
    }
}