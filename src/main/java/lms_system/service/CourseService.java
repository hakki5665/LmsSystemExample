package lms_system.service;

import lms_system.dto.CourseDto;
import lms_system.entity.Course;
import lms_system.entity.Teacher;
import lms_system.exception.ResourceNotFoundException;
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
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Преподаватель с id " + dto.getTeacherId() + " не найден"));

        Course course = courseMapper.toEntity(dto);
        course.setTeacher(teacher);
        return courseMapper.toDto(courseRepository.save(course));
    }

    public Page<CourseDto> getAll(Pageable pageable) {
        return courseRepository.findAll(pageable).map(courseMapper::toDto);
    }

    public CourseDto getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс с id " + id + " не найден"));
        return courseMapper.toDto(course);
    }

    @Transactional
    public CourseDto update(Long id, CourseDto dto) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Курс с id " + id + " не найден"));

        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Преподаватель с id " + dto.getTeacherId() + " не найден"));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setTeacher(teacher);

        return courseMapper.toDto(courseRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Курс с id " + id + " не найден");
        }
        courseRepository.deleteById(id);
    }
}