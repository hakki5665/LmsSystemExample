package lms_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lms_system.dto.CourseDto;
import lms_system.entity.Course;
import lms_system.repository.CourseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
public class CourseControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private lms_system.repository.TeacherRepository teacherRepository;

    private Long savedCourseId;
    private Long savedTeacherId;

    @BeforeEach
    void setUp() {
        lms_system.entity.Teacher teacher = teacherRepository.save(
                lms_system.entity.Teacher.builder().firstName("Иван").lastName("Иванов").deleted(false).build()
        );
        savedTeacherId = teacher.getId();

        Course course = Course.builder()
                .name("Java Advanced")
                .description("Глубокое изучение Spring Boot")
                .teacher(teacher)
                .build();
        Course saved = courseRepository.save(course);
        savedCourseId = saved.getId();
    }

    @AfterEach
    void tearDown() {
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    void shouldCreateCourseSuccessfully() throws Exception {
        CourseDto newCourse = CourseDto.builder()
                .name("Python Basics")
                .description("Основы синтаксиса")
                .teacherId(savedTeacherId)
                .build();

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetCoursesWithPagination() throws Exception {
        String responseContent = mockMvc.perform(get("/api/courses?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseContent).contains("Java Advanced");
    }
}