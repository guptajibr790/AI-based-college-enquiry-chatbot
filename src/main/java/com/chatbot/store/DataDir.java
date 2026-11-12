package com.chatbot.store;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/** JSON files live in  <your user folder>/chatbot-data/  (survives redeploys). */
class DataDir {
    static Path file(String name) throws IOException {
        Path dir = Paths.get(System.getProperty("user.home"), "chatbot-data");
        Files.createDirectories(dir);
        return dir.resolve(name);
    }

    static String read(Path p) throws IOException {
        return Files.readString(p, StandardCharsets.UTF_8);
    }

    static void write(Path p, String text) throws IOException {
        Files.writeString(p, text, StandardCharsets.UTF_8);
    }
}
