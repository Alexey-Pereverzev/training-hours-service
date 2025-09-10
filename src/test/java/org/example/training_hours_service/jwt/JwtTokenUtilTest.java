package org.example.training_hours_service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class JwtTokenUtilTest {

    private static Path tempDir;
    private static RSAPublicKey publicKey;
    private static RSAPrivateKey privateKey;
    private JwtTokenUtil jwtTokenUtil;
    private Algorithm algorithm;

    @BeforeAll
    static void generateKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        tempDir = Files.createTempDirectory("jwt-keys");
        Path publicKeyFile = tempDir.resolve("public.key");
        Files.write(publicKeyFile, publicKey.getEncoded());
    }

    @BeforeEach
    void setUp() throws Exception {
        jwtTokenUtil = new JwtTokenUtil(tempDir.toString());
        algorithm = Algorithm.RSA256(publicKey, privateKey);
    }

    @AfterAll
    static void cleanup() throws Exception {
        if (tempDir != null) {
            try (var paths = Files.walk(tempDir)) {
                paths.map(Path::toFile)
                        .sorted((a, b) -> -a.compareTo(b))              // files first, the folders
                        .forEach(file -> {
                            if (!file.delete()) {
                                System.err.println("Failed to delete file: " + file.getAbsolutePath());
                            }
                        });
            }
        }
    }

    private String generateToken() {
        return JWT.create()
                .withSubject("Dina.Aliyeva")
                .withClaim("role", "ROLE_TRAINER")
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(60)))
                .sign(algorithm);
    }


    @Test
    void whenGenerateToken_validatedSuccessfully() {
        // given
        String token = generateToken();
        // when
        DecodedJWT decoded = jwtTokenUtil.validateAndParseToken(token);
        // then
        assertEquals("Dina.Aliyeva", decoded.getSubject());
        assertEquals("ROLE_TRAINER", decoded.getClaim("role").asString());
    }

    
    @Test
    void whenValidateAndParseToken_invalidToken_shouldThrowException() {
        // given
        String invalidToken = "invalid.token.value";
        // when + then
        assertThrows(JWTVerificationException.class, () -> jwtTokenUtil.validateAndParseToken(invalidToken));
    }


    @Test
    void whenPublicKeyFileMissing_shouldThrowException() {
        // given
        Path wrongDir = tempDir.resolve("nonexistent");
        // when + then
        assertThrows(Exception.class, () -> new JwtTokenUtil(wrongDir.toString()));
    }

    @Test
    void whenValidateAndParseToken_tokenExpired_shouldThrowJWTVerificationException() {
        // given
        String expiredToken = JWT.create()
                .withSubject("Expired.User")
                .withClaim("role", "ROLE_TRAINEE")
                .withExpiresAt(Date.from(Instant.now().minusSeconds(60)))
                .sign(algorithm);
        // when + then
        assertThrows(JWTVerificationException.class, () -> jwtTokenUtil.validateAndParseToken(expiredToken));
    }

    @Test
    void whenValidateAndParseToken_tokenWithoutRole_shouldReturnNullForRoleClaim() {
        // given
        String token = JWT.create()
                .withSubject("User.WithoutRole")
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(60)))
                .sign(algorithm);
        // when
        DecodedJWT decoded = jwtTokenUtil.validateAndParseToken(token);
        // then
        assertEquals("User.WithoutRole", decoded.getSubject());
        assertNull(decoded.getClaim("role").asString());
    }


}
