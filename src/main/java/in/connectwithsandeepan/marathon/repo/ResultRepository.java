package in.connectwithsandeepan.marathon.repo;

import in.connectwithsandeepan.marathon.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
