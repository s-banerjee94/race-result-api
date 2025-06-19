package in.connectwithsandeepan.marathon.repo;

import in.connectwithsandeepan.marathon.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    List<EventCategory> findByEventIdOrderByFlagOffTimeAsc(Long eventId);

    @Query("SELECT COUNT(r) > 0 FROM Result r WHERE r.category.id = :categoryId")
    boolean hasResults(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(r) FROM Result r WHERE r.category.id = :categoryId")
    int getResultCount(@Param("categoryId") Long categoryId);

    boolean existsByEventIdAndCategoryName(Long eventId, String raceCategory);

    Optional<EventCategory> findByEventIdAndCategoryName(Long eventId, String raceCategory);
}
