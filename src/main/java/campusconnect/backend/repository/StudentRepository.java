package campusconnect.backend.repository;

import java.util.List;
import java.util.Optional;

import campusconnect.backend.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import campusconnect.backend.entity.Student;
import campusconnect.backend.entity.User;

public interface StudentRepository extends JpaRepository<Student , Long> {

    Optional<Student> findByUser(User user);

    List<Student> findByVerificationStatus (VerificationStatus status);

    long countByVerificationStatus(VerificationStatus status);

    Optional<Student> findByEmail(String email);

}