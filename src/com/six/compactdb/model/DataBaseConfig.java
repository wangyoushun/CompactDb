package com.six.compactdb.model;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.six.compactdb.dialect.Dialect;
import com.six.compactdb.dialect.DialectFactory;

/**
 * 数据库配置
 * @author iwantfly
 */
public class DataBaseConfig {
    private Dialect dialect = null;
    private String jndiName = null;
    private String jndiUrl = null;
    private String jndiClass = null;
    private String jdbcDriverClass = null;
    private String url = null;
    private String loginUser = null;
    private String loginPassword = null;
    private String catalog = null;
    private String schema = null;
    private int isolation;
    private int poolSize = 0;
    private int batchSize = 20;
    private List<TableConfig> tableConfigs = null;
    private List<ScriptConfig> sQLscriptConfigs = null;
    private Properties properties = null;

    public DataBaseConfig(Properties parameters, List<TableConfig> tableConfigs, List<ScriptConfig> sQLscriptConfigs) {
        String compactdbDatabaseType = parameters.getProperty("compactdb.database.type", "");
        this.jdbcDriverClass = parameters.getProperty("compactdb.database.connection.driverclass");
        this.url = parameters.getProperty("compactdb.database.connection.url");
        this.loginUser = parameters.getProperty("compactdb.database.connection.user");
        this.loginPassword = parameters.getProperty("compactdb.database.connection.password");
        this.poolSize = Integer.parseInt(parameters.getProperty("compactdb.database.c3p0.poolsize", "0"));
        this.batchSize = Integer.parseInt(parameters.getProperty("compactdb.database.batch.size", "20"));
        this.catalog = parameters.getProperty("compactdb.database.catalog");
        this.schema = parameters.getProperty("compactdb.database.schema");
        this.jndiName = parameters.getProperty("compactdb.database.jndi");
        this.jndiClass = parameters.getProperty("compactdb.database.jndi.class");
        this.jndiUrl = parameters.getProperty("compactdb.database.jndi.url");
        this.isolation = Integer.parseInt(parameters.getProperty("compactdb.database.connection.isolation", String.valueOf(2)));
        this.dialect = DialectFactory.getDialect(compactdbDatabaseType);
        this.tableConfigs = tableConfigs;
        this.sQLscriptConfigs = sQLscriptConfigs;
        Iterator<?> it = parameters.keySet().iterator();

        while(it.hasNext()) {
            String name = (String)it.next();
            if(name.startsWith("compactdb.database.connection.property.")) {
                String propertyName = name.substring("compactdb.database.connection.property.".length());
                String value = parameters.getProperty(name);
                if(this.properties == null) {
                    this.properties = new Properties();
                }

                this.properties.setProperty(propertyName, value);
            }
        }

    }

    public Dialect getDialect() {
        return this.dialect;
    }

    public String getJndiName() {
        return this.jndiName;
    }

    public String getJdbcDriverClass() {
        return this.jdbcDriverClass;
    }

    public String getUrl() {
        return this.url;
    }

    public String getLoginUser() {
        return this.loginUser;
    }

    public String getLoginPassword() {
        return this.loginPassword;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public String getSchema() {
        return this.schema;
    }

    public int getPoolSize() {
        return this.poolSize;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public List<TableConfig> getTableConfigs() {
        return this.tableConfigs;
    }

    public List<ScriptConfig> getSQLscriptConfigs() {
        return this.sQLscriptConfigs;
    }

    public String getJndiUrl() {
        return this.jndiUrl;
    }

    public String getJndiClass() {
        return this.jndiClass;
    }

    public int getIsolation() {
        return this.isolation;
    }

    public Properties getProperties() {
        return this.properties;
    }
}
