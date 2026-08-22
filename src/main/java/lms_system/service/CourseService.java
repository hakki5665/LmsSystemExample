package lms_system.service;

import lms_system.dto.CourseDto;
import lms_system.dto.PageResponse;
import lms_system.entity.Course;
import lms_system.entity.Teacher;
import lms_system.mapper.CourseMapper;
import lms_system.repository.CourseRepository;
import lms_system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseMapper courseMapper;

    @Transactional
    public CourseDto create(CourseDto dto) {
        Teacher teacher = teacherRepository.getOrThrow(dto.getTeacherId());

        Course course = courseMapper.toEntity(dto);
        course.setTeacher(teacher);
        return courseMapper.toDto(courseRepository.save(course));
    }

    public PageResponse<CourseDto> getAll(Pageable pageable) {
        Page<CourseDto> page = courseRepository.findAll(pageable).map(courseMapper::toDto);
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getTotalPages(), page.getTotalElements());
    }

    public CourseDto getById(Long id) {
        return courseMapper.toDto(courseRepository.getOrThrow(id));
    }

    @Transactional
    public CourseDto update(Long id, CourseDto dto) {
        Course existing = courseRepository.getOrThrow(id);
        Teacher teacher = teacherRepository.getOrThrow(dto.getTeacherId());

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setTeacher(teacher);

        return courseMapper.toDto(courseRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.getOrThrow(id);
        course.setDeleted(true);
        courseRepository.save(course);
    }
}