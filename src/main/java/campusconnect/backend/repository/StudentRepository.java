package campusconnect.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import campusconnect.backend.entity.Student;
import campusconnect.backend.entity.User;

public interface StudentRepository extends JpaRepository<Student , Long> {

    Optional<Student> findByUser(User user);

}