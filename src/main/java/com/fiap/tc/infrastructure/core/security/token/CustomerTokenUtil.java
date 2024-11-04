package com.fiap.tc.infrastructure.core.security.token;

import com.fiap.tc.domain.entities.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static java.lang.String.format;

@Component
public class CustomerTokenUtil {

    private final String secretKey = "e0b41325-42e5-4b3a-b995-b05c4e77c56d";

    public String generateToken(Customer customer) {
        return Jwts.builder()
                .setSubject(customer.getDocument())
                .claim("id", customer.getId())
                .claim("name", customer.getName())
                .claim("email", customer.getEmail())
                .claim("document", customer.getDocument())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new UnauthorizedUserException(format("Invalid JWT Token: %s", token));
        }
    }
}