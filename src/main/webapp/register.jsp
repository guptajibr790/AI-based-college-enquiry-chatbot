<%@ page contentType="text/html;charset=UTF-8" %>
<% String ctx = request.getContextPath(); String error = (String) request.getAttribute("error"); %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Create Admin</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<div class="card">
  <h1>&#128100; Create Admin</h1>
  <p class="sub">One-time setup for the first admin</p>

  <% if (error != null) { %>
    <div class="msg err"><%= com.chatbot.util.Html.esc(error) %></div>
  <% } %>

  <form action="<%= ctx %>/register" method="post" onsubmit="return check()">
    <label for="fullName">Full Name</label>
    <input type="text" id="fullName" name="fullName" maxlength="100" required>
    <label for="email">Email</label>
    <input type="email" id="email" name="email" maxlength="100" required>
    <label for="password">Password (8-72 characters)</label>
    <input type="password" id="password" name="password" maxlength="72" required>
    <button class="btn full" type="submit">Create Account</button>
  </form>

  <div class="link">Already have an account? <a href="<%= ctx %>/login">Sign in</a></div>
</div>
<script>
function check() {
  var p = document.getElementById("password").value;
  if (p.length < 8 || p.length > 72) { alert("Password must contain 8-72 characters."); return false; }
  return true;
}
</script>
</body>
</html>
