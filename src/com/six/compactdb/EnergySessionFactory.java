package com.six.compactdb;

import com.six.compactdb.exception.CompactdbException;

public class EnergySessionFactory {
    private DataBaseConfiguration dataBaseConfiguration = null;

    public EnergySessionFactory(DataBaseConfiguration dataBaseConfiguration) {
        this.dataBaseConfiguration = dataBaseConfiguration;
    }

    public DataBaseConfiguration getDataBaseConfiguration() {
        return this.dataBaseConfiguration;
    }

    public EnergySession getEnergySession() throws CompactdbException {
        return this.dataBaseConfiguration.getEnergySession();
    }
}
