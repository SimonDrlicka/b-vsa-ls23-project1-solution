package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "teacher")
@NamedQuery(name = "Teacher.findAll", query = "SELECT t FROM Teacher t")
@NamedQuery(name = "Teacher.findById", query = "SELECT t FROM Teacher t WHERE t.id = :id")
public class Teacher {
    public Teacher(Long aisId, String name, String email, String workplace){
        this.id = aisId;
        this.name = name;
        this.email = email;
        this.workplace = workplace;
        this.institute = workplace;
        this.finalProjects = new ArrayList<>();
    }
    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column(name= "name")
    protected String name;

    @Column(name="email", unique = true)
    protected String email;
    private String institute;

    private String workplace;

    @OneToMany(mappedBy = "teacher", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Assignment> finalProjects;

    public void removeFinalProject(Assignment finalProject) {
        finalProjects.remove(finalProject);
        finalProject.setTeacher(null);
    }

    public void addFinalProject(Assignment finalProject) {

        finalProjects.add(finalProject);
        finalProject.setTeacher(this);
    }
}