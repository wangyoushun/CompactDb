package com.six.compactdb.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.six.compactdb.model.DataBaseConfig;

/**
 * 
 * @author iwantfly
 *
 */
public class DriverManagerConnectionProvider implements ConnectionProvider {
	private DataBaseConfig dataBaseConfig = null;

	DriverManagerConnectionProvider(DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}

	public Connection getConnection() throws SQLException {
		try {
			Class.forName(this.dataBaseConfig.getJdbcDriverClass());
		} catch (ClassNotFoundException e) {
			throw new SQLException("driver class \""
					+ this.dataBaseConfig.getJdbcDriverClass() + "\" not found");
		}

		Properties prop = new Properties();
		prop.setProperty("user", this.dataBaseConfig.getLoginUser());
		prop.setProperty("password", this.dataBaseConfig.getLoginPassword());
		if (this.dataBaseConfig.getProperties() != null) {
			prop.putAll(this.dataBaseConfig.getProperties());
		}

		Connection conn = DriverManager.getConnection(
				this.dataBaseConfig.getUrl(), prop);
		conn.setTransactionIsolation(this.dataBaseConfig.getIsolation());
		if (!conn.getAutoCommit()) {
			conn.setAutoCommit(true);
		}

		return conn;
	}

	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	public void close() {
	}
}
