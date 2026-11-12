package com.chatbot.servlet;

import com.chatbot.store.AdminStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Admin register (first-time setup only), login and logout. */
@WebServlet(urlPatterns = {"/register", "/login", "/logout"})
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final AdminStore store = new AdminStore();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        } else if ("/register".equals(path)) {
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if ("/register".equals(request.getServletPath())) register(request, response);
        else if ("/login".equals(request.getServletPath())) login(request, response);
    }

    private void fail(HttpServletRequest req, HttpServletResponse res, String page, String msg)
            throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.getRequestDispatcher(page).forward(req, res);
    }

    private void register(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (name == null || name.isBlank() || name.length() > 100
                || email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || password == null || password.length() < 8 || password.length() > 72) {
            fail(request, response, "/register.jsp",
                 "Enter a valid name, email and password (8-72 characters).");
            return;
        }
        try {
            if (store.exists()) {      // only the very first admin may register
                fail(request, response, "/register.jsp",
                     "An admin account already exists. Please log in.");
                return;
            }
            store.create(name.trim(), email.trim().toLowerCase(), password);
            response.sendRedirect(request.getContextPath() + "/login?registered=1");
        } catch (IOException e) {
            log("Registration failed", e);
            fail(request, response, "/register.jsp", "Unable to register. Please try again.");
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        try {
            String name = (email == null || password == null || password.length() > 72)
                    ? null : store.verify(email, password);
            if (name != null) {
                HttpSession old = request.getSession(false);
                if (old != null) old.invalidate();          // fresh session after login
                request.getSession(true).setAttribute("adminName", name);
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                fail(request, response, "/login.jsp", "Invalid email or password.");
            }
        } catch (IOException e) {
            log("Login failed", e);
            fail(request, response, "/login.jsp", "Unable to log in. Please try again.");
        }
    }
}
