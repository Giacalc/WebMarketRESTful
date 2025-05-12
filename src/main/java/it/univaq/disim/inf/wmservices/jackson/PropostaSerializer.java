package it.univaq.disim.inf.wmservices.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.univaq.disim.inf.wmservices.model.Proposta;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class PropostaSerializer extends JsonSerializer<Proposta> {
    @Override
    public void serialize(Proposta item, JsonGenerator jgen, SerializerProvider provider) 
            throws IOException, JsonProcessingException {
    
        jgen.writeStartObject();

        jgen.writeNumberField("ID", item.getID());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        jgen.writeStringField("data", sdf.format(item.getData()));

        jgen.writeStringField("stato", item.getRevisione());

        if(item.getRev_motivazione() != null) 
            jgen.writeStringField("motivazione", item.getRev_motivazione());

        jgen.writeStringField("note", item.getNote());
        jgen.writeObjectField("prodotto",item.getProd());
        jgen.writeObjectField("tecnico", item.getTec());

        jgen.writeObjectFieldStart("richiesta");
            jgen.writeNumberField("ID", item.getRic().getID());
            jgen.writeStringField("data", sdf.format(item.getRic().getData()));
            jgen.writeStringField("stato", item.getRic().getStato());
            jgen.writeStringField("note", item.getRic().getNote());
            jgen.writeStringField("categoria", item.getRic().getIDcat());

            Map<String, String> caratteristiche = item.getRic().getCaratteristiche();
            if (caratteristiche != null && !caratteristiche.isEmpty()) {
                jgen.writeObjectFieldStart("caratteristiche");
                for (Map.Entry<String, String> entry : caratteristiche.entrySet()) {
                    jgen.writeStringField(entry.getKey(), entry.getValue());
                }
                jgen.writeEndObject();
            }
            jgen.writeObjectField("ordinante", item.getRic().getOrd());
        jgen.writeEndObject();

        jgen.writeEndObject();
    }
}