package models;

import java.util.Objects;

public class Mentor {
    private Long id;
    private String firstName;
    private String lastName;
    private Student student;

    public Mentor(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mentor mentor = (Mentor) o;
        return Objects.equals(id, mentor.id) &&
                Objects.equals(firstName, mentor.firstName) &&
                Objects.equals(lastName, mentor.lastName) &&
                Objects.equals(student, mentor.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, student);
    }

    @Override
    public String toString() {
        return "Mentor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName +
                '}';
    }
}
