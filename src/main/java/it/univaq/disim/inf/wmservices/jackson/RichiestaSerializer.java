package it.univaq.disim.inf.wmservices.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

public class RichiestaSerializer extends JsonSerializer<Richiesta> {
    @Override
    public void serialize(Richiesta item, JsonGenerator jgen, SerializerProvider provider) 
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        
        jgen.writeNumberField("ID", item.getID());
        
        java.sql.Date sqlDate = (Date) item.getData();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
        String formattedDate = sdf.format(sqlDate);
        jgen.writeStringField("data", formattedDate); 
        
        jgen.writeStringField("stato", item.getStato());
        jgen.writeStringField("note", item.getNote());
        jgen.writeStringField("categoria", item.getIDcat());
        
        Map<String, String> caratteristiche = item.getCaratteristiche();
        if (caratteristiche != null && !caratteristiche.isEmpty()) {
            jgen.writeObjectFieldStart("caratteristiche");
            for (Map.Entry<String, String> entry : caratteristiche.entrySet()) {
                jgen.writeStringField(entry.getKey(), entry.getValue());
            }
            jgen.writeEndObject();
        }
        jgen.writeObjectField("ordinante", item.getOrd());
        
        if(item.getTec() != null) {
            jgen.writeObjectField("tecnico_incarico", item.getTec());
        }
        if(item.getProposta() != null) {
            jgen.writeObjectFieldStart("proposta");
            jgen.writeNumberField("ID", item.getProposta().getID());
            jgen.writeStringField("data", sdf.format(item.getProposta().getData()));
            jgen.writeStringField("stato", item.getProposta().getRevisione());
        
            if(item.getProposta().getRev_motivazione() != null) 
                jgen.writeStringField("motivazione", item.getProposta().getRev_motivazione());
            jgen.writeStringField("note", item.getProposta().getNote());
            jgen.writeObjectField("prodotto",item.getProposta().getProd());
            jgen.writeEndObject();
        }
        
        jgen.writeEndObject(); 
    }
}
