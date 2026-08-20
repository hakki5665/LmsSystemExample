package lms_system;

import com.fasterxml.jackson.core.type.TypeReference;
import lms_system.dto.TimetableDto;
import lms_system.entity.Course;
import lms_system.entity.Group;
import lms_system.entity.Teacher;
import lms_system.entity.Timetable;
import lms_system.repository.CourseRepository;
import lms_system.repository.GroupRepository;
import lms_system.repository.TeacherRepository;
import lms_system.repository.TimetableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TimetableControllerIT extends BaseIT {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldCreateTimetableSuccessfully() throws Exception {
        Group group = groupRepository.save(Group.builder().name("Волейбол расписание").deleted(false).build());
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Иван").lastName("Иванов").deleted(false).build());
        Course course = courseRepository.save(Course.builder().name("Основы тактики").teacher(teacher).deleted(false).build());

        TimetableDto newTimetable = TimetableDto.builder()
                .groupId(group.getId())
                .teacherId(teacher.getId())
                .courseId(course.getId())
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusHours(1))
                .build();

        MvcResult result = mockMvc.perform(post("/api/timetables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTimetable)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        TimetableDto responseDto = objectMapper.readValue(responseJson, TimetableDto.class);
        assertThat(responseDto.getGroupId()).isEqualTo(group.getId());
        assertThat(responseDto.getId()).isNotNull();

        Timetable saved = timetableRepository.findById(responseDto.getId()).orElseThrow();
        assertThat(saved.getGroup().getId()).isEqualTo(group.getId());
        assertThat(saved.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(saved.isDeleted()).isFalse();
    }

    @Test
    void shouldGetScheduleForGroupSuccessfully() throws Exception {
        Group group = groupRepository.save(Group.builder().name("Секция Шахмат").deleted(false).build());
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Петр").lastName("Петров").deleted(false).build());
        Course course = courseRepository.save(Course.builder().name("Дебюты").teacher(teacher).deleted(false).build());

        timetableRepository.save(Timetable.builder()
                .group(group)
                .teacher(teacher)
                .course(course)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .deleted(false)
                .build());

        String responseContent = mockMvc.perform(get("/api/timetables/group/{groupId}", group.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        List<TimetableDto> schedule = objectMapper.readValue(responseContent, new TypeReference<List<TimetableDto>>() {});

        assertThat(schedule).isNotEmpty();
        assertThat(schedule.get(0).getGroupId()).isEqualTo(group.getId());
        assertThat(schedule.get(0).getCourseId()).isEqualTo(course.getId());
    }

    @Test
    void shouldDeleteTimetableLogically() throws Exception {
        Group group = groupRepository.save(Group.builder().name("Группа расписание").deleted(false).build());
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Анна").lastName("Сергеева").deleted(false).build());
        Course course = courseRepository.save(Course.builder().name("Продвинутый курс").teacher(teacher).deleted(false).build());

        Timetable timetable = timetableRepository.save(Timetable.builder()
                .group(group)
                .teacher(teacher)
                .course(course)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .deleted(false)
                .build());

        mockMvc.perform(delete("/api/timetables/{id}", timetable.getId()))
                .andExpect(status().isNoContent());

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM timetables WHERE id = ?",
                Boolean.class,
                timetable.getId()
        );
        assertThat(isDeleted).isTrue();
    }
}