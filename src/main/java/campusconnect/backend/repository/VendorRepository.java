package campusconnect.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import campusconnect.backend.entity.Vendor;
import campusconnect.backend.entity.User;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByUser(User user);

}