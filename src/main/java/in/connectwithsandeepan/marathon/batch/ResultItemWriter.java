package in.connectwithsandeepan.marathon.batch;

import in.connectwithsandeepan.marathon.entity.Result;
import in.connectwithsandeepan.marathon.repo.ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class ResultItemWriter implements ItemWriter<Result> {
    private final ResultRepository resultRepository;

    @Override
    public void write(Chunk<? extends Result> chunk) throws Exception {
        List<? extends Result> results = chunk.getItems();
        log.info("Writing {} results to database", results.size());

        try {
            resultRepository.saveAll(results);
            log.info("Successfully saved {} results", results.size());
        } catch (Exception e) {
            log.error("Error saving results: {}", e.getMessage());
            throw e;
        }
    }
}
