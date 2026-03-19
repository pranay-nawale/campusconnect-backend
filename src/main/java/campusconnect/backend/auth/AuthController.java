package campusconnect.backend.auth;

//import campusconnect.backend.admin.AdminService;
import campusconnect.backend.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/test/debug")
    public String debug(Authentication auth){
        return auth.getAuthorities().toString();
    }
}