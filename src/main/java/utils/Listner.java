package utils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class Listner implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseManager.init(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseManager.close();
    }
}
