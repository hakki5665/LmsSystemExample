package lms_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lms_system.dto.TeacherDto;
import lms_system.entity.Teacher;
import lms_system.repository.TeacherRepository;
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
public class TeacherControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private lms_system.repository.CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long savedTeacherId;

    @BeforeEach
    void setUp() {
        Teacher teacher = Teacher.builder()
                .firstName("Вячеслав")
                .lastName("Брызгачев")
                .deleted(false)
                .build();

        Teacher saved = teacherRepository.save(teacher);
        savedTeacherId = saved.getId();
    }

    @AfterEach
    void tearDown() {
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    void shouldCreateTeacherSuccessfully() throws Exception {
        TeacherDto newTeacher = TeacherDto.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTeacher)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetTeachersWithPagination() throws Exception {
        String responseContent = mockMvc.perform(get("/api/teachers?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseContent).contains("Вячеслав");
        assertThat(responseContent).contains("Брызгачев");
    }

    @Test
    void shouldDeleteTeacherLogically() throws Exception {
        mockMvc.perform(delete("/api/teachers/" + savedTeacherId))
                .andExpect(status().isNoContent());

        String responseContent = mockMvc.perform(get("/api/teachers?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseContent).doesNotContain("\"id\":" + savedTeacherId);
    }
}