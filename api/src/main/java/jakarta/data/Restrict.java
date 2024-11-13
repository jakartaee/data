package jakarta.data;

import java.util.List;
import java.util.Set;

public class Restrict {


    @SafeVarargs
    public static <T> Restriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestrictionType.ALL, List.of(restrictions));
    }

    @SafeVarargs
    public static <T> Restriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestrictionType.ANY, List.of(restrictions));
    }

    public static <T> Restriction<T> equalTo(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.EQUAL, value);
    }


    public static <T> Restriction<T> lessThan(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.LESS_THAN, value);
    }

    public static <T> Restriction<T> greaterThanEqual(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.GREATER_THAN_EQUAL, value);
    }

    public static <T> Restriction<T> lessThanEqual(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.LESS_THAN_EQUAL, value);
    }

    public static <T> Restriction<T> greaterThan(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.GREATER_THAN, value);
    }

    public static <T> Restriction<T> in(Set<Object> values, String field) {
        return new RestrictionRecord<>(field, Operator.IN, values);
    }

}
