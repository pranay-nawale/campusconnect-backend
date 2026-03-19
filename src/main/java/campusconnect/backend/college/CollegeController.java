package campusconnect.backend.college;

import campusconnect.backend.dto.EventPaymentDTO;
import campusconnect.backend.entity.College;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    ) throws MessagingException {

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
            @ModelAttribute EventRequestDTO request,
            @RequestParam(required = false) MultipartFile banner,
            Authentication authentication
    ){

        String email = authentication.getName();

        return collegeService.createEventRequest(request, banner, email);
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

    @PostMapping("/events/{id}/confirm")
    public String confirmEvent(@PathVariable Long id,
                               @RequestBody EventPaymentDTO payment,
                               Authentication authentication) throws Exception {

        return collegeService.confirmEventPlan(
                id,
                payment,
                authentication.getName()
        );
    }

    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Long id,
                              Authentication authentication){

        return collegeService.rejectEventPlan(
                id,
                authentication.getName()
        );
    }

    @PutMapping("/events/{id}/reschedule")
    public String rescheduleEvent(@PathVariable Long id,
                                  @RequestParam LocalDateTime newDate,
                                  Authentication authentication){

        return collegeService.requestReschedule(
                id,
                newDate,
                authentication.getName()
        );
    }

    @PostMapping("/upload-official-letter")
    public College uploadOfficialLetter(
            @RequestParam MultipartFile file,
            Authentication authentication
    ) {

        return collegeService.uploadOfficialLetter(authentication.getName(), file);
    }

    @PostMapping("/upload-naac")
    public College uploadNaac(
            @RequestParam MultipartFile file,
            Authentication authentication
    ) {

        return collegeService.uploadNaacCertificate(authentication.getName(), file);
    }
    @PostMapping("/upload-logo")
    public College uploadLogo(
            @RequestParam MultipartFile file,
            Authentication authentication
    ) {

        return collegeService.uploadLogo(authentication.getName(), file);
    }
}