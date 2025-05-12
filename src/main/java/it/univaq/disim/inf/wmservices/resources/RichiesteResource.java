package it.univaq.disim.inf.wmservices.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import it.univaq.disim.inf.wmservices.business.RichiesteService;
import it.univaq.disim.inf.wmservices.business.RichiesteServiceFactory;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import it.univaq.disim.inf.wmservices.security.Logged;
import jakarta.ws.rs.core.MediaType;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//path .../rest/richieste
@Path("/richieste")
public class RichiesteResource {

    //istanziamento logica di business
    private final RichiesteService business;
    public RichiesteResource() {
        this.business = RichiesteServiceFactory.getRichiesteService();
    }
    
    @GET
    @Logged
    @Produces({"application/json"})
    public Response getRichieste(
            @QueryParam("ID_Ordinante") String username, 
            @Context UriInfo uriinfo,
            @Context SecurityContext secCon,
            @Context ContainerRequestContext reqCon) {
        
        if(username == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Specificare l'utente durante l'esecuzione della richiesta")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        List<Richiesta> richieste = new ArrayList<>();     
        try {
            richieste = business.getRichieste(username);
            if (richieste.isEmpty()) {         
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Non è presente alcuna richiesta per l'utente '" + username +"'")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(RichiesteResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante il recupero delle richieste")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        return Response.ok(richieste).build();
    }
    
    @Path("/{id: [0-9]+}")
    public RichiestaResource getRichiesta(
            @PathParam("id") int id,
            @Context UriInfo uriinfo,
            @Context SecurityContext secCon,
            @Context ContainerRequestContext reqCon) {
        try {
            Richiesta r = business.getRichiesta(id);
            return new RichiestaResource(r);
        } catch (SQLException ex) {
            Logger.getLogger(RichiesteResource.class.getName()).log(Level.SEVERE, null, ex);
            return new RichiestaResource(null);
        }
    }
    
    @GET
    @Logged
    @Path("/non_in_carico")
    @Produces({"application/json"})
    public Response getRichiesteDaPrendereInCarico() {
        List<Richiesta> richieste = new ArrayList<>();

        try {
            richieste = business.getRichiesteDaPrendereInCarico();
            if (richieste.isEmpty()) {         
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Non è presente alcuna richiesta da prendere in carico")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(RichiesteResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante il recupero delle richieste")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        return Response.ok(richieste).build();
    }
    
    @GET
    @Logged
    @Path("/in_corso")
    @Produces({"application/json"})
    public Response getRichiesteInCorso(
            @QueryParam("ID_Ordinante") String ordID,
            @Context SecurityContext secCon) {
        if(ordID == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Specificare l'ordinante durante l'esecuzione della richiesta")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        List<Richiesta> richieste = new ArrayList<>();
        try {
            richieste = business.getRichiesteInCorso(ordID);
            if (richieste.isEmpty()) {         
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Non è presente alcuna richiesta in corso per l'ordinante '" + ordID +"'")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(RichiesteResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante il recupero delle richieste in corso")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        return Response.ok(richieste).build();
    }
    
    @GET
    @Logged
    @Path("/in_carico")
    @Produces({"application/json"})
    public Response getRichiesteInCarico(
            @QueryParam("ID_Tecnico") String idTec) { 
        if(idTec == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Specificare il tecnico durante l'esecuzione della richiesta")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        List<Richiesta> richieste = new ArrayList<>();
        try {
            richieste = business.getRichiesteInCarico(idTec);
            if (richieste.isEmpty()) {         
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Non è presente alcuna richiesta in carico per il tecnico '" + idTec +"'")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (Exception ex) {
            Logger.getLogger(RichiesteResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante il recupero delle richieste in carico")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        return Response.ok(richieste).build();
    }
    
    @POST
    @Logged
    @Consumes({"application/json"})
    public Response insertRichiesta(
            @Context ContainerRequestContext req,
            @Context UriInfo uriinfo,
            @Context SecurityContext sec,
            Richiesta richiesta) {
        try {
            int richiestaId = business.insertRichiesta(richiesta);
            if (richiestaId > 0){
                URI uri = uriinfo.getAbsolutePathBuilder().path(String.valueOf(richiestaId)).build();
                return Response.created(uri).build();
            } else {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Inserimento della richiesta non andato a buon fine")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (SQLException e) {
            Logger.getLogger(RichiesteResource.class.getName()).log(Level.SEVERE, null, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante l'inserimento della richiesta")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}