package jakarta.data.repository;

import jakarta.data.Operator;
import jakarta.data.Restriction;

import java.util.function.Supplier;
/**
 * Represents a pattern for SQL `LIKE` operations, encapsulating different
 * `LIKE` matching options, such as prefix, suffix, and substring matching,
 * and supplying a `Restriction` for easy use in repository queries.
 *
 * <p>This class supports type-safe attributes and various matching types,
 * including case-insensitive patterns.</p>
 *
 * @param <T> the type of the entity on which the restriction will be applied.
 */
public record Pattern<T>(String field, String value, boolean ignoreCase) implements Supplier<Restriction<T>> {

    /**
     * Supplies a `Restriction` configured with this `LIKE` pattern.
     *
     * @return a `Restriction` representing the `LIKE` pattern.
     */
    @Override
    public Restriction<T> get() {
        return new Restriction<>(field, Operator.LIKE, value);
    }

    /**
     * Creates a case-sensitive `LIKE` pattern for an exact match.
     *
     * @param field the field to apply the pattern on.
     * @param value the value to match exactly.
     * @return a Pattern for an exact match.
     */
    public static <T> Pattern<T> like(String field, String value) {
        return new Pattern<>(field, value, false);
    }

    /**
     * Creates a case-insensitive `LIKE` pattern for an exact match.
     *
     * @param field the field to apply the pattern on.
     * @param value the value to match exactly.
     * @return a Pattern for a case-insensitive exact match.
     */
    public static <T> Pattern<T> likeIgnoreCase(String field, String value) {
        return new Pattern<>(field, value, true);
    }

    /**
     * Creates a `LIKE` pattern for values prefixed with the specified value.
     *
     * @param field the field to apply the pattern on.
     * @param value the prefix to match.
     * @return a Pattern for prefix matching.
     */
    public static <T> Pattern<T> prefixed(String field, String value) {
        return new Pattern<>(field, value + "%", false);
    }

    /**
     * Creates a case-insensitive `LIKE` pattern for values prefixed with the specified value.
     *
     * @param field the field to apply the pattern on.
     * @param value the prefix to match.
     * @return a Pattern for case-insensitive prefix matching.
     */
    public static <T> Pattern<T> prefixedIgnoreCase(String field, String value) {
        return new Pattern<>(field, value + "%", true);
    }

    /**
     * Creates a `LIKE` pattern for values containing the specified substring.
     *
     * @param field the field to apply the pattern on.
     * @param value the substring to match.
     * @return a Pattern for substring matching.
     */
    public static <T> Pattern<T> substringed(String field, String value) {
        return new Pattern<>(field, "%" + value + "%", false);
    }

    /**
     * Creates a case-insensitive `LIKE` pattern for values containing the specified substring.
     *
     * @param field the field to apply the pattern on.
     * @param value the substring to match.
     * @return a Pattern for case-insensitive substring matching.
     */
    public static <T> Pattern<T> substringedIgnoreCase(String field, String value) {
        return new Pattern<>(field, "%" + value + "%", true);
    }

    /**
     * Creates a `LIKE` pattern for values suffixed with the specified value.
     *
     * @param field the field to apply the pattern on.
     * @param value the suffix to match.
     * @return a Pattern for suffix matching.
     */
    public static <T> Pattern<T> suffixed(String field, String value) {
        return new Pattern<>(field, "%" + value, false);
    }

    /**
     * Creates a case-insensitive `LIKE` pattern for values suffixed with the specified value.
     *
     * @param field the field to apply the pattern on.
     * @param value the suffix to match.
     * @return a Pattern for case-insensitive suffix matching.
     */
    public static <T> Pattern<T> suffixedIgnoreCase(String field, String value) {
        return new Pattern<>(field, "%" + value, true);
    }
}
