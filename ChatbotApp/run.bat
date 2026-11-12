@echo off
REM =====================================================================
REM  AI-Based College Enquiry Chatbot - one-click build, deploy and run
REM  1) Edit the TOMCAT line below (folder that contains "bin" and "lib")
REM  2) Double-click run.bat
REM =====================================================================
set TOMCAT=C:\apache-tomcat-11.0.27

if not exist "%TOMCAT%\lib\servlet-api.jar" (
  echo.
  echo Tomcat folder not found: %TOMCAT%
  echo Open run.bat in Notepad and fix the TOMCAT line.
  pause
  exit /b 1
)

where javac >nul 2>nul
if errorlevel 1 (
  echo.
  echo javac not found. Install JDK 21 and add its bin folder to PATH.
  pause
  exit /b 1
)

REM Tomcat needs JAVA_HOME - find it from javac if it is not set
if not defined JAVA_HOME (
  for /f "delims=" %%i in ('where javac') do set JC=%%i
  for %%i in ("%JC%\..\..") do set JAVA_HOME=%%~fi
)

echo Stopping Tomcat if it is running...
call "%TOMCAT%\bin\shutdown.bat" >nul 2>nul
timeout /t 4 >nul

set APP=%TOMCAT%\webapps\ChatbotApp
if exist "%APP%" rmdir /s /q "%APP%"
mkdir "%APP%\WEB-INF\classes"

echo Copying web pages...
xcopy /e /i /y "%~dp0src\main\webapp\*" "%APP%\" >nul

echo Compiling Java...
pushd "%~dp0src\main\java"
javac -encoding UTF-8 -cp "%TOMCAT%\lib\servlet-api.jar" -sourcepath . -d "%APP%\WEB-INF\classes" com\chatbot\servlet\ChatServlet.java com\chatbot\servlet\AuthServlet.java com\chatbot\servlet\AdminServlet.java com\chatbot\util\Html.java
if errorlevel 1 (
  popd
  echo.
  echo Compile failed - read the error above.
  pause
  exit /b 1
)
popd

echo Starting Tomcat...
call "%TOMCAT%\bin\startup.bat"
timeout /t 10 >nul
start "" http://localhost:8080/ChatbotApp/
echo.
echo Chatbot opened in your browser. To stop: close the Tomcat window.
pause
