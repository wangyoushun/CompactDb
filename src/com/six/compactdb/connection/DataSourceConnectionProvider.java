package com.six.compactdb.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.six.compactdb.model.DataBaseConfig;

/**
 * jndi connection
 * @author iwantfly
 *
 */
public class DataSourceConnectionProvider implements ConnectionProvider {
	private DataSource ds;
	private DataBaseConfig dataBaseConfig = null;

	public DataSourceConnectionProvider(DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}

	public Connection getConnection() throws SQLException {
		if (this.ds == null) {
			try {
				if (this.dataBaseConfig.getJndiUrl() == null
						&& this.dataBaseConfig.getJndiClass() == null) {
					this.ds = (DataSource) (new InitialContext())
							.lookup(this.dataBaseConfig.getJndiName());
				} else {
					Properties e = new Properties();
					e.put("java.naming.factory.initial", "");
					e.put("java.naming.provider.url", "");
					this.ds = (DataSource) (new InitialContext(e))
							.lookup(this.dataBaseConfig.getJndiName());
				}
			} catch (NamingException e) {
				throw new SQLException(e.getMessage());
			}
		}

		return this.dataBaseConfig.getLoginUser() != null
				&& this.dataBaseConfig.getLoginPassword() != null ? this.ds
				.getConnection(this.dataBaseConfig.getLoginUser(),
						this.dataBaseConfig.getLoginPassword()) : this.ds
				.getConnection();
	}

	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	public void close() throws Exception {
	}
}
