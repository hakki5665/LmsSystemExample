package lms_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lms_system.dto.StudentDto;
import lms_system.entity.Group;
import lms_system.entity.Student;
import lms_system.repository.GroupRepository;
import lms_system.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
public class StudentControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private lms_system.repository.TimetableRepository timetableRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private Long savedStudentId;
    private Long savedGroupId;

    @BeforeEach
    void setUp() {
        Group group = Group.builder()
                .name("Волейбол")
                .deleted(false)
                .build();
        Group savedGroup = groupRepository.save(group);
        savedGroupId = savedGroup.getId();

        Student student = Student.builder()
                .firstName("Алексей")
                .lastName("Петров")
                .deleted(false)
                .build();
        Student savedStudent = studentRepository.save(student);
        savedStudentId = savedStudent.getId();

        savedStudent.setGroups(new java.util.HashSet<>(Set.of(savedGroup)));
        studentRepository.save(savedStudent);
    }

    @AfterEach
    void tearDown() {
        timetableRepository.deleteAll();
        studentRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    void shouldCreateStudentSuccessfully() throws Exception {
        StudentDto newStudentDto = StudentDto.builder()
                .firstName("Мария")
                .lastName("Сидорова")
                .groupIds(Set.of(savedGroupId))
                .build();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudentDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetStudentsWithPagination() throws Exception {
        String responseContent = mockMvc.perform(get("/api/students?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseContent).contains("Алексей");
        assertThat(responseContent).contains("Петров");
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void shouldDeleteStudentLogically() throws Exception {
        mockMvc.perform(delete("/api/students/" + savedStudentId))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        Student checkedStudent = (Student) entityManager.createNativeQuery(
                        "SELECT * FROM students WHERE id = ?1", Student.class)
                .setParameter(1, savedStudentId)
                .getResultStream()
                .findFirst()
                .orElse(null);

        assertThat(checkedStudent).isNotNull();
        assertThat(checkedStudent.isDeleted()).isTrue();
    }
}