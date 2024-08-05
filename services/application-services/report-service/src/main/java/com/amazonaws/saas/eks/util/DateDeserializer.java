package com.amazonaws.saas.eks.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateDeserializer extends JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utils.DATE_FORMAT_PATTERN);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(jsonParser.getText(), formatter);
        return zonedDateTime.withZoneSameInstant(ZoneId.of(Utils.UTC_TIMEZONE));
    }
}
