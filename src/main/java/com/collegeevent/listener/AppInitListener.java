package com.collegeevent.listener;

import com.collegeevent.dao.DataSeeder;
import com.collegeevent.dao.DataStore;
import com.collegeevent.util.AppConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Runs when Tomcat starts the app: opens the JSON files, creates missing ones and seeds first-run data. */
@WebListener
public class AppInitListener implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(AppInitListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Path dir = AppConfig.resolveDataDir(sce.getServletContext());
            DataStore.init(dir);
            DataSeeder.seed();
            LOG.info("College Event Registration System data folder: " + dir.toAbsolutePath());
            sce.getServletContext().setAttribute("dataDir", dir.toAbsolutePath().toString());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "College Event Registration System could not start: unable to prepare the JSON data folder. "
                    + "Set -Dcollegeevent.data.dir=<writable folder> in the Tomcat launch configuration.", e);
            throw new IllegalStateException("College Event Registration System data folder problem: " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) { }
}
