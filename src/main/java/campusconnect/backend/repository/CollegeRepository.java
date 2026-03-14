package campusconnect.backend.repository;

import campusconnect.backend.entity.College;
import campusconnect.backend.entity.User;
import campusconnect.backend.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {

    Optional<College> findByUser(User user);

    List<College> findByVerificationStatus(VerificationStatus verificationStatus);
}