package com.chatbot.store;

import com.chatbot.util.Json;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Single admin account in admin.json. Password is salted + hashed (PBKDF2), never plain text. */
public class AdminStore {

    private static final Object LOCK = new Object();
    private static final int ITERATIONS = 65000;

    public boolean exists() throws IOException {
        return Files.exists(DataDir.file("admin.json"));
    }

    public void create(String name, String email, String password) throws IOException {
        synchronized (LOCK) {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            String json = "[\n  {\"name\": " + Json.quote(name)
                    + ", \"email\": " + Json.quote(email)
                    + ", \"salt\": " + Json.quote(Base64.getEncoder().encodeToString(salt))
                    + ", \"hash\": " + Json.quote(hash(password, salt)) + "}\n]";
            DataDir.write(DataDir.file("admin.json"), json);
        }
    }

    /** Returns the admin's name if email + password are correct, otherwise null. */
    public String verify(String email, String password) throws IOException {
        synchronized (LOCK) {
            Path p = DataDir.file("admin.json");
            if (!Files.exists(p)) return null;
            try {
                Map<String, String> a = Json.parse(DataDir.read(p)).get(0);
                byte[] salt = Base64.getDecoder().decode(a.get("salt"));
                boolean ok = a.get("email").equalsIgnoreCase(email.trim())
                        && MessageDigest.isEqual(hash(password, salt).getBytes(), a.get("hash").getBytes());
                return ok ? a.get("name") : null;
            } catch (RuntimeException e) {
                throw new IOException("admin.json is not valid", e);
            }
        }
    }

    private String hash(String password, byte[] salt) throws IOException {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, 256);
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(h);
        } catch (Exception e) {
            throw new IOException("Hashing failed", e);
        }
    }
}
