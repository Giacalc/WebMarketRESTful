package it.univaq.disim.inf.wmservices.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import it.univaq.disim.inf.wmservices.business.ProposteService;
import it.univaq.disim.inf.wmservices.business.ProposteServiceFactory;
import it.univaq.disim.inf.wmservices.model.Proposta;
import it.univaq.disim.inf.wmservices.security.Logged;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

//sottorisorsa
public class PropostaResource {

    private final ProposteService business;
    private final Proposta proposta;

    public PropostaResource(Proposta p) {
        this.business = ProposteServiceFactory.getProposteService();
        this.proposta = p;
    }

    @GET
    @Logged
    @Produces("application/json")
    public Response getproposta() {
        if(proposta == null){
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("La proposta è inesistente")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        return Response.ok(proposta).build();
    }
    
    @PATCH
    @Logged
    @Path("/accetta")
    @Produces("application/json")
    public Response accettaProposta(@Context SecurityContext secCon) {
        try {
            //controllo se l'ordinante connesso può accettare la sua proposta
            String user = secCon.getUserPrincipal().getName();
            
            int accetta = business.accettaProposta(proposta.getID(), user);
        
            if (accetta > 0) {
                return Response.noContent().build();
            } else if (accetta == -1) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("L'utente in sessione non corrisponde al destinatario della proposta")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Accettazione proposta non andato a buon fine.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(PropostaResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante l'accettazione della proposta.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } 
    }
    
    @PATCH
    @Logged
    @Path("/rifiuta")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response rifiutaProposta(
            @FormParam("motivazione") String rif,
            @Context SecurityContext secCon) {
        try {
            //controllo se l'ordinante connesso può accettare la sua proposta
            String user = secCon.getUserPrincipal().getName();
            
            int rifiuta = business.rifiutaProposta(proposta.getID(), user, rif);
        
            if (rifiuta > 0) {
                return Response.noContent().build();
            } else if (rifiuta == -1) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("L'utente in sessione non corrisponde al destinatario della proposta")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Rifiuto proposta non andato a buon fine.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(PropostaResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante il rifiuto della proposta.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } 
    }
    
    
    @PUT
    @Logged
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response updateProposta(
            Proposta proposta_mod,         
            @Context SecurityContext sec) {       
        try {   
            //controllo se il tecnico connesso può modificare la sua proposta
            String user = sec.getUserPrincipal().getName();
            
            int mod = business.updateProposta(proposta_mod, proposta.getID(), user);
            if (mod > 0) {
                return Response.noContent().build();
            } else if (mod == -1) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Il tecnico in sessione non corrisponde al mittente della proposta")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            } else {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Modifica proposta non andata a buon fine.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            } 
        } catch (SQLException e) {
            Logger.getLogger(PropostaResource.class.getName()).log(Level.SEVERE, null, e);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante la modifica della proposta")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } 
    }
}
