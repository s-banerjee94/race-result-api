package in.connectwithsandeepan.marathon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_event_bib_number", columnNames = {"event_id", "bib_number"})
})
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String bibNumber;

    @Column(nullable = false)
    private String participantName;

    private String gender;
    private String ageCategory;

    private String overAllRank;
    private String genderRank;
    private String ageCategoryRank;

    @Transient
    @JsonIgnore
    private String raceCategory;

    @Column(nullable = false)
    private LocalTime chipTime;

    @Column(nullable = false)
    private LocalTime gunTime;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Checkpoint> checkpointTimes = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne()
    @JoinColumn(name = "event_category_id", nullable = false)
    private EventCategory category;
}
