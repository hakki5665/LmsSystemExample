package lms_system;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LmsSystemApplicationTests extends BaseIT {

	@Test
	void contextOkTest() {
		assertNotNull(environment);
	}

	@Test
	void checkHealth() throws Exception {
		mockMvc.perform(get("/actuator/health")
						.characterEncoding(StandardCharsets.UTF_8.name()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json("{\"status\":\"UP\"}"));
	}
}