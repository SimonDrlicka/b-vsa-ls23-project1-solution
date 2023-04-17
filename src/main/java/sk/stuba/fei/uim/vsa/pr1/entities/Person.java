package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Person implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column(name= "name")
    protected String name;

    @Column(name="email", unique = true)
    protected String email;

}
