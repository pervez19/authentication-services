package com.mycompany.authenticationservices.utils;

import lombok.experimental.UtilityClass;

import java.util.Date;

@UtilityClass
public class DateUtils {

    public static Long DateToLong(Date date){
        return date.getTime();
    }
}
