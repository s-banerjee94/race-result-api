package in.connectwithsandeepan.marathon.repo;

import in.connectwithsandeepan.marathon.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
