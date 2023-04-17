package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sk.stuba.fei.uim.vsa.pr1.enums.AssignmentStatus;
import sk.stuba.fei.uim.vsa.pr1.enums.AssignmentType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "assignment")
@Getter
@Setter
@AllArgsConstructor
@NamedQuery(name = "Assignment.findAll", query = "SELECT a FROM Assignment a")
@NamedQuery(name = "Assignment.findByTeacherId", query = "SELECT a FROM Assignment a WHERE a.teacher.id = :teacherId")
@NamedQuery(name = "Assignment.findByStudentId", query = "SELECT a FROM Assignment a WHERE a.student.id = :studentId")
@NamedQuery(name = "Assignment.findById", query = "SELECT a FROM Assignment a WHERE a.id = :id")
public class Assignment {
    static int counter = 1;
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number" , unique = true)
    private String registrationNumber;


    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "workplace")
    private String workplace;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "date_of_publication")
    private LocalDate dateOfPublication;

    @Column(name = "deadline")
    private LocalDate deadline;

    //I want enum type
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AssignmentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssignmentStatus status;

    public Assignment(){
        this.registrationNumber = "FEI-" + ++counter;
    }

    public boolean isAssignable(){
        LocalDate now = LocalDate.now();
        boolean isBeforeDeadline = now.isBefore(LocalDate.ofEpochDay(this.deadline.toEpochDay()));
        return this.status == AssignmentStatus.OPEN && isBeforeDeadline;
    }

    public boolean isSubmittable(){
        LocalDate now = LocalDate.now();
        boolean isBeforeDeadline = now.isBefore(LocalDate.ofEpochDay(this.deadline.toEpochDay()));
        return this.status == AssignmentStatus.IN_PROGRESS && isBeforeDeadline;
    }

    public Assignment(String title, String type, String description, Teacher t){
        this();
        this.title = title;
        this.type = AssignmentType.valueOf(type);
        this.description = description;
        this.teacher = t;
        this.workplace = t.getWorkplace();
        this.status = AssignmentStatus.OPEN;
        this.dateOfPublication =  LocalDate.now();
        this.deadline = LocalDate.now().plusMonths(3);
        this.status = AssignmentStatus.OPEN;
        t.addFinalProject(this);
    }

    /**
     * Copy constructor, used only for testing purposes
     * @param a: Assignment
     */
    public Assignment(Assignment a){
        this.id = a.id;
        this.registrationNumber = a.registrationNumber;
        this.title = a.title;
        this.type = a.type;
        this.description = a.description;
        this.teacher = a.teacher;
        this.workplace = a.workplace;
        this.status = a.status;
        this.dateOfPublication =  a.dateOfPublication;
        this.deadline = a.deadline;
        this.student = a.student;
    }

    /**
     * Removes student and sets status to OPEN
     */
    public void open(){
        this.status = AssignmentStatus.OPEN;
        this.student = null;
    }

    public boolean assign(Student s){
        if(this.isAssignable()){
            this.student = s;
            this.status = AssignmentStatus.IN_PROGRESS;
            return true;
        }
        return false;
    }
    public void submit() {
        this.status = AssignmentStatus.COMPLETED;
    }


    public void removeTeacher(){
        this.teacher = null;
    }

    public void addTeacher(Teacher t){
        this.teacher = t;
    }



}
