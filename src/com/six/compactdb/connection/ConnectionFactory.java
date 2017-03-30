package com.six.compactdb.connection;

import com.six.compactdb.model.DataBaseConfig;

/**
 * connection factory
 * @author iwantfly
 */
public class ConnectionFactory {
	public ConnectionFactory() {
	}

	public static ConnectionProvider getConnectionProvider(
			DataBaseConfig dataBaseConfig) {
		return (ConnectionProvider) (dataBaseConfig.getJndiName() != null ? new DataSourceConnectionProvider(
				dataBaseConfig)
				: (dataBaseConfig.getPoolSize() > 0 ? new C3P0ConnectionProvider(
						dataBaseConfig) : new DriverManagerConnectionProvider(
						dataBaseConfig)));
	}
}