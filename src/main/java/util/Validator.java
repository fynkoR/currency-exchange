package util;

import exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.math.BigDecimal;

public class Validator {
    public static String getRequiredParameter(HttpServletRequest req, String parameter){
        String result = req.getParameter(parameter);
        if(result == null || result.isBlank()){
            throw new ValidationException("Form field " + parameter + " missing !");
        }
        if(parameter.equals("sign") && result.length() > 3){
            throw new ValidationException("Sign must be as follows: AAA (3 letters) !");
        }
        return result;
    }

    public static String getRequiredParameterForPatch(HttpServletRequest req, String parameter) throws IOException {
        String rateString = req.getReader().readLine();
        if(rateString == null || !rateString.contains(parameter + "=")){
            throw new ValidationException("Form field '" + parameter + "' missing !");
        }
        return rateString.replace(parameter + "=", "");
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
    public static String[] getRequiredPathTwoSegment(HttpServletRequest req){
        String path = req.getPathInfo();
        if(path == null || path.isEmpty()){
            throw new ValidationException("Path segment is missing");
        }
        String pathWithoutSlash = path.substring(1);
        if(pathWithoutSlash.length() != 6){
            throw new ValidationException("The currency code format must be as follows: AAABBB");
        }
        if(!isOnlyLetters(pathWithoutSlash)){
            throw new ValidationException("The currency code must be letters");
        }
        String firstCode = path.substring(1, 4);
        String secondCode = path.substring(4, 7);

        return new String[]{firstCode, secondCode};
    }

    public static boolean isOnlyLetters(String path){
        for(int i = 0; i < path.length(); i++){
            if(!Character.isLetter(path.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public static BigDecimal getPositiveDecimal(String rateString, String fieldName){
        BigDecimal rate = new BigDecimal(rateString);
        if(rate.compareTo(BigDecimal.ZERO) <= 0){
            throw new ValidationException(fieldName + " is negative or zero");
        }
        return rate;
    }

    public static void validateCurrenciesNotEqual(String baseCurrency, String targetCurrency){
        if(baseCurrency.equals(targetCurrency)){
            throw new ValidationException("Base currency and target currency equals");
        }
    }
}
