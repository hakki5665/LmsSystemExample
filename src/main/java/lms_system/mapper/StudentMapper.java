package lms_system.mapper;

import lms_system.dto.StudentDto;
import lms_system.entity.Group;
import lms_system.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "groupIds", source = "groups", qualifiedByName = "mapGroupsToIds")
    StudentDto toDto(Student student);

    @Mapping(target = "groups", ignore = true)
    Student toEntity(StudentDto studentDto);

    @Named("mapGroupsToIds")
    default Set<Long> mapGroupsToIds(Set<Group> groups) {
        if (groups == null) return null;
        return groups.stream()
                .map(Group::getId)
                .collect(Collectors.toSet());
    }
}