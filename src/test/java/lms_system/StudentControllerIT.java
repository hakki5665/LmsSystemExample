package lms_system;

import com.fasterxml.jackson.databind.JsonNode;
import lms_system.dto.StudentDto;
import lms_system.entity.Group;
import lms_system.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentControllerIT extends BaseIT {

    @Test
    void shouldCreateStudentSuccessfully() throws Exception {
        Group group = groupRepository.save(Group.builder().name("Волейбол").deleted(false).build());
        StudentDto newStudent = StudentDto.builder()
                .firstName("Мария")
                .lastName("Сидорова")
                .groupIds(Set.of(group.getId()))
                .build();

        MvcResult result = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        StudentDto responseDto = objectMapper.readValue(responseJson, StudentDto.class);
        assertThat(responseDto.getFirstName()).isEqualTo("Мария");
        assertThat(responseDto.getId()).isNotNull();

        Student saved = studentRepository.findById(responseDto.getId()).orElseThrow();
        assertThat(saved.getFirstName()).isEqualTo("Мария");
    }

    @Test
    void shouldGetStudentsWithPagination() throws Exception {
        studentRepository.save(Student.builder().firstName("Алексей").lastName("Петров").deleted(false).build());

        String responseContent = mockMvc.perform(get("/api/students?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JsonNode pageResponse = objectMapper.readValue(responseContent, JsonNode.class);
        String firstName = pageResponse.get("content").get(0).get("firstName").asText();
        assertThat(firstName).isEqualTo("Алексей");
    }

    @Test
    void shouldDeleteStudentLogically() throws Exception {
        Student student = studentRepository.save(Student.builder().firstName("Анна").lastName("Иванова").deleted(false).build());
        Long id = student.getId();

        mockMvc.perform(delete("/api/students/{id}", id))
                .andExpect(status().isNoContent());

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM students WHERE id = ?",
                Boolean.class,
                id
        );
        assertThat(isDeleted).isTrue();
    }
}