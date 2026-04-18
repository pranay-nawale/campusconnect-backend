package campusconnect.backend.payment;

import campusconnect.backend.entity.EventRequest;
import campusconnect.backend.entity.EventStatus;
import campusconnect.backend.repository.EventRequestRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository paymentRepo;
    private final EventRequestRepository eventRepo;

    private static final String KEY = "rzp_test_SaeQXT2cyBC0e9";
    private static final String SECRET = "Tsj1yz9Q12Usg0r9rkgq30uP";

    public Map<String, Object> createOrder(Long eventId) throws Exception {

        EventRequest event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        RazorpayClient client = new RazorpayClient(KEY, SECRET);

        int amount = (int) (event.getPrice() * 100);

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        JSONObject options = new JSONObject();
        options.put("amount", amount);
        options.put("currency", "INR");

// ✅ REQUIRED
        options.put("receipt", "order_" + eventId);

// ✅ IMPORTANT (sometimes required)
        options.put("payment_capture", 1);

        Order order = client.orders.create(options);

        PaymentTransaction tx = PaymentTransaction.builder()
                .orderId(order.get("id"))
                .amount(event.getPrice())
                .currency("INR")
                .status(PaymentStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .eventRequest(event)
                .build();

        paymentRepo.save(tx);

        return Map.of(
                "orderId", order.get("id"),
                "amount", amount,
                "key", KEY
        );
    }

    public String verifyPayment(Map<String, String> payload) throws Exception {

        String orderId = payload.get("razorpay_order_id");
        String paymentId = payload.get("razorpay_payment_id");
        String signature = payload.get("razorpay_signature");

        PaymentTransaction tx = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // 🔐 create your own signature
        String data = orderId + "|" + paymentId;
        String generatedSignature = generateSignature(data, SECRET);

        // ❌ if mismatch → reject
        if (!generatedSignature.equals(signature)) {
            tx.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(tx);
            throw new RuntimeException("Payment verification failed");
        }

        // ✅ success
        tx.setPaymentId(paymentId);
        tx.setSignature(signature);
        tx.setStatus(PaymentStatus.SUCCESS);
        paymentRepo.save(tx);

        EventRequest event = tx.getEventRequest();
        event.setEventStatus(EventStatus.CONFIRMED);
        eventRepo.save(event);

        return "Payment verified";
    }

    private String generateSignature(String data, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKey);

        byte[] hash = mac.doFinal(data.getBytes());

        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }
}