package lms_system.service;

import lms_system.dto.TimetableDto;
import lms_system.entity.Course;
import lms_system.entity.Group;
import lms_system.entity.Teacher;
import lms_system.entity.Timetable;
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
        Group group = groupRepository.getOrThrow(dto.getGroupId());
        Teacher teacher = teacherRepository.getOrThrow(dto.getTeacherId());
        Course course = courseRepository.getOrThrow(dto.getCourseId());

        Timetable timetable = timetableMapper.toEntity(dto);
        timetable.setGroup(group);
        timetable.setTeacher(teacher);
        timetable.setCourse(course);

        return timetableMapper.toDto(timetableRepository.save(timetable));
    }

    public List<TimetableDto> getScheduleForGroup(Long groupId) {
        groupRepository.getOrThrow(groupId);
        return timetableRepository.findByGroupId(groupId).stream()
                .map(timetableMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TimetableDto> getScheduleForTeacher(Long teacherId) {
        teacherRepository.getOrThrow(teacherId);
        return timetableRepository.findByTeacherId(teacherId).stream()
                .map(timetableMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimetableDto update(Long id, TimetableDto dto) {
        Timetable existing = timetableRepository.getOrThrow(id);
        Group group = groupRepository.getOrThrow(dto.getGroupId());
        Teacher teacher = teacherRepository.getOrThrow(dto.getTeacherId());
        Course course = courseRepository.getOrThrow(dto.getCourseId());

        existing.setGroup(group);
        existing.setTeacher(teacher);
        existing.setCourse(course);
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());

        return timetableMapper.toDto(timetableRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Timetable timetable = timetableRepository.getOrThrow(id);
        timetable.setDeleted(true);
        timetableRepository.save(timetable);
    }
}