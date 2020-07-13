package repositories;

import models.Mentor;
import models.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class StudentsRepositoryJdbcImpl implements StudentsRepository {

    //language=SQL
    private static final String SQL_SELECT_BY_ID = "select * from student left join mentor m on student.id = m.student_id where id = ";
    private static final String SQL_SELECT_BY_AGE = "select * from student left join mentor m on student.id = m.student_id where age = ";
    private static final String SQL_NEW_STUDENT = "insert into student (first_name, last_name, age, group_number) values (";
    private static final String SQL_SELECT_ALL = "SELECT * FROM student left join mentor m on student.id = m.student_id";

    public List<Student> getStudentsList (Statement statement, ResultSet result, String searchParameter) {
        LinkedList<Student> slist = new LinkedList<>();
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(searchParameter);
            while (result.next()) {
                Student student = new Student (
                        result.getLong("id"),
                        result.getString("first_name").trim(),
                        result.getString("last_name").trim(),
                        result.getInt("age"),
                        result.getInt("group_number")
                );
                if (student.getMentors() == null) {
                    student.setMentors(new LinkedList<>());
                }
                if (result.getString("m_first_name") != null) {
                    Mentor mentor = new Mentor(
                            result.getLong("m_id"),
                            result.getString("m_first_name").trim(),
                            result.getString("m_last_name").trim()
                    );
                    if (!slist.contains(student)) {
                        List<Mentor> mlist = student.getMentors();
                        mlist.add(mentor);
                        student.setMentors(mlist);
                        slist.add(student);
                    } else for (Student s : slist) {
                        if (s.getId().equals(student.getId())) {
                            student.setMentors(s.getMentors());
                            slist.remove(s);
                            List<Mentor> mlist = student.getMentors();
                            mlist.add(mentor);
                            student.setMentors(mlist);
                            slist.add(student);
                        }
                    }
                }
                else slist.add(student);
            }
            return slist;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    private Connection connection;

    public StudentsRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Student> findAllByAge(int age) {
        Statement statement = null;
        ResultSet result = null;
        return getStudentsList(statement, result, SQL_SELECT_BY_AGE + age);
    }

    @Override
    public List<Student> findAll() {
        Statement statement = null;
        ResultSet result = null;
        return getStudentsList(statement, result, SQL_SELECT_ALL);
    }

    @Override
    public Student findById(Long id) {
        Statement statement = null;
        ResultSet result = null;
        return getStudentsList(statement, result, SQL_SELECT_BY_ID + id).get(0);
    }

    @Override
    public void save(Student entity) {
        Statement statement, statement1 = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            statement.executeUpdate(SQL_NEW_STUDENT + "'" + entity.getFirstName() + "', " + "'" + entity.getLastName() + "', " + entity.getAge() + ", " + entity.getGroupNumber() + ")");
            statement1 = connection.createStatement();
            result = statement1.executeQuery("SELECT max(id) as i FROM student");
            if (result.next()) {
                long l = result.getLong("i");
                entity.setId(l);
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
            if (statement1 != null) {
                try {
                    statement1.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
        }
    }

    @Override
    public void update(Student entity) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("update student set first_name = " + "'" + entity.getFirstName() + "'" + ", last_name = " + "'" + entity.getLastName() + "'" + ", age = " +
                    entity.getAge() + ", group_number = " + entity.getGroupNumber() + " where id = " + entity.getId());
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    //ignore
                }
            }
        }
    }
}