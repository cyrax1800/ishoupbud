package com.project.ishoupbud.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by michael on 3/29/17.
 */

public class ValidationUtils {
    public static String TAG = "Validation Utils";

    public static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern PHONE_REGEX =
            Pattern.compile("(^\\+\\d{11,13}$)|(^\\d{10,12}$)", Pattern.CASE_INSENSITIVE);

    public static final int minPasswordLength = 4;

    public static boolean isEmailValid(String email){
        if((email == null) || email.isEmpty()) return false;
        Matcher matcher = EMAIL_REGEX.matcher(email);
        return matcher.find();
    }

    public static boolean isPasswordValid(String password){
        if((password == null) || password.isEmpty()) return false;
        return (password.length() >= minPasswordLength);
    }

    public static boolean isPasswordValid(String password, String confirmPassword){
        if((password == null) || (confirmPassword == null)) return false;
        if(password.isEmpty() || confirmPassword.isEmpty()) return false;
        if(!password.equals(confirmPassword)) return false;
        if(password.length() >= minPasswordLength) return false;
        if(confirmPassword.length() >= minPasswordLength) return false;
        return true;
    }

    public static boolean isPhoneNumberValid(String phoneNumber){
        if((phoneNumber == null) || phoneNumber.isEmpty()) return false;
        Matcher matcher = PHONE_REGEX.matcher(phoneNumber);

        return matcher.find();
    }
}
