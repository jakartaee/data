package ee.jakarta.tck.data.common.entity.nosql;

@jakarta.nosql.Entity
public class Student {
    @jakarta.nosql.Id
    private Long id;
    
    private String name;

    @jakarta.nosql.Column
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
