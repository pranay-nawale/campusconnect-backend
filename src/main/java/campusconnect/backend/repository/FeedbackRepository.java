package campusconnect.backend.repository;

import campusconnect.backend.entity.College;
import campusconnect.backend.entity.Feedback;
import campusconnect.backend.entity.Student;
import campusconnect.backend.entity.EventRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    boolean existsByStudentAndEvent(Student student, EventRequest event);

    boolean existsByCollegeAndEvent(College college, EventRequest event);

    List<Feedback> findByEvent(EventRequest event);
}