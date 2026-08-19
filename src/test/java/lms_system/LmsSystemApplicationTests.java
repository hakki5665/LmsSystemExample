package lms_system;

import lms_system.controller.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class LmsSystemApplicationTests extends BaseIT {

	@Autowired
	private TeacherController teacherController;

	@Autowired
	private StudentController studentController;

	@Autowired
	private CourseController courseController;

	@Autowired
	private GroupController groupController;

	@Autowired
	private TimetableController timetableController;

	@Test
	void contextLoads() {
		assertThat(teacherController).isNotNull();
		assertThat(studentController).isNotNull();
		assertThat(courseController).isNotNull();
		assertThat(groupController).isNotNull();
		assertThat(timetableController).isNotNull();
	}
}