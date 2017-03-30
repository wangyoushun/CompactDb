package com.six.compactdb;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.six.compactdb.connection.ConnectionFactory;
import com.six.compactdb.connection.ConnectionProvider;
import com.six.compactdb.dialect.Dialect;
import com.six.compactdb.dtd.DTDResolver;
import com.six.compactdb.exception.CompactdbException;
import com.six.compactdb.model.Column;
import com.six.compactdb.model.DataBaseConfig;
import com.six.compactdb.model.SQLParameter;
import com.six.compactdb.model.SQLScript;
import com.six.compactdb.model.ScriptConfig;
import com.six.compactdb.model.Table;
import com.six.compactdb.model.TableConfig;

public class DataBaseConfiguration {
    private static final Log log = LogFactory.getLog(DataBaseConfiguration.class);
    private EnergyDataBaseConfiguration energyDataBaseConfiguration = null;
    private TableMapping tableMapping = new TableMapping();
    private SQLScriptMapping sQLScriptMapping = new SQLScriptMapping();
    private EnergySessionFactory energySessionFactorys = null;
    private DataBaseConfig dataBaseConfig = null;
    private ConnectionProvider connectionProvider = null;
    private boolean inited = false;

    private DataBaseConfiguration(EnergyDataBaseConfiguration energyDataBaseConfiguration, DataBaseConfig dataBaseConfig) {
        this.energyDataBaseConfiguration = energyDataBaseConfiguration;
        this.dataBaseConfig = dataBaseConfig;
        this.energySessionFactorys = new EnergySessionFactory(this);
        this.connectionProvider = ConnectionFactory.getConnectionProvider(dataBaseConfig);

        try {
            this.addTable(dataBaseConfig.getTableConfigs());
        } catch (Exception var4) {
            log.info("abort init table mapping first");
        }

        this.addScript(dataBaseConfig.getSQLscriptConfigs());
    }

    public static DataBaseConfiguration configurate(EnergyDataBaseConfiguration energyDataBaseConfiguration, DataBaseConfig dataBaseConfig) throws CompactdbException {
        DataBaseConfiguration dataBaseConfiguration = new DataBaseConfiguration(energyDataBaseConfiguration, dataBaseConfig);
        return dataBaseConfiguration;
    }

    public void addTable(List<TableConfig> tables) throws CompactdbException {
        if(tables != null && tables.size() != 0) {
            EnergySession session = null;
            TableConfig tableConfig = null;

            try {
                session = new EnergySession(this, this.connectionProvider);

                for(int e = 0; tables != null && e < tables.size(); ++e) {
                    tableConfig = (TableConfig)tables.get(e);
                    Table table = this.loadTable(session, this.getDataBaseConfig().getDialect(), tableConfig.getName(), tableConfig.getKeyGenerate(), tableConfig.getSequence());
                    if(table == null) {
                        throw new CompactdbException("无法获取表\"" + tableConfig.getName() + "\"的信息，请确定该表是否存在");
                    }

                    log.info("load table \"" + tableConfig.getName() + "\" successed");
                    this.tableMapping.addTable(tableConfig.getId(), table);
                }

                this.inited = true;
            } catch (SQLException var9) {
                if(tableConfig != null) {
                    log.info("load table \"" + tableConfig.getName() + "\" failed");
                }

                throw new CompactdbException(var9);
            } finally {
                if(session != null) {
                    session.close();
                }

            }
        } else {
            this.inited = true;
        }
    }

    public void addScript(List<ScriptConfig> scripts) throws CompactdbException {
        ScriptConfig scriptConfig = null;

        try {
            for(int e = 0; scripts != null && e < scripts.size(); ++e) {
                scriptConfig = (ScriptConfig)scripts.get(e);
                this.loadConfig(scriptConfig.getClassPath());
                log.info("load script \"" + scriptConfig.getClassPath() + "\" successed");
            }

        } catch (Exception var4) {
            if(scriptConfig != null) {
                log.info("load script \"" + scriptConfig.getClassPath() + "\" failed");
            }

            log.error(var4, var4);
            throw new CompactdbException(var4);
        }
    }

    public EnergyDataBaseConfiguration getEnergyConfiguration() {
        return this.energyDataBaseConfiguration;
    }

    public EnergySessionFactory getEnergySessionFactorys() {
        return this.energySessionFactorys;
    }

    public TableMapping getTableMapping() {
        return this.tableMapping;
    }

    public SQLScriptMapping getSQLScriptMapping() {
        return this.sQLScriptMapping;
    }

    public DataBaseConfig getDataBaseConfig() {
        return this.dataBaseConfig;
    }

    private Table loadTable(EnergySession session, Dialect dialect, String table, String keyGenerate, String sequence) throws SQLException {
    	List<Column> keys = new ArrayList<Column>();
        Map<String,Column> keyMap = new HashMap<String,Column>();
        List<Column> columns = new ArrayList<Column>();
        ResultSet rsKey = null;

        try {
//          System.out.println(this.getDataBaseConfig().getCatalog() + "----" + this.getDataBaseConfig().getSchema());
            rsKey = session.getConnection().getMetaData().getPrimaryKeys(this.getDataBaseConfig().getCatalog(), this.getDataBaseConfig().getSchema(), table);

            while(rsKey.next()) {
                Column rsColumn = new Column();
                rsColumn.setName(rsKey.getString("COLUMN_NAME"));
                keys.add(rsColumn);
                keyMap.put(rsColumn.getName(), rsColumn);
            }
        } finally {
            if(rsKey != null) {
                rsKey.close();
            }

        }

        ResultSet rsColumn1 = null;

        try {
            rsColumn1 = session.getConnection().getMetaData().getColumns(this.getDataBaseConfig().getCatalog(), this.getDataBaseConfig().getSchema(), table, (String)null);

            while(rsColumn1.next()) {
                Column column = null;
                String name = rsColumn1.getString("COLUMN_NAME");
                if(keyMap.containsKey(name)) {
                    column = (Column)keyMap.get(name);
                    column.setNullAble(false);
                    column.setPrimaryKey(true);
                } else {
                    column = new Column();
                    column.setName(name);
                    if(rsColumn1.getInt("NULLABLE") == 0) {
                        column.setNullAble(false);
                    } else {
                        column.setNullAble(true);
                    }

                    column.setPrimaryKey(false);
                }

                column.setLength(rsColumn1.getInt("COLUMN_SIZE"));
                column.setScale(rsColumn1.getInt("DECIMAL_DIGITS"));
                column.setDataType(rsColumn1.getInt("DATA_TYPE"));
                columns.add(column);
            }
        } finally {
            if(rsColumn1 != null) {
                rsColumn1.close();
            }

        }

        return columns.size() == 0?null:new Table(table, keys, columns, dialect, keyGenerate, sequence);
    }

    private void loadConfig(String path) {
        try {
            SAXReader e = new SAXReader();
            e.setEntityResolver(DTDResolver.getInstance());
            e.setValidation(true);
            URL url = Thread.currentThread().getContextClassLoader().getResource(path);
            if(url != null) {
                Document doc = null;
                File dataFile = new File(url.getPath());
                if(dataFile.exists()) {
                    doc = e.read(new FileInputStream(dataFile));
                } else {
                    doc = e.read(Thread.currentThread().getContextClassLoader().getResource(path));
                }

                Element root = doc.getRootElement();
                String namespace = root.attributeValue("namespace");
                Element defaultElement = root.element("default");
                this.loadScripts(namespace, defaultElement);
                Element dialectElement = root.element(this.dataBaseConfig.getDialect().getName());
                this.loadScripts(namespace, dialectElement);
            } else {
                throw new CompactdbException("\"" + path + "\" not exist!");
            }
        } catch (Exception var10) {
            throw new CompactdbException(var10);
        }
    }

    private void loadScripts(String namespace, Element rootElement) {
        if(rootElement != null) {
            Iterator<?> it = rootElement.elementIterator("script");

            while(true) {
                Element scriptElement;
                String id;
                String isQuery;
                String script;
                do {
                    do {
                        Element sqlElement;
                        do {
                            if(!it.hasNext()) {
                                return;
                            }

                            scriptElement = (Element)it.next();
                            id = scriptElement.attributeValue("name");
                            isQuery = scriptElement.attributeValue("isquery");
                            sqlElement = scriptElement.element("sql");
                        } while(sqlElement == null);

                        script = sqlElement.getText();
                    } while(script == null);
                } while(script.trim().equals(""));

                Element parameterElement = scriptElement.element("parameters");
                List<SQLParameter> parameters = null;
                Element element;
                if(parameterElement != null) {
                    for(Iterator<?> itx = parameterElement.elementIterator("parameter"); itx.hasNext(); parameters.add(new SQLParameter(element.attributeValue("name"), element.attributeValue("datatype"), element.attributeValue("type")))) {
                        element = (Element)itx.next();
                        if(parameters == null) {
                            parameters = new ArrayList<SQLParameter>();
                        }
                    }
                }

                if(namespace != null && !namespace.trim().equals("")) {
                    id = namespace + "." + id;
                }

                this.sQLScriptMapping.addSQLScript(id, new SQLScript(script.trim(), parameters, isQuery));
            }
        }
    }

    public EnergySession getEnergySession() throws CompactdbException {
        if(!this.inited) {
            log.info("retry to init table mapping");
            this.addTable(this.dataBaseConfig.getTableConfigs());
        }

        return new EnergySession(this, this.connectionProvider);
    }

    public String getDataBaseTypeName() {
        return this.dataBaseConfig.getDialect().getName();
    }
}
