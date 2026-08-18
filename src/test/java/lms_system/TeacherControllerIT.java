package lms_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lms_system.dto.TeacherDto;
import lms_system.entity.Teacher;
import lms_system.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerIT {
    static {
        System.setProperty("DOCKER_HOST", "tcp://127.0.0.1:2375");
        System.setProperty("TESTCONTAINERS_RYUK_DISABLED", "true");
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("lms_test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Иван"));
    }

    @Test
    void shouldGetTeachersWithPagination() throws Exception {
        mockMvc.perform(get("/api/teachers")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].lastName").value("Брызгачев"));
    }


    @Test
    void shouldDeleteTeacherLogically() throws Exception {
        mockMvc.perform(delete("/api/teachers/" + savedTeacherId))
                .andExpect(status().isNoContent());

        String responseContent = mockMvc.perform(get("/api/teachers?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

        assertThat(responseContent).doesNotContain("\"id\":" + savedTeacherId);
    }
}