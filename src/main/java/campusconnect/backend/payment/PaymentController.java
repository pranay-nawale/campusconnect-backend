package campusconnect.backend.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


    @RestController
    @RequestMapping("/payments")
    @RequiredArgsConstructor
    public class PaymentController {

        private final PaymentService paymentService;

        @PostMapping("/create-order/{eventId}")
        public ResponseEntity<?> createOrder(@PathVariable Long eventId) throws Exception {
            return ResponseEntity.ok(paymentService.createOrder(eventId));
        }

        @PostMapping("/verify")
        public ResponseEntity<?> verify(@RequestBody Map<String, String> payload) throws Exception {
            return ResponseEntity.ok(paymentService.verifyPayment(payload));
        }
    }


