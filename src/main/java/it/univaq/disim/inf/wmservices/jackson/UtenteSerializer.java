package it.univaq.disim.inf.wmservices.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.univaq.disim.inf.wmservices.model.Utente;
import java.io.IOException;

public class UtenteSerializer extends JsonSerializer<Utente> {

    @Override
    public void serialize(Utente utente, JsonGenerator jgen, SerializerProvider provider) 
            throws IOException, JsonProcessingException {
        jgen.writeStartObject(); 
        
        jgen.writeStringField("username", utente.getUsername());
        jgen.writeStringField("email", utente.getEmail());
        jgen.writeStringField("nome", utente.getNome());
        jgen.writeStringField("cognome", utente.getCognome());
        jgen.writeStringField("ruolo", utente.getRuolo());

        jgen.writeEndObject(); 
    }
}
