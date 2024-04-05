package ee.jakarta.tck.data.common.cdi;

import static jakarta.data.repository.By.ID;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;

/**
 * An AddressBook repository for testing.
 * 
 * Uses the AddressRecord with the {@code @EntityDefining} annotation {@code TCKEntity} 
 * to ensure the mock Jakarta Data provider from this TCK implements this repository interface.
 * 
 * @see ee.jakarta.tck.data.common.cdi.DirectoryRepository
 */
@Repository
public interface AddressBook extends DataRepository<AddressRecord, UUID>  {
    
    public static final String ADDRESS_PROVIDER = "ADDRESS_PROVIDER";

    @Find
    List<AddressRecord> findById(List<UUID> ids);
    
    @Delete
    void deleteById(@By(ID) UUID id);
    
    @PutTCKLifecyleMethod
    AddressRecord putAddress(AddressRecord address);
}
