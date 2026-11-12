package com.chatbot.util;

import java.util.*;

/** Tiny JSON reader/writer for a list of flat objects (no external library needed). */
public class Json {

    public static String quote(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  b.append("\\\""); break;
                case '\\': b.append("\\\\"); break;
                case '\n': b.append("\\n");  break;
                case '\r': b.append("\\r");  break;
                case '\t': b.append("\\t");  break;
                default:
                    if (c < 0x20) b.append(String.format("\\u%04x", (int) c));
                    else b.append(c);
            }
        }
        return b.append('"').toString();
    }

    /** Parses  [ {"a":"x","b":1}, ... ]  into a list of maps (all values as text). */
    public static List<Map<String, String>> parse(String text) {
        P p = new P(text);
        List<Map<String, String>> list = new ArrayList<>();
        p.eat('[');
        if (p.next() == ']') return list;
        while (true) {
            list.add(p.obj());
            char c = p.next();
            p.i++;
            if (c == ']') break;
            if (c != ',') throw new IllegalArgumentException("Bad JSON at " + p.i);
        }
        return list;
    }

    private static class P {
        final String s;
        int i = 0;
        P(String s) { this.s = s; }

        char next() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
            return s.charAt(i);
        }

        void eat(char c) {
            if (next() != c) throw new IllegalArgumentException("Expected " + c + " at " + i);
            i++;
        }

        String str() {
            eat('"');
            StringBuilder b = new StringBuilder();
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') break;
                if (c == '\\') {
                    char e = s.charAt(i++);
                    switch (e) {
                        case 'n': b.append('\n'); break;
                        case 'r': b.append('\r'); break;
                        case 't': b.append('\t'); break;
                        case 'u': b.append((char) Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; break;
                        default:  b.append(e);
                    }
                } else {
                    b.append(c);
                }
            }
            return b.toString();
        }

        String value() {
            if (next() == '"') return str();
            int st = i;
            while (i < s.length() && ",}] \n\r\t".indexOf(s.charAt(i)) < 0) i++;
            return s.substring(st, i);
        }

        Map<String, String> obj() {
            Map<String, String> m = new LinkedHashMap<>();
            eat('{');
            if (next() == '}') { i++; return m; }
            while (true) {
                String k = str();
                eat(':');
                m.put(k, value());
                char c = next();
                i++;
                if (c == '}') break;
                if (c != ',') throw new IllegalArgumentException("Bad JSON at " + i);
            }
            return m;
        }
    }
}
