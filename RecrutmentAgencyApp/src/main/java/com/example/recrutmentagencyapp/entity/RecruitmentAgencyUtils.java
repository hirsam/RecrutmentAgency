package com.example.recrutmentagencyapp.entity;

public class RecruitmentAgencyUtils {

    public static String getUsernameFromUserData(String userData) {
        StringBuilder result = new StringBuilder("");
        if (userData.isEmpty())
            return result.toString();
        var currentSymbol = userData.charAt(0);
        boolean parse = false;
        for (int i = 0; i < userData.length(); i++) {
            if (currentSymbol == ',') break;
            if (currentSymbol == '=') parse = true;
            result.append((parse && currentSymbol != '=') ? currentSymbol : "");
            currentSymbol = userData.charAt(i);
        }
        return result.toString();
    }
}
