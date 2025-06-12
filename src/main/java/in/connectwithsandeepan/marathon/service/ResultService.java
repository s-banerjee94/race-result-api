package in.connectwithsandeepan.marathon.service;

import in.connectwithsandeepan.marathon.dto.ResultRequestDTO;
import in.connectwithsandeepan.marathon.entity.Result;

import java.util.List;

public interface ResultService {
    /**
     * Saves a result for a specific marathon event.
     *
     * @param eventId the ID of the marathon event
     * @param result  the result to be saved
     * @return the saved result
     * @See Result
     */
    Result saveResult(Long eventId, ResultRequestDTO result);

    /**
     * Retrieves a result by its ID.
     *
     * @param id the ID of the result
     * @return the result with the specified ID
     */
    Result getResultById(Long id);

    /**
     * Retrieves all results for a specific marathon event.
     *
     * @param eventId the ID of the marathon event
     * @return a list of results for the specified event
     */
    List<Result> getAllResultsByEventId(Long eventId);

    /**
     * Updates an existing result.
     *
     * @param id     the ID of the result to be updated
     * @param result the updated result data
     * @return the updated result
     */
    Result updateResult(Long id, Result result);

    /**
     * Deletes a result by its ID.
     *
     * @param id the ID of the result to be deleted
     */
    void deleteResult(Long id);
}
