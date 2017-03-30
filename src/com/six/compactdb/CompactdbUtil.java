package com.six.compactdb;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.six.compactdb.exception.CompactdbException;
import com.six.compactdb.model.ScriptConfig;
import com.six.compactdb.model.TableConfig;

public class CompactdbUtil {
    private static final ThreadLocal<Map<String, EnergySession>> sessionLocal = new ThreadLocal<Map<String, EnergySession>>();
    private static final EnergyDataBaseConfiguration configuration = EnergyDataBaseConfiguration.getConfiguration();

    public CompactdbUtil() {
    }

    public static EnergySession currentSession() throws CompactdbException {
        return currentSession("default");
    }

    public static EnergySession[] currentAllSession() throws CompactdbException {
        String[] names = configuration.getDataBaseNames();
        EnergySession[] session = (EnergySession[])null;

        for(int i = 0; names != null && i < names.length; ++i) {
            session = (EnergySession[])ArrayUtils.add(session, currentSession(names[i]));
        }

        return session;
    }

    public static EnergySession currentSession(String databaseId) throws CompactdbException {
        EnergySession session = null;
        Map<String, EnergySession> sessions = sessionLocal.get();
        if(sessions == null) {
            sessions = new HashMap<String, EnergySession>();
            sessionLocal.set(sessions);
        }

        if(sessions.containsKey(databaseId)) {
            session = sessions.get(databaseId);
        } else {
            EnergySessionFactory factory = configuration.getEnergySessionFactory(databaseId);
            session = factory.getEnergySession();
            sessions.put(databaseId, session);
        }

        return session;
    }

    public static ActionDao currentActionDao() throws CompactdbException {
        return currentActionDao("default");
    }

    public static ActionDao currentActionDao(String databaseId) throws CompactdbException {
        return currentSession(databaseId).getActionDao();
    }

    public static void closeSession() throws CompactdbException {
        Map<String, EnergySession> sessions = sessionLocal.get();
        if(sessions != null) {
            Iterator<EnergySession> it = sessions.values().iterator();

            while(it.hasNext()) {
                EnergySession session = (EnergySession)it.next();

                try {
                    session.close();
                } catch (CompactdbException var4) {
                    ;
                }
            }

            sessionLocal.set(null);
        }

    }

    public static void registerTables(String databaseId, List<TableConfig> tables) throws CompactdbException {
        DataBaseConfiguration cataBaseConfiguration = configuration.getDataBaseConfiguration(databaseId);
        cataBaseConfiguration.addTable(tables);
    }

    public static void registerScripts(String databaseId, List<ScriptConfig> scripts) throws CompactdbException {
        DataBaseConfiguration cataBaseConfiguration = configuration.getDataBaseConfiguration(databaseId);
        cataBaseConfiguration.addScript(scripts);
    }
}
