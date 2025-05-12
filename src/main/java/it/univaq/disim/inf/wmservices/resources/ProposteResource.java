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
import it.univaq.disim.inf.wmservices.business.ProposteService;
import it.univaq.disim.inf.wmservices.business.ProposteServiceFactory;
import it.univaq.disim.inf.wmservices.model.Proposta;
import it.univaq.disim.inf.wmservices.security.Logged;
import it.univaq.disim.inf.wmservices.security.exceptions.RESTWebApplicationException;
import jakarta.ws.rs.core.MediaType;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

//path .../rest/proposte
@Path("/proposte")
public class ProposteResource {

    //istanziamento logica di business
    private final ProposteService business;
    public ProposteResource() {
        this.business = ProposteServiceFactory.getProposteService();
    }
    
    @GET
    @Logged
    @Produces({"application/json"})
    public Response getProposte(
            @QueryParam("ID_Ordinante") String user, 
            @Context UriInfo uriinfo,
            @Context SecurityContext secCon,
            @Context ContainerRequestContext reqCon)
            throws RESTWebApplicationException, SQLException,
                   ClassNotFoundException, NamingException {
        
        if(user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Specificare l'utente durante l'esecuzione della richiesta")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        List<Proposta> proposte = new ArrayList<>();
        try {
            proposte = business.getProposte(user);
            if (proposte.isEmpty()) {         
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Non è presente alcuna proposta associata all'utente '" + user +"'")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProposteResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante il recupero delle proposte")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        return Response.ok(proposte).build();
    }
    
    @Path("/{id: [0-9]+}")
    public PropostaResource getProposta(
            @PathParam("id") int id,
            @Context UriInfo uriinfo,
            @Context SecurityContext secCon,
            @Context ContainerRequestContext reqCon) {
        try {
            Proposta p = business.getProposta(id);
            return new PropostaResource(p);
        } catch (SQLException ex) {
            Logger.getLogger(ProposteResource.class.getName()).log(Level.SEVERE, null, ex);
            return new PropostaResource(null);
        }
    }
    
    @POST
    @Logged
    @Consumes({"application/json"})
    public Response insertProposta(
            @Context ContainerRequestContext req,
            @Context UriInfo uriinfo,
            @Context SecurityContext sec,
            Proposta proposta) {
        try {
            //controllo se il tecnico connesso può inserire la proposta
            String user = sec.getUserPrincipal().getName();
            
            int propostaID = business.insertProposta(proposta, user);
            if (propostaID > 0){
                URI uri = uriinfo.getAbsolutePathBuilder().path(String.valueOf(propostaID)).build();
                return Response.created(uri).build();
            } else if (propostaID == -1) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Il tecnico in sessione non corrisponde al tecnico incaricato della richiesta")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            } else {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Inserimento della proposta non andato a buon fine")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (SQLException e) {
            Logger.getLogger(ProposteResource.class.getName()).log(Level.SEVERE, null, e);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante l'inserimento della proposta")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }   
    }
}
