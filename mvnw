#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

set -e

WRAPPER_JAR="$0"/../.mvn/wrapper/maven-wrapper.jar
WRAPPER_JAR="$(cd "$(dirname "$WRAPPER_JAR")" && pwd -P)/$(basename "$WRAPPER_JAR")"
WRAPPER_PROPERTIES="$(dirname "$WRAPPER_JAR")/maven-wrapper.properties"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

if [ ! -f "$WRAPPER_JAR" ]; then
  DOWNLOAD_URL="$(grep -e '^wrapperUrl=' "$WRAPPER_PROPERTIES" | cut -d'=' -f2-)"
  if [ -n "$MVNW_REPOURL" ]; then
    DOWNLOAD_URL="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
  fi
  mkdir -p "$(dirname "$WRAPPER_JAR")"
  if command -v curl >/dev/null 2>&1; then
    curl -s -o "$WRAPPER_JAR" "$DOWNLOAD_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$WRAPPER_JAR" "$DOWNLOAD_URL"
  else
    echo "Error: curl or wget is required to download the Maven Wrapper." >&2
    exit 1
  fi
fi

exec java -classpath "$WRAPPER_JAR" "$WRAPPER_LAUNCHER" "$@"
