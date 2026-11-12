<%@ page contentType="text/html;charset=UTF-8" import="java.util.List,com.chatbot.model.Faq,com.chatbot.util.Html" %>
<%
  String ctx = request.getContextPath();
  List<Faq> faqs = (List<Faq>) request.getAttribute("faqs");
  List<String> unanswered = (List<String>) request.getAttribute("unanswered");
  Faq edit = (Faq) request.getAttribute("editFaq");
  String error = (String) request.getAttribute("error");
  String adminName = (String) session.getAttribute("adminName");
  boolean editing = edit != null && edit.getId() > 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin Panel</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body class="admin">
<div class="top">
  <strong>&#129302; Chatbot Admin Panel</strong>
  <div>Welcome, <%= Html.esc(adminName) %>
    <a href="<%= ctx %>/">Open Chatbot</a><a href="<%= ctx %>/logout">Logout</a></div>
</div>

<div class="wrap">
  <div>
    <div class="panel">
      <h3><%= editing ? "Edit FAQ" : "Add New FAQ" %></h3>
      <% if (error != null) { %><div class="msg err"><%= Html.esc(error) %></div><% } %>
      <% if ("1".equals(request.getParameter("saved"))) { %><div class="msg ok">FAQ saved.</div><% } %>
      <% if ("1".equals(request.getParameter("deleted"))) { %><div class="msg ok">FAQ deleted.</div><% } %>

      <form action="<%= ctx %>/admin" method="post">
        <input type="hidden" name="action" value="save">
        <input type="hidden" name="id" value="<%= editing ? edit.getId() : 0 %>">
        <label>Question</label>
        <input name="question" maxlength="200" required
               value="<%= edit == null ? "" : Html.esc(edit.getQuestion()) %>">
        <label>Keywords (comma separated)</label>
        <input name="keywords" maxlength="255" required placeholder="fee,fees,tuition"
               value="<%= edit == null ? "" : Html.esc(edit.getKeywords()) %>">
        <label>Answer</label>
        <textarea name="answer" rows="4" maxlength="1000" required><%= edit == null ? "" : Html.esc(edit.getAnswer()) %></textarea>
        <br><br>
        <button class="btn" type="submit"><%= editing ? "Update FAQ" : "Add FAQ" %></button>
        <% if (editing) { %><a class="btn grey" href="<%= ctx %>/admin">Cancel</a><% } %>
      </form>
    </div>

    <div class="panel">
      <h3>All FAQs (<%= faqs == null ? 0 : faqs.size() %>)</h3>
      <div style="overflow-x:auto">
      <table>
        <tr><th>ID</th><th>Question</th><th>Keywords</th><th>Actions</th></tr>
        <% if (faqs != null) for (Faq f : faqs) { %>
        <tr>
          <td><%= f.getId() %></td>
          <td><%= Html.esc(f.getQuestion()) %></td>
          <td><%= Html.esc(f.getKeywords()) %></td>
          <td class="acts">
            <a class="btn green" href="<%= ctx %>/admin?edit=<%= f.getId() %>">Edit</a>
            <form action="<%= ctx %>/admin" method="post"
                  onsubmit="return confirm('Delete this FAQ?');">
              <input type="hidden" name="action" value="delete">
              <input type="hidden" name="id" value="<%= f.getId() %>">
              <button class="btn red" type="submit">Delete</button>
            </form>
          </td>
        </tr>
        <% } %>
      </table>
      </div>
    </div>
  </div>

  <div>
    <div class="panel">
      <h3>&#10067; Unanswered questions</h3>
      <p class="sub" style="text-align:left">Add FAQs for these so the bot learns.</p>
      <ul class="un">
        <% if (unanswered == null || unanswered.isEmpty()) { %>
          <li>Nothing yet &#127881;</li>
        <% } else for (String q : unanswered) { %>
          <li><%= Html.esc(q) %></li>
        <% } %>
      </ul>
    </div>
  </div>
</div>
</body>
</html>
