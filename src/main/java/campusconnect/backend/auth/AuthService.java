package campusconnect.backend.auth;

import campusconnect.backend.dto.AuthResponse;
import campusconnect.backend.dto.LoginRequest;
import campusconnect.backend.dto.RegisterRequest;
import campusconnect.backend.entity.Role;
import campusconnect.backend.entity.User;
import campusconnect.backend.notification.NotificationFacade;
import campusconnect.backend.notification.NotificationType;
import campusconnect.backend.repository.UserRepository;
import campusconnect.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final NotificationFacade notificationFacade;


    public User register(RegisterRequest request) {

        if (request.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin cannot register");
        }

        if (request.getRole() == null) {
            throw new RuntimeException("Role is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());

//        notificationFacade.notifyUser(
//                user,
//                "Welcome to CampusConnect 🎉",
//                NotificationType.USER_REGISTERED,
//                vars,
//                true,
//                null
//        );

        return user;
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token, user.getRole());
    }
}