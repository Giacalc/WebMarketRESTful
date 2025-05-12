package it.univaq.disim.inf.wmservices.security;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import jakarta.ws.rs.core.UriInfo;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

@Path("auth")
public class AuthenticationRes {
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(
            @Context UriInfo uriinfo,
            @FormParam("username") String username,
            @FormParam("password") String password) {
        try {
            if (AuthHelpers.getInstance().authenticateUser(username, password)) {
                String authToken = AuthHelpers.getInstance().issueToken(uriinfo, username);
                return Response.ok(authToken)
                        .cookie(new NewCookie.Builder("token").value(authToken).build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken).build();
            }
        } catch (SQLException | NamingException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(AuthenticationRes.class.getName()).log(Level.SEVERE, null, ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante il login").build();
        }
        return Response.status(UNAUTHORIZED).build();
    }
    
    @DELETE
    @Path("logout")
    @Logged
    public Response logout(@Context ContainerRequestContext req) {
        String token = (String) req.getProperty("token");
        AuthHelpers.getInstance().revokeToken(token);
        return Response.noContent()
                .cookie(new NewCookie.Builder("token").value("").maxAge(0).build())
                .build();
    }

    @GET
    @Path("refresh")
    @Logged
    public Response refresh(@Context ContainerRequestContext req, @Context UriInfo uriinfo) {
        String username = (String) req.getProperty("user");
        String newtoken = AuthHelpers.getInstance().issueToken(uriinfo, username);
        return Response.ok(newtoken)
                .cookie(new NewCookie.Builder("token").value(newtoken).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newtoken).build();
    }
}
