package com.six.compactdb;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.six.compactdb.DataBaseConfiguration;
import com.six.compactdb.EnergyDataBaseConfiguration;
import com.six.compactdb.Resource;
import com.six.compactdb.model.ScriptConfig;

public class EnergyResourceMonitor implements Runnable {
    private EnergyDataBaseConfiguration energyDataBaseConfiguration;
    private List<Resource> resources = new ArrayList<Resource>();

    public EnergyResourceMonitor(EnergyDataBaseConfiguration energyDataBaseConfiguration) {
        this.energyDataBaseConfiguration = energyDataBaseConfiguration;
    }

    public void addResource(String name, String classPath) {
        synchronized(this.resources) {
            if(this.resources == null) {
                this.resources = new ArrayList<Resource>();
            }

            URL url = Thread.currentThread().getContextClassLoader().getResource(classPath);
            if(url != null) {
                File resourceFile = new File(url.getPath());
                if(resourceFile.exists()) {
                    this.resources.add(new Resource(name, classPath, resourceFile, resourceFile.lastModified()));
                }
            }

        }
    }

    public void startMonitor() {
        (new Thread(this)).start();
    }

    public void run() {
        while(true) {
            synchronized(this.resources) {
                int i = 0;

                while(true) {
                    if(this.resources == null || i >= this.resources.size()) {
                        break;
                    }

                    Resource resource = (Resource)this.resources.get(i);
                    if(resource.isModified()) {
                        this.reload(resource.getName(), resource.getClassPath());
                        resource.updateTime();
                    }

                    ++i;
                }
            }

            try {
                Thread.sleep(5000L);
            } catch (InterruptedException var4) {
                ;
            }
        }
    }

    private void reload(String name, String classPath) {
        DataBaseConfiguration cataBaseConfiguration = this.energyDataBaseConfiguration.getDataBaseConfiguration(name);
        List<ScriptConfig> scripts = new ArrayList<ScriptConfig>();
        scripts.add(new ScriptConfig(classPath));
        cataBaseConfiguration.addScript(scripts);
    }
}
