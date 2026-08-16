package lms_system.service;

import lms_system.dto.TimetableDto;
import lms_system.entity.Course;
import lms_system.entity.Group;
import lms_system.entity.Teacher;
import lms_system.entity.Timetable;
import lms_system.exception.ResourceNotFoundException;
import lms_system.mapper.TimetableMapper;
import lms_system.repository.CourseRepository;
import lms_system.repository.GroupRepository;
import lms_system.repository.TeacherRepository;
import lms_system.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final TimetableMapper timetableMapper;

    @Transactional
    public TimetableDto create(TimetableDto dto) {
        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Группа с id " + dto.getGroupId() + " не найдена"));
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Преподаватель с id " + dto.getTeacherId() + " не найден"));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Курс с id " + dto.getCourseId() + " не найден"));

        Timetable timetable = timetableMapper.toEntity(dto);
        timetable.setGroup(group);
        timetable.setTeacher(teacher);
        timetable.setCourse(course);

        return timetableMapper.toDto(timetableRepository.save(timetable));
    }

    public List<TimetableDto> getScheduleForGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Группа с id " + groupId + " не найдена");
        }
        return timetableRepository.findByGroupId(groupId).stream()
                .map(timetableMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TimetableDto> getScheduleForTeacher(Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Преподаватель с id " + teacherId + " не найден");
        }
        return timetableRepository.findByTeacherId(teacherId).stream()
                .map(timetableMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimetableDto update(Long id, TimetableDto dto) {
        Timetable existing = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Запись расписания с id " + id + " не найдена"));

        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Группа с id " + dto.getGroupId() + " не найдена"));
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Преподаватель с id " + dto.getTeacherId() + " не найден"));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Курс с id " + dto.getCourseId() + " не найден"));

        existing.setGroup(group);
        existing.setTeacher(teacher);
        existing.setCourse(course);
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());

        return timetableMapper.toDto(timetableRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!timetableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Запись расписания с id " + id + " не найдена");
        }
        timetableRepository.deleteById(id);
    }
}