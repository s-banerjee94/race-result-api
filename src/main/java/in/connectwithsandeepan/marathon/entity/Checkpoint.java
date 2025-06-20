package in.connectwithsandeepan.marathon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Checkpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int checkpointNumber;

    @Column(nullable = false)
    private LocalTime time;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private Result result;
}
