package filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.CurrencyAlreadyExistsException;
import exception.DatabaseException;
import exception.ExchangeRateNotFoundException;
import exception.ValidationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ErrorResponse;
import org.sqlite.SQLiteErrorCode;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        try{
            chain.doFilter(req,res);
        } catch (NoSuchElementException | ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                    (HttpServletResponse.SC_NOT_FOUND, e.getMessage()));
        }catch (ValidationException | NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                    (HttpServletResponse.SC_BAD_REQUEST, e.getMessage()));

        } catch (CurrencyAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                    (HttpServletResponse.SC_CONFLICT, e.getMessage()));

        } catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()));

        }
    }
}
