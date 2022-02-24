import java.sql.Connection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class AbstractRepository<T> {
    String table;
    Connection connection;

    public AbstractRepository(Connection connection, String table) {
        this.table = table;
        this.connection = connection;
    }

    abstract public void createTable() throws SQLException;

    abstract public ArrayList<T> findAll(String query) throws SQLException;

    public Optional<T> findOne(String query) throws SQLException {
        var result = findAll(query);
        return result.isEmpty()
                ? Optional.empty()
                : Optional.of(result.get(0));
    }

    public Optional<T> findById(int id) throws SQLException {
        return findOne("SELECT * FROM " + table + " WHERE id = " + id);
    }

    abstract public void saveAll(ArrayList<T> data) throws SQLException;

    public void saveOne(T entity) throws SQLException{
        saveAll(new ArrayList<>(List.of(entity)));
    }

    // ToDo implement here or on the concrete class these methods
    // ToDo tell me why there is no "updateOneById"
    // ToDo tell me why there is no "deleteOne"

    abstract public void deleteOneById(int id);
    abstract public void deleteAll(); // <- ToDo Set the input types

    abstract public void updateOne(); // <- ToDo Set the input types
    abstract public void updateAll(); // <- ToDo Set the input types
}
