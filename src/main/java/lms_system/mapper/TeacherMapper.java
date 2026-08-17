package lms_system.mapper;

import lms_system.dto.TeacherDto;
import lms_system.entity.Teacher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    TeacherDto toDto(Teacher teacher);
    Teacher toEntity(TeacherDto teacherDto);
}