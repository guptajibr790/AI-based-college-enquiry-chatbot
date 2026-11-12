package com.chatbot.servlet;

import com.chatbot.model.Faq;
import com.chatbot.store.FaqStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Admin panel: CRUD on FAQs (session protected). */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final FaqStore store = new FaqStore();

    private boolean loggedIn(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("adminName") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!loggedIn(req, res)) return;
        show(req, res, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!loggedIn(req, res)) return;
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String ctx = req.getContextPath();

        try {
            if ("delete".equals(action)) {
                String id = req.getParameter("id");
                if (id != null && id.matches("\\d{1,9}")) store.delete(Integer.parseInt(id));
                res.sendRedirect(ctx + "/admin?deleted=1");
                return;
            }
            if ("save".equals(action)) {
                String idText = req.getParameter("id");
                int id = (idText != null && idText.matches("\\d{1,9}")) ? Integer.parseInt(idText) : 0;
                String q = trim(req.getParameter("question"));
                String k = trim(req.getParameter("keywords"));
                String a = trim(req.getParameter("answer"));
                Faq f = new Faq(id, q, k, a);

                // server-side validation
                if (q.length() < 5 || q.length() > 200) {
                    show(req, res, "Question must be 5-200 characters.", f); return;
                }
                if (k.length() < 2 || k.length() > 255) {
                    show(req, res, "Enter 1 or more keywords separated by commas.", f); return;
                }
                if (a.length() < 5 || a.length() > 1000) {
                    show(req, res, "Answer must be 5-1000 characters.", f); return;
                }
                if (id > 0) store.update(f); else store.insert(f);
                res.sendRedirect(ctx + "/admin?saved=1");
                return;
            }
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            log("Admin action failed", e);
            show(req, res, "Could not read or write the JSON file.", null);
        }
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }

    private void show(HttpServletRequest req, HttpServletResponse res, String error, Faq edit)
            throws ServletException, IOException {
        try {
            req.setAttribute("faqs", store.getAll());
            req.setAttribute("unanswered", store.unanswered(8));
            String eid = req.getParameter("edit");
            if (edit == null && eid != null && eid.matches("\\d{1,9}")) {
                edit = store.get(Integer.parseInt(eid));
            }
        } catch (IOException e) {
            log("Unable to load admin data", e);
            error = "Unable to load data from the JSON files.";
        }
        req.setAttribute("error", error);
        req.setAttribute("editFaq", edit);
        req.getRequestDispatcher("/admin.jsp").forward(req, res);
    }
}
