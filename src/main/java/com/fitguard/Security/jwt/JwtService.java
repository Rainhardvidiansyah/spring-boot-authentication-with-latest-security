package com.fitguard.Security.jwt;


import com.fitguard.entity.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final String JWTSECRETTOKEN = "gjagnbkgjbneoeoeorifb394802555prtoacvbnfmg";

        SignatureAlgorithm sa = SignatureAlgorithm.HS256;
        SecretKeySpec secretKeySpec = new SecretKeySpec(JWTSECRETTOKEN.getBytes(), sa.getJcaName());


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
            HashMap<String, Object> payload = new HashMap<>();
            //payload.put("Id", userDetails.getId());
            payload.put("email", userDetails.getUsername());
            payload.put("role", userDetails.getAuthorities());
        return generateToken(payload, userDetails); //generate, build, or sign token
    }

    //generate, build, or sign token
    public String generateToken(Map<String, Object> extractClaim, UserDetails userDetails){
        logger.info("User Details in jwt service class: {}", userDetails);
        return Jwts.builder() //generate, build, or sign token
                .claims(extractClaim)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *10))
                .signWith(secretKeySpec)
                .compact();
    }


    //jwt parser
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKeySpec)
                .build()
                .parseSignedClaims(token)
                .getBody();
    }


    public String extractUserName(String tokenJwt) {
        if(tokenJwt.isEmpty()){
            throw new RuntimeException("TOKEN TIDAK ADA");
        }
        logger.info("Jwt token form extract user name method in jwt service class: {}", tokenJwt);
        return extractClaim(tokenJwt, Claims::getSubject);
    }

    public String getUserNameFromJwtToken(String tokenJwt){
        return extractClaim(tokenJwt, claims -> claims.getSubject());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userName = extractUserName(userDetails.getUsername());
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public Long getUserId(String token){
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

}
