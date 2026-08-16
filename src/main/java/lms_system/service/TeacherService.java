package lms_system.service;

import lms_system.dto.PageResponse;
import lms_system.dto.TeacherDto;
import lms_system.entity.Teacher;
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
        return teacherMapper.toDto(teacherRepository.save(teacher));
    }

    public PageResponse<TeacherDto> getAll(Pageable pageable) {
        Page<TeacherDto> page = teacherRepository.findAll(pageable).map(teacherMapper::toDto);
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getTotalPages(), page.getTotalElements());
    }

    public TeacherDto getById(Long id) {
        return teacherMapper.toDto(teacherRepository.getOrThrow(id));
    }

    @Transactional
    public TeacherDto update(Long id, TeacherDto dto) {
        Teacher existing = teacherRepository.getOrThrow(id);
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        return teacherMapper.toDto(teacherRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Teacher teacher = teacherRepository.getOrThrow(id);
        teacher.setDeleted(true);
        teacherRepository.save(teacher);
    }
}