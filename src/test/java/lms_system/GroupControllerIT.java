package lms_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lms_system.dto.GroupDto;
import lms_system.entity.Group;
import lms_system.repository.GroupRepository;
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
public class GroupControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Autowired
    private lms_system.repository.TimetableRepository timetableRepository;

    private Long savedGroupId;
    private String uniqueGroupName;

    @BeforeEach
    void setUp() {
        uniqueGroupName = "Группа " + java.util.UUID.randomUUID().toString().substring(0, 8);

        Group group = Group.builder()
                .name(uniqueGroupName)
                .deleted(false)
                .build();
        Group saved = groupRepository.save(group);
        savedGroupId = saved.getId();
    }

    @AfterEach
    void tearDown() {
        timetableRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    void shouldCreateGroupSuccessfully() throws Exception {
        GroupDto newGroupDto = GroupDto.builder()
                .name("Уникальная Секция Тенниса")
                .build();

        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGroupDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetGroupsWithPagination() throws Exception {
        String responseContent = mockMvc.perform(get("/api/groups?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseContent).contains(uniqueGroupName);
    }

    @Test
    void shouldDeleteGroupLogically() throws Exception {
        mockMvc.perform(delete("/api/groups/" + savedGroupId))
                .andExpect(status().isNoContent());

        String responseContent = mockMvc.perform(get("/api/groups?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(responseContent).doesNotContain("\"id\":" + savedGroupId);
    }
}