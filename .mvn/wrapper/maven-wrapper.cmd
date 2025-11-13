@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@IF "%DEBUG%"=="" @ECHO OFF
@SETLOCAL

set WRAPPER_JAR="%~dp0maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL=
for /F "usebackq tokens=1,2 delims==" %%A in ("%~dp0maven-wrapper.properties") do (
  if "%%A"=="wrapperUrl" set DOWNLOAD_URL=%%B
)

if exist %WRAPPER_JAR% goto execute

if "%MVNW_REPOURL%"=="" (
  set DOWNLOAD_URL=%DOWNLOAD_URL%
) else (
  set DOWNLOAD_URL=%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
)

powershell -Command "\$progressPreference='silentlyContinue'; Invoke-WebRequest '%DOWNLOAD_URL%' -OutFile '%WRAPPER_JAR%'" >NUL 2>&1
if ERRORLEVEL 1 goto error

:execute
set JAVA_EXE=
for %%A in (java.exe) do set JAVA_EXE=%%~$PATH:A
if defined JAVA_HOME goto initHome
if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
goto error

:initHome
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to "%JAVA_HOME%" but the directory does not contain a java executable.
echo.
goto error

:init
set JAVA_CMD="%JAVA_EXE%"
set WRAPPER_JAR_UNQUOTED=%WRAPPER_JAR:"=%
%JAVA_CMD% %JVM_CONFIG_MAVEN_OPTS% %MAVEN_OPTS% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" -classpath "%WRAPPER_JAR_UNQUOTED%" %WRAPPER_LAUNCHER% %MAVEN_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@ENDLOCAL
if not "%MAVEN_SKIP_EXIT%"=="true" exit /b %ERROR_CODE%
