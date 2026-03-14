package campusconnect.backend.auth;

//import campusconnect.backend.admin.AdminService;
import campusconnect.backend.dto.*;
import campusconnect.backend.entity.College;
import campusconnect.backend.entity.Event;
import campusconnect.backend.entity.EventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        authService.register(request);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        return authService.login(request);
    }


}