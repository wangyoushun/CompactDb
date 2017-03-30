package com.six.compactdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.six.compactdb.connection.ConnectionProvider;
import com.six.compactdb.connection.EnergyConnectionWrapper;
import com.six.compactdb.exception.CompactdbException;

public class EnergySession {
    private DataBaseConfiguration dataBaseConfiguration = null;
    private ConnectionProvider connectionProvider = null;
    private ActionDao actionDao = null;
    private Connection connection = null;
    private List<Object> clears = null;

    public EnergySession(DataBaseConfiguration dataBaseConfiguration, ConnectionProvider connectionProvider) throws CompactdbException {
        this.dataBaseConfiguration = dataBaseConfiguration;
        this.connectionProvider = connectionProvider;

        try {
            this.connection = connectionProvider.getConnection();
        } catch (SQLException var4) {
            throw new CompactdbException(var4);
        }

        this.actionDao = new ActionDaoImpl(this);
    }

    public Connection getConnection() {
        return new EnergyConnectionWrapper(this, this.connection);
    }

    public DataBaseConfiguration getConfiguration() {
        return this.dataBaseConfiguration;
    }

    public ActionDao getActionDao() {
        return this.actionDao;
    }

    public void beginTranscation() throws CompactdbException {
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException var2) {
            throw new CompactdbException(var2);
        }
    }

    public void commitTranscation() throws CompactdbException {
        try {
            this.connection.commit();
            if(!this.connection.getAutoCommit()) {
                try {
                    this.connection.setAutoCommit(true);
                } catch (SQLException var2) {
                    ;
                }
            }

        } catch (SQLException var3) {
            throw new CompactdbException(var3);
        }
    }

    public void rollbackTranscation() throws CompactdbException {
        try {
            this.connection.rollback();
            if(!this.connection.getAutoCommit()) {
                try {
                    this.connection.setAutoCommit(true);
                } catch (SQLException var2) {
                    ;
                }
            }

        } catch (SQLException var3) {
            throw new CompactdbException(var3);
        }
    }

    public void addClears(Object object) {
        if(this.clears == null) {
            this.clears = new ArrayList<Object>();
        }

        this.clears.add(object);
    }

    public void close() throws CompactdbException {
        try {
            this.clear();
            this.connectionProvider.closeConnection(this.connection);
        } catch (SQLException var2) {
            throw new CompactdbException(var2);
        }
    }

    private void clear() {
        if(this.clears != null && this.clears.size() > 0) {
            for(int i = 0; i < this.clears.size(); ++i) {
                Object obj = this.clears.get(i);
                if(obj != null) {
                    try {
                        if(obj instanceof ResultSet) {
                            ((ResultSet)obj).close();
                        } else if(obj instanceof Statement) {
                            ((Statement)obj).close();
                        }
                    } catch (SQLException var4) {
                        ;
                    }
                }
            }

            this.clears.clear();
        }

    }

    public String getDataBaseTypeName() {
        return this.dataBaseConfiguration.getDataBaseTypeName();
    }
}
