package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import repositories.JdbcCurrencyRepository;
import utils.DatabaseManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

@WebServlet("/hello")
public class testServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcCurrencyRepository jdbcCurrencyRepository = new JdbcCurrencyRepository();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter printWriter = resp.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Currency> list = jdbcCurrencyRepository.findAll();
            String json = objectMapper.writeValueAsString(list);
            printWriter.println(json);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
