package jakarta.data.metamodel.impl;

import jakarta.data.Sort;
import jakarta.data.metamodel.TextAttribute;

/**
 * Record type implementing {@link jakarta.data.metamodel.TextAttribute}.
 * This may be used to simplify implementation of the static metamodel.
 *
 * @param name the name of the attribute
 */
public record TextAttributeRecord<T>(String name)
        implements TextAttribute<T> {
    @Override
    public Sort<T> asc() {
        return Sort.asc(name);
    }

    @Override
    public Sort<T> desc() {
        return Sort.desc(name);
    }

    @Override
    public Sort<T> ascIgnoreCase() {
        return Sort.ascIgnoreCase(name);
    }

    @Override
    public Sort<T> descIgnoreCase() {
        return Sort.descIgnoreCase(name);
    }
}
