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
    private static final String SQL_SELECT_BY_ID = "select * from student where id = ";
    private static final String SQL_SELECT_BY_AGE = "select * from student where age = ";
    private static final String SQL_NEW_STUDENT = "insert into student (first_name, last_name, age, group_number) values (";

    private Connection connection;

    public StudentsRepositoryJdbcImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Student> findAllByAge(int age) {
        Statement statement = null;
        ResultSet result = null;
        LinkedList<Student> list = new LinkedList<>();
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_BY_AGE + age);
            if (result.next()) {
                list.add(new Student(
                        result.getLong("id"),
                        result.getString("first_name").trim(),
                        result.getString("last_name").trim(),
                        result.getInt("age"),
                        result.getInt("group_number")
                ));
            } else return null;
            return list;
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

    // Необходимо вытащить список всех студентов, при этом у каждого студента должен быть проставлен список менторов
    // у менторов в свою очередь ничего проставлять (кроме имени, фамилии, id не надо)
    // student1(id, firstName, ..., mentors = [{id, firstName, lastName, null}, {}, ), student2, student3
    // все сделать одним запросом
    @Override
    public List<Student> findAll() {
        Statement statement = null;
        ResultSet result = null;
        LinkedList<Student> slist = new LinkedList<>();
        try {
            statement = connection.createStatement();
            result = statement.executeQuery("SELECT * FROM student left join mentor m on student.id = m.student_id");
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

    @Override
    public Student findById(Long id) {
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(SQL_SELECT_BY_ID + id);
            if (result.next()) {
                return new Student(
                        result.getLong("id"),
                        result.getString("first_name").trim(),
                        result.getString("last_name").trim(),
                        result.getInt("age"),
                        result.getInt("group_number")
                );
            } else return null;
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

    // просто вызывается insert для сущности
    // student = Student(null, 'Марсель', 'Сидиков', 26, 915)
    // studentsRepository.save(student);
    // // student = Student(3, 'Марсель', 'Сидиков', 26, 915)
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

    // для сущности, у которой задан id выполнить обновление всех полей

    // student = Student(3, 'Марсель', 'Сидиков', 26, 915)
    // student.setFirstName("Игорь")
    // student.setLastName(null);
    // studentsRepository.update(student);
    // (3, 'Игорь', null, 26, 915)

    private static final String UpDATE_F = "update student set first_name = ";
    private static final String UPDATE_L = ", last_name = ";
    private static final String UPDATE_AGE = ", age = ";
    private static final String UPDATE_GR = ", group_number = ";
    private static final String ID = " where id = ";

    @Override
    public void update(Student entity) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(UpDATE_F + "'" +entity.getFirstName() + "'" + UPDATE_L + "'" + entity.getLastName() + "'" + UPDATE_AGE +
                    entity.getAge() + UPDATE_GR + entity.getGroupNumber() + ID + entity.getId());
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