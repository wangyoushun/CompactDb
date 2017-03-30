package com.six.compactdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.six.compactdb.dtd.DTDResolver;
import com.six.compactdb.exception.CompactdbException;
import com.six.compactdb.model.DataBaseConfig;
import com.six.compactdb.model.ScriptConfig;
import com.six.compactdb.model.TableConfig;
import com.six.compactdb.util.EnergydbActionUtil;

public class EnergyDataBaseConfiguration {
    private static final Log log = LogFactory.getLog(EnergyDataBaseConfiguration.class);
    public static final String DEFAULT = "default";
    private static EnergyDataBaseConfiguration configuration = null;
    private EnergyResourceMonitor energyResourceMonitor = new EnergyResourceMonitor(this);
    private Properties properties = new Properties();
    private Map<String, DataBaseConfiguration> dataBaseConfigurations = new LinkedHashMap<String, DataBaseConfiguration>();

    private EnergyDataBaseConfiguration() {
        this.configure();
    }

    public static EnergyDataBaseConfiguration getConfiguration() {
        if(configuration == null) {
            configuration = new EnergyDataBaseConfiguration();
        }

        return configuration;
    }

    private void configure() {
        log.info("==========compactdb load configure====================");
        log.info(System.getProperties());
        this.loadConfig();
        if(this.isResourceMonitor()) {
            this.energyResourceMonitor.startMonitor();
        }

    }

    public DataBaseConfiguration getDataBaseConfiguration() throws CompactdbException {
        return this.getDataBaseConfiguration("default");
    }

    public String[] getDataBaseNames() {
        Iterator<String> it = this.dataBaseConfigurations.keySet().iterator();

        String[] names;
        for(names = (String[])null; it.hasNext(); names = (String[])ArrayUtils.add(names, it.next())) {
            ;
        }

        return names;
    }

    public DataBaseConfiguration getDataBaseConfiguration(String id) throws CompactdbException {
        if(this.dataBaseConfigurations.containsKey(id)) {
            return (DataBaseConfiguration)this.dataBaseConfigurations.get(id);
        } else {
            throw new CompactdbException("未定义的数据库配置ID\"" + id + "\"");
        }
    }

    public EnergySessionFactory getEnergySessionFactory() throws CompactdbException {
        return this.getDataBaseConfiguration("default").getEnergySessionFactorys();
    }

    public EnergySessionFactory getEnergySessionFactory(String id) throws CompactdbException {
        return this.getDataBaseConfiguration(id).getEnergySessionFactorys();
    }

    private void loadConfig() {
        this.loadConfig("compactDbConfig.xml");
    }

    public void loadConfig(String resource) {
        try {
            SAXReader e = new SAXReader();
            e.setEntityResolver(DTDResolver.getInstance());
            e.setValidation(true);
            Document doc = e.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));
            Element root = doc.getRootElement();
            Element globalElement = root.element("compactdb");
            Element databaseElement;
            if(globalElement != null) {
                databaseElement = globalElement.element("properties");
                this.properties = this.loadProperties(databaseElement);
            }

            databaseElement = root.element("databases");
            if(databaseElement != null) {
                Iterator<?> it = databaseElement.elementIterator("database");

                while(it.hasNext()) {
                    Element element = (Element)it.next();
                    this.loadDataBaseConfig(element);
                }
            }

        } catch (Exception var9) {
            log.error(var9, var9);
            throw new CompactdbException(var9);
        }
    }

    private void loadDataBaseConfig(Element rootElement) {
        String name = rootElement.attributeValue("name");
        log.info("begin load \"" + name + "\" configuration...");
        Element configElement = rootElement.element("properties");
        Properties properties = this.loadProperties(configElement);
        List<TableConfig> tableList = new ArrayList<TableConfig>();
        Element tableElement = rootElement.element("tables");
        Element scriptElement;
        if(tableElement != null) {
            Iterator<?> scriptList = tableElement.elementIterator("table");

            while(scriptList.hasNext()) {
                scriptElement = (Element)scriptList.next();
                tableList.add(new TableConfig(scriptElement.attributeValue("id"), scriptElement.attributeValue("name"), scriptElement.attributeValue("keygen"), scriptElement.attributeValue("sequence")));
            }
        }

        List<ScriptConfig> scriptList1 = new ArrayList<ScriptConfig>();
        scriptElement = rootElement.element("scripts");
        if(scriptElement != null) {
            Iterator<?> dataBaseConfig = scriptElement.elementIterator("script");

            while(dataBaseConfig.hasNext()) {
                Element element = (Element)dataBaseConfig.next();
                ScriptConfig scriptConfig = new ScriptConfig(element.attributeValue("resource"));
                scriptList1.add(scriptConfig);
                if(this.isResourceMonitor()) {
                    this.energyResourceMonitor.addResource(name, scriptConfig.getClassPath());
                }
            }
        }

        if(this.dataBaseConfigurations.containsKey(name)) {
            DataBaseConfiguration dataBaseConfig1 = (DataBaseConfiguration)this.dataBaseConfigurations.get(name);
            dataBaseConfig1.addTable(tableList);
            dataBaseConfig1.addScript(scriptList1);
        } else {
            DataBaseConfig dataBaseConfig2 = new DataBaseConfig(properties, tableList, scriptList1);
            this.dataBaseConfigurations.put(name, DataBaseConfiguration.configurate(this, dataBaseConfig2));
        }

        log.info("finish load \"" + name + "\" configuration.");
    }

    private Properties loadProperties(Element configElement) {
        Properties properties = new Properties();
        if(configElement != null) {
            Map<String, Object> root = new HashMap<String, Object>();
            root.putAll(EnergydbActionUtil.propertiesToMap(System.getProperties()));
            String reference = configElement.attributeValue("reference");
            if(reference != null && !reference.trim().equals("")) {
                File it = new File(EnergydbActionUtil.compileString(reference, root));
                if(it.exists()) {
                    Properties element = new Properties();
                    FileInputStream propertyName = null;

                    try {
                        propertyName = new FileInputStream(it);
                        element.load(propertyName);
                        root.putAll(EnergydbActionUtil.propertiesToMap(element));
                    } catch (IOException var16) {
                        throw new RuntimeException(var16);
                    } finally {
                        if(propertyName != null) {
                            try {
                                propertyName.close();
                            } catch (IOException var15) {
                                ;
                            }
                        }

                    }
                }
            }

            Iterator<?> it1 = configElement.elementIterator("property");

            while(it1.hasNext()) {
                Element element1 = (Element)it1.next();
                String propertyName1 = element1.attributeValue("name");
                String propertyValue = EnergydbActionUtil.compileString(element1.getText() != null?element1.getText().trim():"", root);
                properties.put(propertyName1, propertyValue);
            }
        }

        return properties;
    }

    public boolean isResourceMonitor() {
        String value = this.properties.getProperty("compactdb.resource.reload", "false");
        return Boolean.valueOf(value).booleanValue();
    }

    public boolean isShowSql() {
        String value = this.properties.getProperty("compactdb.showsql", "false");
        return Boolean.valueOf(value).booleanValue();
    }
}
