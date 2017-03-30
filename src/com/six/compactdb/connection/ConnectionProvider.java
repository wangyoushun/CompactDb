package com.six.compactdb.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *@author iwantfly
 */
public interface ConnectionProvider {
    Connection getConnection() throws SQLException;

    void closeConnection(Connection var1) throws SQLException;

    void close() throws Exception;
}
