package com.amazonaws.saas.eks.model;

import java.util.HashMap;
import java.util.Map;

public enum UserAttribute {
    UNDEFINED("undefined"),
    USER_ID("sub"),
    EMAIL("email"),
    EMAIL_VERIFIED("email_verified"),
    FIRST_NAME("given_name"),
    LAST_NAME("family_name"),
    ADDRESS("address"),
    CITY("custom:city"),
    STATE("custom:state"),
    COUNTRY("custom:country"),
    ZIP("custom:zip"),
    PHONE_NUMBER("phone_number"),
    PHONE_NUMBER_VERIFIED("phone_number_verified"),
    HOME_PHONE_NUMBER("custom:home_phone"),
    BIRTHDAY("birthdate");

    public final String label;

    private UserAttribute(String label) {
        this.label = label;
    }

    private static final Map<String, UserAttribute> BY_LABEL = new HashMap<>();
    
    static {
        for (UserAttribute e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    public static UserAttribute valueOfLabel(String label) {
        return BY_LABEL.containsKey(label) ? BY_LABEL.get(label) : UserAttribute.UNDEFINED;
    }
}
