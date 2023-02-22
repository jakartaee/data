package ee.jakarta.tck.data.common.entity.persistance;

@jakarta.persistence.Entity
public class Student {
    
    @jakarta.persistence.Id
    private Long id;
    
    @jakarta.persistence.Transient
    private String name;

    private int age;
    
    public Student() {
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
