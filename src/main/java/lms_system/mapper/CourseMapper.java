package lms_system.mapper;

import lms_system.dto.CourseDto;
import lms_system.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "teacherId", source = "teacher.id")
    CourseDto toDto(Course course);

    @Mapping(target = "teacher", ignore = true)
    Course toEntity(CourseDto courseDto);
}