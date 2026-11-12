# AI-Based College Enquiry Chatbot System  (JSON version)

Java Servlet + JSP. No MySQL, no Maven, no Eclipse needed.
You only need: **JDK 21** (with `javac` in PATH) and **Apache Tomcat 11**.

## Run
1. Unzip the project anywhere.
2. Right-click `run.bat` > Edit, and set the `TOMCAT` line to your Tomcat folder
   (the one that contains `bin` and `lib`), e.g. `C:\apache-tomcat-11.0.27`.
3. Double-click `run.bat`. It compiles, deploys, starts Tomcat and opens the browser.
4. Admin: http://localhost:8080/ChatbotApp/register (first time only), then /login.

To stop: close the Tomcat window. If port 8080 is busy, stop Eclipse's server first.

## Data
JSON files are created automatically in `C:\Users\<you>\chatbot-data\`:
`faqs.json`, `admin.json` (hashed password), `unanswered.json`. Delete the folder to reset.

## How it works
Student message -> `/chat` servlet (POST) -> `ChatBot` scores each FAQ by keyword matches ->
best answer shown. Admin (HttpSession login) adds / edits / deletes FAQs stored in `faqs.json`.
