package com.chatbot.util;

import com.chatbot.model.Faq;
import java.util.List;

/**
 * The "AI" brain: simple keyword-based intent matching.
 * Every FAQ has comma-separated keywords. The FAQ whose keywords
 * appear most often in the student's message wins.
 */
public class ChatBot {

    public static String reply(String message, List<Faq> faqs) {
        String m = message.toLowerCase().trim();

        // small talk
        if (m.split("\\s+").length <= 3
                && m.matches("^(hi|hii|hello|hey|good (morning|afternoon|evening))\\b.*")) {
            return "Hello! \uD83D\uDC4B I'm the College Enquiry Assistant. "
                 + "Ask me about admission, fees, courses, hostel or placements.";
        }
        if (m.matches(".*\\b(thanks|thank you|thx)\\b.*")) {
            return "You're welcome! \uD83D\uDE0A Ask me anything else about the college.";
        }
        if (m.matches(".*\\b(bye|goodbye)\\b.*")) {
            return "Goodbye! Have a great day \uD83C\uDF93";
        }

        // keyword scoring
        Faq best = null;
        int bestScore = 0;
        for (Faq f : faqs) {
            int score = 0;
            for (String k : f.getKeywords().split(",")) {
                k = k.trim().toLowerCase();
                if (!k.isEmpty() && m.contains(k)) {
                    score += k.contains(" ") ? 2 : 1;   // phrases count more
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = f;
            }
        }
        return best == null ? null : best.getAnswer();
    }
}
