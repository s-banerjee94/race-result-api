package in.connectwithsandeepan.marathon.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String eventName;
    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(length = 2000)
    private String eventDescription;

    private String organizerName;
    private String organizerWebsite;

    private String city;
    private String state;
    private String country;

    private String imageUrl;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventCategory> eventCategories;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results = new ArrayList<>();

    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }

    public boolean hasCategories() {
        return eventCategories != null && !eventCategories.isEmpty();
    }

    @JsonIgnore
    public int getCategoryCount() {
        return eventCategories != null ? eventCategories.size() : 0;
    }

    @JsonIgnore
    public int getResultCount() {
        return results != null ? results.size() : 0;
    }
}
