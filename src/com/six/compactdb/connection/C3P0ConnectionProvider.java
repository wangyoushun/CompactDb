package com.six.compactdb.connection;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.six.compactdb.model.DataBaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * c3p0 connection
 * @author iwantfly
 */
public class C3P0ConnectionProvider implements ConnectionProvider {
	private DataBaseConfig dataBaseConfig = null;
	private ComboPooledDataSource dataSource;

	public C3P0ConnectionProvider(DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}

	public Connection getConnection() throws SQLException {
		try {
			if (this.dataSource == null) {
				this.dataSource = new ComboPooledDataSource();

				this.dataSource.setUser(this.dataBaseConfig.getLoginUser());

				this.dataSource.setPassword(this.dataBaseConfig
						.getLoginPassword());

				this.dataSource.setJdbcUrl(this.dataBaseConfig.getUrl());

				this.dataSource.setDriverClass(this.dataBaseConfig
						.getJdbcDriverClass());

				this.dataSource.setInitialPoolSize(2);

				this.dataSource.setMinPoolSize(1);

				this.dataSource.setMaxPoolSize(this.dataBaseConfig
						.getPoolSize());

				this.dataSource.setMaxStatements(500);

				this.dataSource.setMaxIdleTime(60);

				this.dataSource.setAcquireRetryAttempts(1);

				this.dataSource.setAutoCommitOnClose(true);

				this.dataSource.setTestConnectionOnCheckout(true);

				if (this.dataBaseConfig.getProperties() != null)
					this.dataSource.getProperties().putAll(
							this.dataBaseConfig.getProperties());
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		return this.dataSource.getConnection();
	}

	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	public void close() throws Exception {
		if (this.dataSource == null)
			return;
		try {
			this.dataSource.close();
		} catch (Exception localException) {
		}
		try {
			DataSources.destroy(this.dataSource);
		} catch (SQLException localSQLException) {
		}
		this.dataSource = null;
	}
}