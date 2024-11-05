package jakarta.data.repository;

import jakarta.data.Criteria;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;

import java.util.List;

/**
 * Repository interface that extends {@link DataRepository} to add support for dynamic filtering
 * using {@link Criteria} objects with optional pagination.
 *
 * <p>This repository allows flexible querying by accepting varargs of criteria conditions
 * for filtering and supports pagination to retrieve results in pages or as a full list.</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * &#64;Inject
 * DriverLicenses licenses;
 *
 * // Define and apply criteria using metamodel attributes
 * PageRequest pageRequest = PageRequest.of(0, 10);
 * Page<DriverLicense> pagedResults = licenses.findByCriteria(pageRequest,
 *     _DriverLicense.license.like("ABC%"),
 *     _DriverLicense.expiry.greaterThan(LocalDate.now())
 * );
 *
 * // Fetch results without pagination
 * List<DriverLicense> allResults = licenses.findByCriteria(
 *     _DriverLicense.license.like("ABC%"),
 *     _DriverLicense.expiry.greaterThan(LocalDate.now())
 * );
 * </pre>
 *
 * @param <T> the type of the primary entity class of the repository.
 * @param <K> the type of the unique identifier field or property of the primary entity.
 */
public interface CriteriaRepository<T, K> extends DataRepository<T, K> {


    /**
     * Finds entities by applying the specified criteria conditions, with support for pagination.
     *
     * @param pageRequest the pagination information including page number and size.
     * @param criteria    the varargs of criteria conditions to filter the results.
     * @return a page of entities that match the criteria.
     */
    Page<T> filter(PageRequest pageRequest, Criteria... criteria);

    /**
     * Finds all entities by applying the specified criteria conditions, returning results as a list without pagination.
     *
     * @param criteria the varargs of criteria conditions to filter the results.
     * @return a list of entities that match the criteria.
     */
    List<T> filter(Criteria... criteria);

    /**
     * Counts entities by applying the specified criteria conditions.
     *
     * @param criteria the varargs of criteria conditions to filter the results.
     * @return the count of entities that match the criteria.
     */
    long countBy(Criteria... criteria);
}
