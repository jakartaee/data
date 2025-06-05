package jakarta.data.repository.stateful;

import java.lang.annotation.*;

/**
 * <p>Lifecycle annotation for repository methods of stateful repositories
 * which perform refresh operations. The {@code Refresh} annotation must
 * not be applied to a method of a stateless repository.
 * </p>
 * <p>The {@code Refresh} annotation indicates that the annotated repository
 * method immediately reloads the state of the given entity from the database,
 * overwriting the persistent state previously held by the entity.
 * </p>
 * <p>An {@code Refresh} method accepts a managed instance or instances of an
 * entity class. The method must have exactly one parameter whose type is
 * either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be inserted, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the
 *     entities to be inserted.</li>
 * </ul>
 * <p>The annotated method must be declared {@code void}.
 * </p>
 * <p>If an unmanaged entity is passed to a {@code Refresh} method, the method
 * must throw {@link IllegalArgumentException}.
 * </p>
 * <p>Every Jakarta Data provider which supports stateful repositories is
 * required to accept a {@code Refresh} method which conforms to this signature.
 * Application of the {@code Refresh} annotation to a method with any other
 * signature is not portable between Jakarta Data providers. Furthermore,
 * support for stateful repositories is optional. A Jakarta Data provider
 * is not required to support stateful repositories.
 * </p>
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Persist},
 * {@code @Merge}, {@code @Remove}, and {@code @Refresh} are mutually-exclusive.
 * A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 * </p>
 *
 * @since 1.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Refresh {
}
