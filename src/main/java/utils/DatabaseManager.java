package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static HikariDataSource dataSource;

    static {
        try{
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e){
            e.getStackTrace();
        }
    }

    public static void init(ServletContext servletContext){
        Properties properties = new Properties();

        try(InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/classes/db.properties")){
            if(inputStream == null){
                throw new RuntimeException("db.properties нет в /WEB-INF/classes/");
            }
            properties.load(inputStream);
        }catch (IOException e){
            throw new RuntimeException("Не удалось загрузить db.properties");
        }
        if(properties.isEmpty()){
            throw new RuntimeException("Пустой файл db.properties");
        }
        String path = properties.getProperty("db.path");
        if(path == null || path.isEmpty()){
            throw new RuntimeException("В db.properties отстствует ключ db.path");
        }

        HikariConfig config = new HikariConfig();
        String url = "jdbc:sqlite:" + path;
        config.setJdbcUrl(url);
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close(){
        dataSource.close();
    }
}
