package jakarta.data.page;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Slice<T> extends Iterable<T> {

    /**
     * Returns whether the {@link Page} has content at all.
     *
     * @return whether the {@link Page} has content at all.
     */
    boolean hasContent();

    /**
     * Returns the page content as a {@link List}.
     * The list is sorted according to the combined sort criteria of the repository method
     * and the sort criteria of the page request that is supplied to the repository method,
     * sorting first by the sort criteria of the repository method,
     * and then by the sort criteria of the page request.
     *
     * @return the page content as a {@link List}; will never be {@code null}.
     */
    List<T> content();

    /**
     * Returns a sequential stream of results, which follow the order of the sort
     * criteria, if any were specified.
     *
     * @return a stream of results.
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns {@code true} if it is known that there are more results or that it is
     * necessary to request a next page to determine whether there are more results,
     * so that {@link #nextPageRequest()} will definitely not return {@code null}.
     * @return {@code false} if this is the last page of results.
     */
    boolean hasNext();

    /**
     * Returns {@code true} if it is known that there are previous results or that it
     * is necessary to request the previous page to determine whether there are previous
     * results, so that {@link #previousPageRequest()} will not return {@code null}.
     * @return {@code false} if this is the first page of results.
     */
    boolean hasPrevious();

    /**
     * Returns the {@linkplain PageRequest page request} for which this
     * page was obtained.
     *
     * @return the request for the current page; will never be {@code null}.
     */
    PageRequest pageRequest();


    /**
     * Returns a request for the next page if {@link #hasNext()} indicates there
     * might be a next page.
     *
     * @return a request for the next page.
     * @throws NoSuchElementException if it is known that there is no next page.
     *         To avoid this exception, check for a {@code true} result of
     *         {@link #hasNext()} before invoking this method.
     */
    PageRequest nextPageRequest();

    /**
     * <p>Returns a request for the previous page, if {@link #hasPrevious()}
     * indicates there might be a previous page.</p>
     *
     * @return a request for the previous page.
     * @throws NoSuchElementException if it is known that there is no previous page.
     *         To avoid this exception, check for a {@code true} result of
     *         {@link #hasPrevious()} before invoking this method.
     */
    PageRequest previousPageRequest();

}
