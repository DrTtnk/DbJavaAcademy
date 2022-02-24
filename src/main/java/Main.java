import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class Student extends Object {
    public String name;
    public String surname;
    public String department;
    public String drivingLicense;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", department='" + department + '\'' +
                ", drivingLicense='" + drivingLicense + '\'' +
                '}';
    }

    public Student(String name, String surname, String department, String drivingLicense) {
        this.name = name;
        this.surname = surname;
        this.department = department;
        this.drivingLicense = drivingLicense;
    }
}

public class Main {

    public static void createTable(Connection connection) throws SQLException {
        var statement = connection.createStatement();
        statement.executeUpdate("""
                    DROP SCHEMA IF EXISTS my_schema CASCADE;
                    CREATE SCHEMA IF NOT EXISTS my_schema;
                    CREATE TABLE my_schema.students (
                        id              SERIAL PRIMARY KEY,
                        name            TEXT NOT NULL,
                        surname         TEXT NOT NULL,
                        department      TEXT DEFAULT 'no department',
                        driving_license TEXT
                    )
                """);
    }

    public static ArrayList<Student> readAllData(Connection connection, String query) throws SQLException {
        var resultSet = connection.createStatement().executeQuery(query);
        var data = new ArrayList<Student>();
        while (resultSet.next()) {
            data.add(new Student(
                    resultSet.getString("name"),
                    resultSet.getString("surname"),
                    resultSet.getString("department"),
                    resultSet.getString("driving_license")
            ));
        }
        return data;
    }

    public static Optional<Student> readSingleRecordByID(Connection connection, int id) throws SQLException {
        var result = readAllData(connection, "SELECT * FROM my_schema.students WHERE id = " + id);
        return result.isEmpty()
                ? Optional.empty()
                : Optional.of(result.get(0));
    }

    static void populateTable(Connection connection, ArrayList<Student> data) throws SQLException {
        var bulkInsertString = """
                    INSERT INTO my_schema.students (name, surname, department, driving_license)
                    VALUES (?, ?, ?, ?)
                """;
        var preparedStatement = connection.prepareStatement(bulkInsertString);

        for (var student : data) {
            preparedStatement.setString(1, student.name);
            preparedStatement.setString(2, student.surname);
            preparedStatement.setString(3, student.department);
            preparedStatement.setString(4, student.drivingLicense);
            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
    }


    // saveSingleRecord
    public static void saveSingleRecord(Connection connection, Student student) throws SQLException {
        populateTable(connection, new ArrayList<>(List.of(student)));
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            var connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/MyDatabase",
                    "postgres",
                    "myPassword"
            );
            createTable(connection);

            var data = new ArrayList<Student>();
            data.add(new Student("John", "Doe", "Engineering", "A"));
            data.add(new Student("Jane", "Doe", "Physics", "B"));
            data.add(new Student("Jack", "Rossi", "Physics", "C"));
            data.add(new Student("Jill", "Doe", "Engineering", "D"));
            data.add(new Student("John", "Doe", "Physics", "E"));
            data.add(new Student("John", "Doe", "Mathematics", "E"));
            data.add(new Student("John", "Doe", "Philosophy", "E"));

            populateTable(connection, data);

            var studentName = readSingleRecordByID(connection, 10000)
                    .map(s -> s.name)
                    .orElse("There is no student");

            System.out.println(studentName);


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
