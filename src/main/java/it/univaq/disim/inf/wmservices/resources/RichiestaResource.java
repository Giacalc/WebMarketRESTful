package it.univaq.disim.inf.wmservices.resources;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import it.univaq.disim.inf.wmservices.business.RichiesteService;
import it.univaq.disim.inf.wmservices.business.RichiesteServiceFactory;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import it.univaq.disim.inf.wmservices.security.Logged;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

//sottorisorsa
public class RichiestaResource {

    private final RichiesteService business;
    private final Richiesta richiesta;

    public RichiestaResource(Richiesta r) {
        this.business = RichiesteServiceFactory.getRichiesteService();
        this.richiesta = r;
    }

    @GET
    @Logged
    @Produces("application/json")
    public Response getRichiesta() {
        if(richiesta == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("La richiesta è inesistente")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        return Response.ok(richiesta).build();
    }
    
    @PATCH
    @Logged
    @Path("/incarico_richiesta") 
    @Produces("application/json")
    public Response inCaricoRichiesta(
            @QueryParam("ID_Tecnico") String tec,
            @Context SecurityContext secCon) {
        if(tec == null){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Specificare il tecnico")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        try {
            int update = business.inCaricoRichiesta(tec, richiesta);
            if(update > 0){
                return Response.noContent().build();
            } else {
                return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Presa in carico della richiesta non andata a buon fine.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }   
        } catch (Exception ex) {
            Logger.getLogger(RichiestaResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante la presa in carico della richiesta.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    @DELETE
    @Logged
    @Produces("application/json")
    public Response deleteRichiesta(@Context SecurityContext secCon) {
        try {
            int delete = business.deleteRichiesta(richiesta.getID());
            if(delete > 0) {
                return Response.noContent().build();
            } else {
                return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Cancellazione della richiesta non andata a buon fine.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }   
        } catch (SQLException e) {
            Logger.getLogger(RichiestaResource.class.getName()).log(Level.SEVERE, null, e);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante la cancellazione della richiesta.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
