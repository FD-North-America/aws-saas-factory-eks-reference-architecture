package com.amazonaws.saas.eks.converter;

import com.amazonaws.saas.eks.util.Utils;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.util.Date;

public class DateConverter implements DynamoDBTypeConverter<String, Date> {
    @Override
    public String convert(Date obj) {
        return Utils.toISO8601UTC(obj);
    }

    @Override
    public Date unconvert(String objAsString) {
        return Utils.fromISO8601UTC(objAsString);
    }
}
