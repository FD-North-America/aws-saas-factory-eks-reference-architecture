package com.amazonaws.saas.eks.config;

import com.amazonaws.saas.eks.util.DateDeserializer;
import com.amazonaws.saas.eks.util.DateSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.ZonedDateTime;
import java.util.Date;

@Configuration
public class AppConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, new DateSerializer());
        module.addDeserializer(ZonedDateTime.class, new DateDeserializer());
        return new ObjectMapper().registerModule(module).registerModule(new JavaTimeModule());
    }
}
