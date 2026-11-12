<%@ page contentType="text/html;charset=UTF-8" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>AI-Based College Enquiry Chatbot</title>
<link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>
<div class="card chat">
  <div class="chat-head">
    <div class="avatar">&#127891;</div>
    <div>
      <h2>College Enquiry Assistant</h2>
      <small><span class="dot"></span>Online &middot; Ask me anything</small>
    </div>
  </div>

  <div id="box">
    <div class="bubble bot">Hi! &#128075; I can answer your questions about admission, courses, fees, hostel, placements and more. How can I help you?</div>
  </div>

  <div class="chips">
    <button class="chip" type="button">Admission process</button>
    <button class="chip" type="button">Courses offered</button>
    <button class="chip" type="button">Fee structure</button>
    <button class="chip" type="button">Hostel facility</button>
    <button class="chip" type="button">Placements</button>
  </div>

  <div class="send-row">
    <input id="msg" type="text" maxlength="300" placeholder="Type your question..." autocomplete="off">
    <button id="send" class="btn" type="button">Send</button>
  </div>
  <div class="foot"><a href="<%= ctx %>/login">Admin login</a></div>
</div>

<script>
const box = document.getElementById("box");
const input = document.getElementById("msg");

function addBubble(text, who) {
  const d = document.createElement("div");
  d.className = "bubble " + who;
  d.textContent = text;              // textContent => safe from XSS
  box.appendChild(d);
  box.scrollTop = box.scrollHeight;
  return d;
}

async function send(text) {
  text = text.trim();
  if (!text) return;
  addBubble(text, "user");
  input.value = "";
  const typing = addBubble("", "bot typing");
  typing.innerHTML = "<span></span><span></span><span></span>";
  try {
    const res = await fetch("<%= ctx %>/chat", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
      body: "message=" + encodeURIComponent(text)
    });
    const answer = await res.text();
    setTimeout(function () { typing.remove(); addBubble(answer, "bot"); }, 400);
  } catch (e) {
    typing.remove();
    addBubble("Network error. Please try again.", "bot");
  }
}

document.getElementById("send").onclick = function () { send(input.value); };
input.addEventListener("keydown", function (e) { if (e.key === "Enter") send(input.value); });
document.querySelectorAll(".chip").forEach(function (c) {
  c.onclick = function () { send(c.textContent); };
});
</script>
</body>
</html>
