#!/bin/sh
set -eu

base_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
wrapper_dir="$base_dir/.mvn/wrapper"
wrapper_jar="$wrapper_dir/maven-wrapper.jar"
properties="$wrapper_dir/maven-wrapper.properties"

if [ ! -r "$wrapper_jar" ]; then
    wrapper_url=$(sed -n 's/^wrapperUrl=//p' "$properties")
    mkdir -p "$wrapper_dir"
    if command -v curl >/dev/null 2>&1; then
        curl --fail --location --silent --show-error "$wrapper_url" --output "$wrapper_jar"
    elif command -v wget >/dev/null 2>&1; then
        wget --quiet "$wrapper_url" --output-document="$wrapper_jar"
    else
        echo "Maven Wrapper requires curl or wget to download $wrapper_url" >&2
        exit 1
    fi
fi

java_cmd="${JAVA_HOME:+$JAVA_HOME/bin/}java"
exec "$java_cmd" ${MAVEN_OPTS:-} -classpath "$wrapper_jar" \
    -Dmaven.multiModuleProjectDirectory="$base_dir" \
    org.apache.maven.wrapper.MavenWrapperMain "$@"