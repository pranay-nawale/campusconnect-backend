package campusconnect.backend.auth;

import campusconnect.backend.dto.*;
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

    public void register(RegisterRequest request) {

        if(request.getRole() == Role.ADMIN){
            throw new RuntimeException("Admin cannot register");
        }

        if (request.getRole() == null) {
            throw new RuntimeException("Role is required");
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());

        notificationFacade.notifyUser(
                user,
                "Welcome to CampusConnect 🎉",
                NotificationType.USER_REGISTERED,
                vars,
                false
        );    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}