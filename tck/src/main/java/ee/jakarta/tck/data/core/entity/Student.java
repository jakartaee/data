package ee.jakarta.tck.data.core.entity;

/**
 * Test to see if we can just put both entities on the same bean
 */
@jakarta.nosql.Entity
@jakarta.persistence.Entity
public class Student {
    
    @jakarta.nosql.Id
    @jakarta.persistence.Id
    private Long id;
    
    @jakarta.persistence.Transient
    private String name;

    @jakarta.nosql.Column
    private int age;
    
    public Student() {
        //DO NOTHING
    }
    
    public Student(Long id, String name, int age) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
