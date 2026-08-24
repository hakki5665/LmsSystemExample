package lms_system;

import com.fasterxml.jackson.databind.JsonNode;
import lms_system.dto.CourseDto;
import lms_system.entity.Course;
import lms_system.entity.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseControllerIT extends BaseIT {

    @Test
    void shouldCreateCourseSuccessfully() throws Exception {
        Teacher teacher = teacherRepository.save(
                Teacher.builder().firstName("John").lastName("Doe").deleted(false).build()
        );
        CourseDto newCourse = CourseDto.builder()
                .name("Python Basics")
                .description("Основы синтаксиса")
                .teacherId(teacher.getId())
                .build();

        MvcResult result = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CourseDto responseDto = objectMapper.readValue(responseJson, CourseDto.class);
        assertThat(responseDto.getName()).isEqualTo("Python Basics");
        assertThat(responseDto.getTeacherId()).isEqualTo(teacher.getId());
        assertThat(responseDto.getId()).isNotNull();

        Course saved = courseRepository.findById(responseDto.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Python Basics");
        assertThat(saved.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(saved.isDeleted()).isFalse();
    }

    @Test
    void shouldGetCoursesWithPagination() throws Exception {
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("John").lastName("Doe").deleted(false).build());
        courseRepository.save(Course.builder().name("Java Advanced").teacher(teacher).deleted(false).build());

        String responseContent = mockMvc.perform(get("/api/courses?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JsonNode pageResponse = objectMapper.readValue(responseContent, JsonNode.class);
        String courseName = pageResponse.get("content").get(0).get("name").asText();
        assertThat(courseName).isEqualTo("Java Advanced");
    }

    @Test
    void shouldDeleteCourseLogically() throws Exception {
        Teacher teacher = teacherRepository.save(
                Teacher.builder().firstName("Jane").lastName("Doe").deleted(false).build()
        );
        Course course = courseRepository.save(Course.builder()
                .name("Spring Boot")
                .teacher(teacher)
                .deleted(false)
                .build());

        mockMvc.perform(delete("/api/courses/{id}", course.getId()))
                .andExpect(status().isNoContent());

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM courses WHERE id = ?",
                Boolean.class,
                course.getId()
        );
        assertThat(isDeleted).isTrue();
    }

    @Test
    void shouldReturnBadRequestWhenTeacherNotFound() throws Exception {
        CourseDto newCourse = CourseDto.builder()
                .name("Invalid Course")
                .teacherId(999L)
                .build();

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isNotFound());
    }
}