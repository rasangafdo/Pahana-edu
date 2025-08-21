package com.pahanaedu.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtValidator {
    private static final String SECRET_KEY = "pahanaEdu";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 *24;

    public static String generateToken(String username,String role) {
        return JWT.create()
                .withSubject(username) 
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }
    

    // Validate token
    public static DecodedJWT validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
        return verifier.verify(token);
    }

    // Extract username
    public static String getUsername(String token) {
        return validateToken(token).getSubject();
    }

    // Extract role
    public static String getRole(String token) {
        return validateToken(token).getClaim("role").asString();
    }
}