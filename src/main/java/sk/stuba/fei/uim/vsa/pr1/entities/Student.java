package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s")
@NamedQuery(name = "Student.findByEmail", query = "SELECT s FROM Student s WHERE s.email = :email")
@NamedQuery(name = "Student.findById", query = "SELECT s FROM Student s WHERE s.id = :id")
public class Student{

    public Student(Long aisId, String name, String email){
        this.id = aisId;
        this.name = name;
        this.email = email;
    }
    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column(name= "name")
    protected String name;

    @Column(name="email", unique = true)
    protected String email;

    @Column(name = "year")
    private Integer year;

    @Column(name="semester")
    private Integer semester;

    @Column(name="program")
    private String program;


    @OneToOne(mappedBy = "student")
    private Assignment assignment;

    public Student(Student s1) {
        this.id = s1.id;
        this.name = s1.name;
        this.email = s1.email;
        this.year = s1.year;
        this.semester = s1.semester;
        this.program = s1.program;
        this.assignment = s1.assignment;
    }

    @Override
    public String toString() {
        return id.toString() + " " + name;
    }
}