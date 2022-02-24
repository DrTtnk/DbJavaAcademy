import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            var connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/MyDatabase",
                    "postgres",
                    "myPassword"
            );

            var studentRepository = new StudentRepository(connection, "my_schema.students");

            studentRepository.createTable();

            var data = new ArrayList<StudentEntity>();
            data.add(new StudentEntity("John", "Doe", "Engineering", "A"));
            data.add(new StudentEntity("Jane", "Doe", "Physics", "B"));
            data.add(new StudentEntity("Jack", "Rossi", "Physics", "C"));
            data.add(new StudentEntity("Jill", "Doe", "Engineering", "D"));
            data.add(new StudentEntity("John", "Doe", "Physics", "E"));
            data.add(new StudentEntity("John", "Doe", "Mathematics", "E"));
            data.add(new StudentEntity("John", "Doe", "Philosophy", "E"));

            studentRepository.saveAll(data);

            var studentName = studentRepository.findById(1)
                    .map(s -> s.name)
                    .orElse("There is no student");

            System.out.println(studentName);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
