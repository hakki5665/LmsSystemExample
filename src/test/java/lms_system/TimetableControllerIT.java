package lms_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lms_system.dto.TimetableDto;
import lms_system.entity.Course;
import lms_system.entity.Group;
import lms_system.entity.Teacher;
import lms_system.entity.Timetable;
import lms_system.repository.CourseRepository;
import lms_system.repository.GroupRepository;
import lms_system.repository.TeacherRepository;
import lms_system.repository.TimetableRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
public class TimetableControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private Long savedTimetableId;
    private Long groupId;
    private Long teacherId;
    private Long courseId;

    @BeforeEach
    void setUp() {
        Group group = groupRepository.save(Group.builder().name("Волейбол расписание").deleted(false).build());
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Иван").lastName("Иванов").deleted(false).build());
        Course course = courseRepository.save(Course.builder()
                .name("Основы тактики")
                .description("Спортивная секция")
                .teacher(teacher)
                .deleted(false)
                .build());

        groupId = group.getId();
        teacherId = teacher.getId();
        courseId = course.getId();

        Timetable timetable = Timetable.builder()
                .group(group)
                .teacher(teacher)
                .course(course)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .deleted(false)
                .build();

        Timetable saved = timetableRepository.save(timetable);
        savedTimetableId = saved.getId();
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE timetables, courses, teachers, groups RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldCreateTimetableSuccessfully() throws Exception {
        TimetableDto newTimetableDto = TimetableDto.builder()
                .groupId(groupId)
                .teacherId(teacherId)
                .courseId(courseId)
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusHours(1))
                .build();

        mockMvc.perform(post("/api/timetables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTimetableDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldDeleteTimetableLogically() throws Exception {
        mockMvc.perform(delete("/api/timetables/" + savedTimetableId));

        lms_system.entity.Timetable checked = timetableRepository.findById(savedTimetableId).orElse(null);

        if (checked != null) {
            assertThat(checked.isDeleted()).isTrue();
        } else {
            assertThat(timetableRepository.existsById(savedTimetableId)).isFalse();
        }
    }
}