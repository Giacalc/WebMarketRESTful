package it.univaq.disim.inf.wmservices.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.ws.rs.core.UriInfo;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class JWTHelpers {

    private static JWTHelpers instance = null;
    private SecretKey jwtKey = null;

    private JWTHelpers() {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("HmacSha256");
            jwtKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SecretKey getJwtKey() {
        return jwtKey;
    }

    public String validateToken(String token) {
        Jws<Claims> jwsc = Jwts.parserBuilder().setSigningKey(getJwtKey()).build().parseClaimsJws(token);
        return jwsc.getBody().getSubject();
    }

    public String issueToken(UriInfo context, String username) {
        Key key = getJwtKey();
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuer(context.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(15L).toInstant()))
                .signWith(key)
                .compact();
        return token;
    }

    public void revokeToken(String token) {
        
    }

    public static JWTHelpers getInstance() {
        if (instance == null) {
            instance = new JWTHelpers();
        }
        return instance;
    }
}
