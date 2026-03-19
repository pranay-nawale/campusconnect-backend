package campusconnect.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String paymentMethod;

    private String transactionId;

    private LocalDateTime paymentDate;

    @OneToOne
    private EventRequest eventRequest;
}