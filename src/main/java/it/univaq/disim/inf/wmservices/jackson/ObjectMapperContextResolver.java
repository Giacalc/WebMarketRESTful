package it.univaq.disim.inf.wmservices.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import it.univaq.disim.inf.wmservices.model.Proposta;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import it.univaq.disim.inf.wmservices.model.Utente;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public ObjectMapperContextResolver() {
        this.mapper = createObjectMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper m = new ObjectMapper();

        m.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule customSerializer = new SimpleModule("CustomSerializersModule");
        //Richiesta
        customSerializer.addSerializer(Richiesta.class, new RichiestaSerializer());
        customSerializer.addDeserializer(Richiesta.class, new RichiestaDeserializer());
        //Utente
        customSerializer.addSerializer(Utente.class, new UtenteSerializer());
        customSerializer.addDeserializer(Utente.class, new UtenteDeserializer());
        //Proposta
        customSerializer.addSerializer(Proposta.class, new PropostaSerializer());
        customSerializer.addDeserializer(Proposta.class, new PropostaDeserializer());

        m.registerModule(customSerializer);
     
        m.findAndRegisterModules();
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return m;
    }
}
