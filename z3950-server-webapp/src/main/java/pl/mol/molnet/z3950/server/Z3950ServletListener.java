package pl.mol.molnet.z3950.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jzkit.z3950.server.Z3950Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Główny servlet tylko startuje serwer z3950
 *
 * @author Paweł
 */
public class Z3950ServletListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(Z3950ServletListener.class);
    private Z3950Listener listener;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ApplicationContext app_context = new ClassPathXmlApplicationContext("applicationContext.xml");
            listener = (Z3950Listener) app_context.getBean("Z3950Listener", Z3950Listener.class);
            listener.start();
            LOG.debug("Server daemon thread started");
        } catch (Exception ex) {
            LOG.error("Problem starting socket server daemon thread" + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (listener != null) {
            listener.shutdown(0);
            LOG.debug("Server daemon thread stopped");
        }
    }
}
