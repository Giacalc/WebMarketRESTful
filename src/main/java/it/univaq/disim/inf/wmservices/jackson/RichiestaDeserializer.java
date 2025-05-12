package it.univaq.disim.inf.wmservices.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.univaq.disim.inf.wmservices.model.Proposta;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import it.univaq.disim.inf.wmservices.model.Utente;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class RichiestaDeserializer extends JsonDeserializer<Richiesta> {
    @Override
    public Richiesta deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Richiesta richiesta = new Richiesta();
        
        JsonNode node = jp.getCodec().readTree(jp);
        
        if (node.has("ID")) {
            richiesta.setID(node.get("ID").asInt());
        }
        if (node.has("data")) {
            String dataStr = node.get("data").asText();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = sdf.parse(dataStr);
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                richiesta.setData(sqlDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        if (node.has("stato")) {
            richiesta.setStato(node.get("stato").asText());
        }
        if (node.has("note")) {
            richiesta.setNote(node.get("note").asText());
        }
        if (node.has("ordinante")) {
            JsonNode ordinanteNode = node.get("ordinante");
            Utente ordinante = jp.getCodec().treeToValue(ordinanteNode, Utente.class);
            richiesta.setOrd(ordinante);
        }
        if (node.has("tecnico_incarico")) {
            JsonNode tecnicoNode = node.get("tecnico_incarico");
            Utente tecnico = jp.getCodec().treeToValue(tecnicoNode, Utente.class);
            richiesta.setTec(tecnico);
        }
        if (node.has("categoria")) {
            richiesta.setIDcat(node.get("categoria").asText());
        }
        if (node.has("caratteristiche")) {
           Map<String, String> caratteristiche = new HashMap<>();
           JsonNode caratteristicheNode = node.get("caratteristiche");

           caratteristicheNode.fields().forEachRemaining(entry -> {
               String nome = entry.getKey();
               String valore = entry.getValue().asText();
               caratteristiche.put(nome, valore);
           });
           richiesta.setCaratteristiche(caratteristiche);
        }
   
        if (node.has("proposta")) {
            JsonNode propostaNode = node.get("proposta");
            Proposta p = jp.getCodec().treeToValue(propostaNode, Proposta.class);
            richiesta.setProposta(p);
        }
        
        return richiesta;
    }
}