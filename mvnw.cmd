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

set ERROR_CODE=0

@REM Locate project base directory
set SCRIPT_DIR=%~dp0
if "%SCRIPT_DIR%"=="" set SCRIPT_DIR=.
set MAVEN_PROJECTBASEDIR=%SCRIPT_DIR%

@REM Read additional JVM options if present
set JVM_CONFIG_PATH=%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config
if exist "%JVM_CONFIG_PATH%" (
	set /p JVM_CONFIG_MAVEN_OPTS=<"%JVM_CONFIG_PATH%"
)

@REM Setup the command line
set MAVEN_CMD_LINE_ARGS=%*

@REM Execute the wrapper
set MAVEN_CMD_WRAPPER=%SCRIPT_DIR%\.mvn\wrapper\maven-wrapper.cmd
call "%MAVEN_CMD_WRAPPER%" %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@ENDLOCAL
if not "%MAVEN_SKIP_EXIT%"=="true" exit /b %ERROR_CODE%
