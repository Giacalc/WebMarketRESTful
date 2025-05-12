package it.univaq.disim.inf.wmservices.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.univaq.disim.inf.wmservices.model.Utente;
import java.io.IOException;

public class UtenteDeserializer extends JsonDeserializer<Utente> {

    @Override
    public Utente deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Utente utente = new Utente(); 
        
        JsonNode node = jp.getCodec().readTree(jp); 
                
        if (node.has("username")) {
            utente.setUsername(node.get("username").asText());
        } 
        if (node.has("email")) {
            utente.setEmail(node.get("email").asText());
        }
        if (node.has("nome")) {
            utente.setNome(node.get("nome").asText());
        }
        if (node.has("cognome")) {
            utente.setCognome(node.get("cognome").asText());
        } 
        if (node.has("ruolo")) {
            utente.setRuolo(node.get("ruolo").asText());
        }
        
        return utente;
    }
}