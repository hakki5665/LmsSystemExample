package lms_system;

import com.fasterxml.jackson.databind.JsonNode;
import lms_system.dto.GroupDto;
import lms_system.entity.Group;
import lms_system.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupControllerIT extends BaseIT {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void shouldCreateGroupSuccessfully() throws Exception {
        GroupDto newGroup = GroupDto.builder()
                .name("Уникальная Секция Тенниса")
                .build();

        MvcResult result = mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGroup)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        GroupDto responseDto = objectMapper.readValue(responseJson, GroupDto.class);
        assertThat(responseDto.getName()).isEqualTo("Уникальная Секция Тенниса");
        assertThat(responseDto.getId()).isNotNull();

        Group saved = groupRepository.findById(responseDto.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Уникальная Секция Тенниса");
        assertThat(saved.isDeleted()).isFalse();
    }

    @Test
    void shouldGetGroupsWithPagination() throws Exception {
        groupRepository.save(Group.builder().name("Тестовая группа").deleted(false).build());

        String responseContent = mockMvc.perform(get("/api/groups?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JsonNode pageResponse = objectMapper.readValue(responseContent, JsonNode.class);
        String groupName = pageResponse.get("content").get(0).get("name").asText();
        assertThat(groupName).isEqualTo("Тестовая группа");
    }

    @Test
    void shouldDeleteGroupLogically() throws Exception {
        Group group = groupRepository.save(Group.builder().name("Группа для удаления").deleted(false).build());
        Long id = group.getId();

        mockMvc.perform(delete("/api/groups/{id}", id))
                .andExpect(status().isNoContent());

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM groups WHERE id = ?",
                Boolean.class,
                id
        );
        assertThat(isDeleted).isTrue();
    }
}