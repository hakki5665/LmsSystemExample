package lms_system;

import com.fasterxml.jackson.databind.JsonNode;
import lms_system.dto.TeacherDto;
import lms_system.entity.Teacher;
import lms_system.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeacherControllerIT extends BaseIT {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void shouldCreateTeacherSuccessfully() throws Exception {
        TeacherDto newTeacher = TeacherDto.builder().firstName("Иван").lastName("Иванов").build();

        MvcResult result = mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTeacher)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        TeacherDto responseDto = objectMapper.readValue(responseJson, TeacherDto.class);
        assertThat(responseDto.getFirstName()).isEqualTo("Иван");

        Teacher saved = teacherRepository.findById(responseDto.getId()).orElseThrow();
        assertThat(saved.getFirstName()).isEqualTo("Иван");
    }

    @Test
    void shouldGetTeachersWithPagination() throws Exception {
        teacherRepository.save(Teacher.builder().firstName("Вячеслав").lastName("Брызгачев").deleted(false).build());

        String responseContent = mockMvc.perform(get("/api/teachers?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JsonNode pageResponse = objectMapper.readValue(responseContent, JsonNode.class);
        String firstName = pageResponse.get("content").get(0).get("firstName").asText();
        assertThat(firstName).isEqualTo("Вячеслав");
    }

    @Test
    void shouldDeleteTeacherLogically() throws Exception {
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Петр").lastName("Сидоров").deleted(false).build());
        Long id = teacher.getId();

        mockMvc.perform(delete("/api/teachers/{id}", id))
                .andExpect(status().isNoContent());

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM teachers WHERE id = ?",
                Boolean.class,
                id
        );
        assertThat(isDeleted).isTrue();
    }
}