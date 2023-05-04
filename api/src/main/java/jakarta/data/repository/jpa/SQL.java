package jakarta.data.repository.jpa;

import jakarta.data.repository.Page;
import jakarta.data.repository.Sort;

/**
 * Defines the SQL string as SQL, JPA-QL that should be executed.
 */
public @interface SQL {

    /**
     * <p>Defines the query to be executed when the annotated method is called.</p>
     *
     * <p>If an application defines a repository method with <code>&#64;Query</code>
     * and supplies other forms of sorting (such as {@link Sort}) to that method,
     * then it is the responsibility of the application to compose the query in
     * such a way that an <code>ORDER BY</code> clause (or query language equivalent)
     * can be validly appended. The Jakarta Data provider is not expected to
     * parse query language that is provided by the application.</p>
     *
     * @return the query to be executed when the annotated method is called.
     */
    String value();

    /**
     * <p>Defines an additional query that counts the number of elements that are
     * returned by the {@link #value() primary} query. This is used to compute
     * the {@link Page#totalElements() total elements}
     * and {@link Page#totalPages() total pages}
     * for paginated repository queries that are annotated with
     * <code>@Query</code> and return a {@link Page} or {@link jakarta.data.repository.jpa.KeysetAwarePage}.
     * Slices do not use a counting query.</p>
     *
     * <p>The default value of empty string indicates that no counting query
     * is provided. A counting query is unnecessary when pagination is
     * performed with slices instead of pages and when pagination is
     * not used at all.</p>
     *
     * @return a query for counting the number of elements across all pages.
     *         Empty string indicates that no counting query is provided.
     */
    String count() default "";
}
