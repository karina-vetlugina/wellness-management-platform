package ca.gbc.comp3095.eventservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "events",
        indexes = {
                @Index(name = "idx_events_location", columnList = "location"),
                @Index(name = "idx_events_event_date", columnList = "event_date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String description;

    @NotNull
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String location;

    @Min(1)
    @Column(nullable = false)
    private int capacity;

    // store student ids registered for the event
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_registration", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "student_id", nullable = false, length = 100)
    @Builder.Default
    private Set<String> registeredStudents = new LinkedHashSet<>();
}