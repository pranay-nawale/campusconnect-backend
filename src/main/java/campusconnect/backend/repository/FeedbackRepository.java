package campusconnect.backend.repository;

import campusconnect.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    boolean existsByStudentAndEvent(Student student, EventRequest event);

    boolean existsByCollegeAndEvent(College college, EventRequest event);

    List<Feedback> findByEvent(EventRequest event);

    List<Feedback> findByStudent(Student student);
}