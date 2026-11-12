<%@ page contentType="text/html;charset=UTF-8" %>
<% String ctx = request.getContextPath(); String error = (String) request.getAttribute("error"); %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin Login</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<div class="card">
  <h1>&#128274; Admin Login</h1>
  <p class="sub">Manage the chatbot knowledge base</p>

  <% if ("1".equals(request.getParameter("registered"))) { %>
    <div class="msg ok">Account created. Please log in.</div>
  <% } %>
  <% if (error != null) { %>
    <div class="msg err"><%= com.chatbot.util.Html.esc(error) %></div>
  <% } %>

  <form action="<%= ctx %>/login" method="post">
    <label for="email">Email</label>
    <input type="email" id="email" name="email" maxlength="100" required>
    <label for="password">Password</label>
    <input type="password" id="password" name="password" required>
    <button class="btn full" type="submit">Sign In</button>
  </form>

  <div class="link">First time? <a href="<%= ctx %>/register">Create admin</a> &middot;
    <a href="<%= ctx %>/">Back to chat</a></div>
</div>
</body>
</html>
