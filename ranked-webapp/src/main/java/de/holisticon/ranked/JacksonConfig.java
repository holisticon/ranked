package de.holisticon.ranked;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.WILDCARD)
@Consumes(MediaType.WILDCARD)
public class JacksonConfig implements ContextResolver<ObjectMapper> {
    private static final Logger LOG = LoggerFactory.getLogger(JacksonConfig.class);

    private final ObjectMapper objectMapper;

    public JacksonConfig()      {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DefaultScalaModule());
        objectMapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    public ObjectMapper getContext(Class<?> objectType) {
        return objectMapper;
    }
}