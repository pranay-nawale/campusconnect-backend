package campusconnect.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventRequest event;

    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean paymentDone = false; // true if student paid

    @Column(nullable = false, columnDefinition = "double precision default 0")
    private double paidAmount = 0;// 0 for free events

    @Column(name = "qr_code")
    private String qrCode;
}