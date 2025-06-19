package in.connectwithsandeepan.marathon.repo;

import in.connectwithsandeepan.marathon.entity.Result;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
     List<Result> findByEventId(Long eventId);


     @Query("SELECT r FROM Result r " +
             "LEFT JOIN FETCH r.category " +
             "LEFT JOIN FETCH r.checkpointTimes " +
             "WHERE r.event.id = :eventId")
     List<Result> findAllByEventIdWithCategoryAndCheckpoints(@Param("eventId") Long eventId);

    boolean existsByEventIdAndBibNumber(Long eventId, String bibNumber);

    boolean existsByEvent_IdAndBibNumber(Long eventId, String bibNumber);

    long countByEventId(Long eventId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Result r WHERE r.event.id = :eventId")
    void deleteByEventId(@Param("eventId") Long eventId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Checkpoint c WHERE c.result.event.id = :eventId")
    void deleteCheckpointsByEventId(@Param("eventId") Long eventId);
}
