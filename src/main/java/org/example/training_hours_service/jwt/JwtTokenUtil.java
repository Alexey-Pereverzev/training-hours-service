package org.example.training_hours_service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;


@Service
public class JwtTokenUtil {

    private final JWTVerifier verifier;

    public JwtTokenUtil(@Value("${jwt.keys-path:secret}") String keysDir) throws Exception {
        Path keysPath = Paths.get(keysDir);
        PublicKey publicKey = loadPublicKey(keysPath);
        this.verifier = JWT.require(Algorithm.RSA256((RSAPublicKey) publicKey, null))
                .build();
    }

    private PublicKey loadPublicKey(Path keysPath) throws Exception {
        byte[] publicKeyBytes = Files.readAllBytes(keysPath.resolve("public.key"));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    public DecodedJWT validateAndParseToken(String token) {
        return verifier.verify(token);
    }

    public String getUsername(String token) {
        return validateAndParseToken(token).getSubject();
    }

    public String getRole(String token) {
        return validateAndParseToken(token).getClaim("role").asString();
    }
}
