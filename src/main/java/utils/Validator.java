package utils;

import exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

public class Validator {
    public static String getRequiredParameter(HttpServletRequest req, String parameter){
        String result = req.getParameter(parameter);
        if(result == null || result.isEmpty()){
            throw new ValidationException("Form field " + parameter + " missing !");
        }
        return result;
    }
    public static String getRequiredPathSegment(HttpServletRequest req){
        String path = req.getPathInfo();
        if(path == null || path.isEmpty()){
            throw new ValidationException("Path segment is missing");
        }
        String result = path.substring(1);
        if(result.isEmpty()){
            throw new ValidationException("Path segment is empty");
        }
        return result;
    }
}
