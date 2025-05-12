package it.univaq.disim.inf.wmservices.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import it.univaq.disim.inf.wmservices.model.Prodotto;
import it.univaq.disim.inf.wmservices.model.Proposta;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import it.univaq.disim.inf.wmservices.model.Utente;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class PropostaDeserializer extends JsonDeserializer<Proposta>{
    
    @Override
    public Proposta deserialize (JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Proposta proposta = new Proposta();
        
        JsonNode node = jp.getCodec().readTree(jp);
        
        if (node.has("ID")) {
            proposta.setID(node.get("ID").asInt());
        }
        
        if (node.has("data")) {
            String dataStr = node.get("data").asText();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = sdf.parse(dataStr);  

                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

                proposta.setData(sqlDate);
            } catch (ParseException e) {
                //errore
            }
        }
       
        if (node.has("stato")) {
            proposta.setRevisione(node.get("stato").asText());
            } 
        
        if (node.has("richiesta")) {
           JsonNode ne = node.get("richiesta");
           Richiesta richiesta = jp.getCodec().treeToValue(ne, Richiesta.class);
           proposta.setRic(richiesta);
        } 

        if (node.has("tecnico")) {
           JsonNode ne = node.get("tecnico");
           Utente tec = jp.getCodec().treeToValue(ne, Utente.class);
           proposta.setTec(tec);
        } 

        if (node.has("prodotto")) {
           JsonNode ne = node.get("prodotto");
           Prodotto prod = jp.getCodec().treeToValue(ne, Prodotto.class);
           proposta.setProd(prod);
        } 
        
        if (node.has("motivazione")) {
            proposta.setRev_motivazione(node.get("motivazione").asText());
        }
        
        if (node.has("note")) {
            proposta.setNote(node.get("note").asText());
        }
        
        return proposta;     
        
    }
}
