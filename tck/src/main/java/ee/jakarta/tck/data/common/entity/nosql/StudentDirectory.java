package ee.jakarta.tck.data.common.entity.nosql;

import ee.jakarta.tck.data.common.entity.persistance.Student;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;

@Repository
public interface StudentDirectory extends DataRepository<Student, Long>  {
    void save(Student student);
    void deleteById(Long id);
    
    int countByAgeGreaterThanEqual(Integer age);
    int countByNameIgnoreCase(String name);
}
