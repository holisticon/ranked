package de.holisticon.ranked;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes(MediaType.WILDCARD) // NOTE: required to support "non-standard" JSON variants
@Produces(MediaType.WILDCARD)
public class JacksonConfig implements ContextResolver<ObjectMapper> {
    private static final Logger LOG = LoggerFactory.getLogger(JacksonConfig.class);

    private final ObjectMapper objectMapper;

    public JacksonConfig()      {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DefaultScalaModule());
        objectMapper.registerModule(new Hibernate4Module());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRAP_EXCEPTIONS, true);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

    }

    @Override
    public ObjectMapper getContext(Class<?> objectType) {
        return objectMapper;
    }
}