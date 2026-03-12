package campusconnect.backend.college;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;

    @PostMapping("/register")
    public String registerCollege(
            @RequestBody CollegeRegistrationRequestDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return collegeService.registerCollege(request, email);
    }

    @GetMapping("/profile")
    public CollegeResponseDTO getCollegeProfile(Authentication authentication){

        String email = authentication.getName();

        return collegeService.getCollegeByUser(email);
    }

    @PatchMapping("/update")
    public CollegeResponseDTO updateCollegeProfile(
            @RequestBody CollegeUpdateDTO request,
            Authentication authentication
    ){

        String email = authentication.getName();

        return collegeService.updateCollegeProfile(request, email);
    }

    @PostMapping("/event-request")
    public EventRequestResponseDTO createEventRequest(
            @RequestBody EventRequestDTO request,
            Authentication authentication){

        String email = authentication.getName();

        return collegeService.createEventRequest(request, email);
    }

    @GetMapping("/event-requests")
    public List<EventRequestResponseDTO> getEventRequests(Authentication authentication){

        String email = authentication.getName();

        return collegeService.getCollegeEventRequests(email);
    }

    @DeleteMapping("/event-requests/{id}")
    public String deleteEventRequest(
            @PathVariable Long id,
            Authentication authentication
    ){

        String email = authentication.getName();

        return collegeService.deleteEventRequest(id, email);
    }

    @PutMapping("/event-requests/{id}")
    public EventRequestResponseDTO updateEventRequest(
            @PathVariable Long id,
            @RequestBody EventRequestDTO request,
            Authentication authentication
    ){

        String email = authentication.getName();

        return collegeService.updateEventRequest(id, request, email);
    }
}