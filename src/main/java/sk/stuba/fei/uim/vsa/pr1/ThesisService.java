package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.AbstractThesisService;
import sk.stuba.fei.uim.vsa.pr1.bonus.MyPage;
import sk.stuba.fei.uim.vsa.pr1.bonus.Page;
import sk.stuba.fei.uim.vsa.pr1.bonus.Pageable;
import sk.stuba.fei.uim.vsa.pr1.bonus.PageableThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Assignment;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr1.entities.Teacher;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ThesisService extends AbstractThesisService<Student, Teacher, Assignment> implements PageableThesisService<Student, Teacher, Assignment> {
    @Override
    public Student createStudent(Long aisId, String name, String email) {
        EntityManager em = this.emf.createEntityManager();
        if(!isAisIdFree(aisId))
            return null;

        em.getTransaction().begin();
        try{
            Student student = new Student(aisId, name, email);
            em.persist(student);

            em.getTransaction().commit();
            return student;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Student getStudent(Long id) {
        EntityManager em = this.emf.createEntityManager();
        Student student = em.find(Student.class, id);
        return student;
    }

    @Override
    public Student updateStudent(Student student) {
        if(student == null)
            throw new IllegalArgumentException("Student is null");
        if(student.getId() == null){
            throw new IllegalArgumentException("Student id is null");
        }

        EntityManager em = this.emf.createEntityManager();
        List<Student> byEmail = em.createNamedQuery("Student.findByEmail", Student.class).setParameter("email", student.getEmail()).getResultList();
        System.out.println(byEmail.size());

        em.getTransaction().begin();
        try{
            Student studentToUpdate = em.find(Student.class, student.getId());
            if(studentToUpdate == null)
                return null;
            Student updatedStudent = em.merge(student);
            em.getTransaction().commit();
            return updatedStudent;

        }catch (Exception e){
//            em.getTransaction().rollback();
            return null;
        }

    }

    @Override
    public List<Student> getStudents() {
        EntityManager em = this.emf.createEntityManager();
        try{
            List<Student> students = em.createNamedQuery("Student.findAll", Student.class).getResultList();
            return students;
        } catch (Exception e){
            return new ArrayList<>();
        }

    }

    @Override
    public Student deleteStudent(Long id) {
        if(id == null)
            throw new IllegalArgumentException("Student id is null");
        //how to remove entity using JPA, do it with try catch
        EntityManager em = this.emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Student student = em.find(Student.class, id);
            em.remove(student);
            em.getTransaction().commit();

            return student;
        }catch (Exception e){
            return null;
        }

    }

    @Override
    public Teacher createTeacher(Long aisId, String name, String email, String department) {
        if(aisId == null)
            throw new IllegalArgumentException("AisId is null");
        EntityManager em = this.emf.createEntityManager();
        if(!isAisIdFree(aisId))
            return null;
        em.getTransaction().begin();
        try {


            Teacher teacher = new Teacher(aisId, name, email, department);
            em.persist(teacher);

            em.getTransaction().commit();

            return teacher;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Teacher getTeacher(Long id) {
        EntityManager em = this.emf.createEntityManager();
        Teacher teacher = em.find(Teacher.class, id);
        return teacher;
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        if(teacher == null)
            throw new IllegalArgumentException("Teacher is null");
        if(teacher.getId() == null){
            throw new IllegalArgumentException("Teacher id is null");
        }
        try {
            EntityManager em = this.emf.createEntityManager();
            em.getTransaction().begin();
            Teacher teacherToUpdate = em.find(Teacher.class, teacher.getId());
            if(teacherToUpdate == null)
                return null;
            Teacher updatedTeacher =  em.merge(teacher);
            return updatedTeacher;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public List<Teacher> getTeachers() {
        EntityManager em = this.emf.createEntityManager();
        try {
            List<Teacher> teachers = em.createNamedQuery("Teacher.findAll", Teacher.class).getResultList();
            return teachers;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    @Override
    public Teacher deleteTeacher(Long id) {
        if(id == null)
            throw new IllegalArgumentException("Teacher id is null");
        EntityManager em = this.emf.createEntityManager();
        em.getTransaction().begin();
        Teacher teacher = em.find(Teacher.class, id);

        em.remove(teacher);
        em.getTransaction().commit();

        return teacher;
    }

    @Override
    public Assignment makeThesisAssignment(Long supervisor, String title, String type, String description) {
        EntityManager em = this.emf.createEntityManager();
        if(supervisor == null)
            throw new IllegalArgumentException("Supervisor id is null");
        em.getTransaction().begin();
        Teacher t = em.find(Teacher.class, supervisor);
        if(t == null)
            throw new IllegalArgumentException("Teacher with id " + supervisor + " does not exist");
        try{
            Assignment assignment = new Assignment(title, type, description, t);
            em.persist(assignment);
            em.getTransaction().commit();
            return assignment;

        }catch (Exception e){
            return null;
        }


    }

    @Override
    public Assignment assignThesis(Long thesisId, Long studentId) {
        EntityManager em = this.emf.createEntityManager();
        em.getTransaction().begin();
        if(thesisId == null || studentId == null)
            throw new IllegalArgumentException("Thesis id is null");
        Assignment thesis = em.find(Assignment.class, thesisId);
        Student student = em.find(Student.class, studentId);
        if(thesis == null)
            throw new IllegalArgumentException("Thesis with id " + thesisId + " does not exist");
        if(student == null)
            throw new IllegalArgumentException("Student with id " + studentId + " does not exist");
        if(!thesis.isAssignable())
            throw new IllegalStateException("Thesis is not assignable, either it is already assigned or it is after deadline");
        try{
            thesis.assign(student);
            student.setAssignment(thesis);
            em.getTransaction().commit();
            return thesis;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Assignment submitThesis(Long thesisId) {
        EntityManager em = this.emf.createEntityManager();
        if(thesisId == null)
            throw new IllegalArgumentException("Thesis id is null");
        em.getTransaction().begin();
        Assignment thesis = em.find(Assignment.class, thesisId);
        if (thesis == null)
            throw new IllegalArgumentException("Thesis with id " + thesisId + " does not exist");
        if (!thesis.isSubmittable())
            throw new IllegalStateException("Thesis is not submittable, either it is not assigned or it is after deadline");
        try {
            thesis.submit();
            em.getTransaction().commit();
            return thesis;
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public Assignment deleteThesis(Long id) {
        if(id == null)
            throw new IllegalArgumentException("Thesis id is null");
        EntityManager em = this.emf.createEntityManager();
        em.getTransaction().begin();
        try {
            Assignment thesis = em.find(Assignment.class, id);
            em.remove(thesis);
            em.getTransaction().commit();
            return thesis;
        }catch (Exception e) {
            return null;
        }

    }

    @Override
    public List<Assignment> getTheses() {
        EntityManager em = this.emf.createEntityManager();
        List<Assignment> theses = em.createNamedQuery("Assignment.findAll", Assignment.class).getResultList();
        return theses;
    }

    @Override
    public List<Assignment> getThesesByTeacher(Long teacherId) {
        if(teacherId == null)
            throw new IllegalArgumentException("Teacher id is null");
        EntityManager em = this.emf.createEntityManager();
        try {
            List<Assignment> theses = em.createNamedQuery("Assignment.findByTeacherId", Assignment.class).setParameter("teacherId", teacherId).getResultList();
            return theses;
        }catch (Exception e){
//            return List.of(); toto mi chyba :(((
            return new ArrayList<>();
        }

    }

    @Override
    public Assignment getThesisByStudent(Long studentId) {
        if(studentId == null)
            throw new IllegalArgumentException("Student id is null");
        EntityManager em = this.emf.createEntityManager();
        try {
            Assignment thesis = em.createNamedQuery("Assignment.findByStudentId", Assignment.class).setParameter("studentId", studentId).getSingleResult();
            return thesis;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Assignment getThesis(Long id) {
        if(id == null)
            throw new IllegalArgumentException("Thesis id is null");
        EntityManager em = this.emf.createEntityManager();
        try {
            Assignment thesis = em.find(Assignment.class, id);
            return thesis;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Assignment updateThesis(Assignment thesis) {
        if(thesis == null)
            throw new IllegalArgumentException("Thesis is null");
        if(thesis.getId() == null){
            throw new IllegalArgumentException("Thesis id is null");
        }

        EntityManager em = this.emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Assignment thesisToUpdate = em.find(Assignment.class, thesis.getId());
            if(thesisToUpdate == null){
                return null;
            }
            Assignment updatedThesis = em.merge(thesis);
            em.getTransaction().commit();
            return updatedThesis;
        }catch (Exception e){
            return null;
        }

    }

    private boolean isAisIdFree(Long number){
        EntityManager em = this.emf.createEntityManager();
        try {
            Student student = em.createNamedQuery("Student.findById", Student.class).setParameter("id", number).getSingleResult();
            return false;
        }catch (Exception e){

        }
        try {
            Teacher teacher = em.createNamedQuery("Teacher.findById", Teacher.class).setParameter("id", number).getSingleResult();
            return false;
        }catch (Exception e){

        }
        return true;
    }

    @Override
    public Page<Student> findStudents(Optional<String> name, Optional<String> year, Pageable pageable) {
        List<Student> all = this.getStudents();

        if(name.isPresent()){
            all = all.stream().filter(s -> s.getName().equals(name.get())).collect(Collectors.toList());
        }

        if(year.isPresent()){
            Integer yr = Integer.parseInt(year.get());
            all = all.stream().filter(s->s.getYear().equals(yr)).collect(Collectors.toList());
        }
        if(all.size() == 0) return new MyPage<>(new ArrayList<>(), pageable, all.size());

        //PageAble(2, 10);

        int startIndex = pageable.getPageNumber() * pageable.getPageSize(); // 20 +10 //28
        int endIndex = Math.min(startIndex + pageable.getPageSize(), all.size());

        if(startIndex > endIndex) return new MyPage<>(new ArrayList<>(), pageable, all.size());

        List<Student> pageContent = all.subList(startIndex, endIndex);

        Page<Student> page = new MyPage<>(pageContent, pageable, all.size());


        return page;
    }

    @Override
    public Page<Teacher> findTeachers(Optional<String> name, Optional<String> institute, Pageable pageable) {
        List<Teacher> all = this.getTeachers();

        if(name.isPresent()){
            all = all.stream().filter(s -> s.getName().equals(name.get())).collect(Collectors.toList());
        }

        if(institute.isPresent()){
            all = all.stream().filter(s->s.getInstitute().equals(institute.get())).collect(Collectors.toList());
        }

        if(all.size() == 0) return new MyPage<>(new ArrayList<>(), pageable, all.size());

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), all.size());

        if(startIndex > endIndex) return new MyPage<>(new ArrayList<>(), pageable, all.size());

        List<Teacher> pageContent = all.subList(startIndex,endIndex);

        Page<Teacher> page = new MyPage<>(pageContent, pageable, all.size());


        return page;
    }

    @Override
    public Page<Assignment> findTheses(Optional<String> department, Optional<java.util.Date> publishedOn, Optional<String> type, Optional<String> status, Pageable pageable){
        List<Assignment> all = this.getTheses();


        if(department.isPresent()){
            all = all.stream().filter(s -> s.getWorkplace().equals(department.get())).collect(Collectors.toList());
        }

        if(publishedOn.isPresent()){
            LocalDate date = new Date(publishedOn.get().getTime()).toLocalDate();
            all = all.stream().filter(s->s.getDateOfPublication().equals(date)).collect(Collectors.toList());
        }

        if(type.isPresent()){
            all = all.stream().filter(a-> a.getType().name().equals(type.get())).collect(Collectors.toList());
        }

        if(status.isPresent()){
            all = all.stream().filter(a-> a.getStatus().name().equals(status.get())).collect(Collectors.toList());
        }

        if(all.size() == 0) return new MyPage<>(new ArrayList<>(), pageable, all.size());

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), all.size());

        if(startIndex > endIndex) return new MyPage<>(new ArrayList<>(), pageable, all.size());

        List<Assignment> pageContent = all.subList(startIndex,endIndex);

        Page<Assignment> page = new MyPage<>(pageContent, pageable, all.size());


        return page;
    }
}
