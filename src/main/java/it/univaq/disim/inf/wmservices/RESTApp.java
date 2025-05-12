package it.univaq.disim.inf.wmservices;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import it.univaq.disim.inf.wmservices.jackson.ObjectMapperContextResolver;
import it.univaq.disim.inf.wmservices.resources.ProposteResource;
import it.univaq.disim.inf.wmservices.resources.RichiesteResource;
import it.univaq.disim.inf.wmservices.security.AuthLoggedFilter;
import it.univaq.disim.inf.wmservices.security.AuthenticationRes;
import it.univaq.disim.inf.wmservices.security.CORSFilter;

@ApplicationPath("rest")
public class RESTApp extends Application {

    private final Set<Class<?>> classes;

    public RESTApp() {
        HashSet<Class<?>> c = new HashSet<>();
        //root resources
        c.add(AuthenticationRes.class);
        c.add(RichiesteResource.class);
        c.add(ProposteResource.class);


        c.add(JacksonJsonProvider.class);   
        c.add(ObjectMapperContextResolver.class);
        c.add(AuthLoggedFilter.class);
        c.add(CORSFilter.class);

        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
