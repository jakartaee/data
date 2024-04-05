package ee.jakarta.tck.data.common.cdi;

import java.util.UUID;

/**
 * A test entity that will be persisted to a repository.
 * Uses the custom {@code @TCKEntity} annotation.
 * 
 * @see ee.jakarta.tck.data.common.cdi.TCKEntity
 */
@TCKEntity
public record AddressRecord(UUID id, int house, String street, String city, String state, long zipCode) {
}
