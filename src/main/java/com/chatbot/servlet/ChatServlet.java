package com.chatbot.servlet;

import com.chatbot.store.FaqStore;
import com.chatbot.util.ChatBot;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Receives the student's message (POST) and returns the bot reply as plain text. */
@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final FaqStore store = new FaqStore();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        String message = request.getParameter("message");
        if (message == null || message.isBlank() || message.length() > 300) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Please type a message (maximum 300 characters).");
            return;
        }
        message = message.trim();

        String reply;
        try {
            reply = ChatBot.reply(message, store.getAll());
            if (reply == null) {
                reply = "Sorry, I couldn't find an answer to that \uD83D\uDE15 "
                      + "Try asking about admission, fees, courses, hostel or placements. "
                      + "Our staff will also review your question.";
                store.logUnanswered(message);
            }
        } catch (IOException e) {
            log("Chat failed", e);
            reply = "Sorry, something went wrong on our side. Please try again later.";
        }
        response.getWriter().print(reply);
    }
}
