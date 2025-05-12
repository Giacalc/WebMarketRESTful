package it.univaq.disim.inf.wmservices.security;

import jakarta.ws.rs.core.UriInfo;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class AuthHelpers {
    
    private static AuthHelpers instance = null;
    private final JWTHelpers jwt;
    
    public AuthHelpers() {
        jwt = JWTHelpers.getInstance();
    }
    
    public boolean authenticateUser(String username, String password) throws NamingException, NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        DataSource dataSource = (DataSource) envContext.lookup("jdbc/wmservices");
        
        Connection conn = dataSource.getConnection();
        String query = "SELECT password FROM utente WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String hash = rs.getString("password");
            return SecurityHelpers.checkPasswordHashPBKDF2(password, hash);
        }                    
        return false;
    }
    
    public String issueToken(UriInfo context, String username) {
        return jwt.issueToken(context, username);
    }
    
    public void revokeToken(String token) {
        jwt.revokeToken(token);
    }
    
    public String validateToken(String token) {
        return jwt.validateToken(token);
    }
    
    public static AuthHelpers getInstance() {
        if (instance == null) {
            instance = new AuthHelpers();
        }
        return instance;
    } 
}
