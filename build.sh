#!/usr/bin/env bash
# ==============================================================================
# Build Script for OOP Java Banking System
# Roll / Student ID: 25CE088
# ==============================================================================

set -e

# Detect Java Compiler
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
elif command -v javac >/dev/null 2>&1; then
    JAVAC="javac"
elif [ -x "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/javac" ]; then
    JAVAC="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/javac"
else
    echo "Error: Java compiler (javac) not found. Please install JDK 11+ or set JAVA_HOME."
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/src"
BIN_DIR="$SCRIPT_DIR/bin"

echo "Building Secure OOP Java Banking System..."
mkdir -p "$BIN_DIR"

SOURCES=$(find "$SRC_DIR" -name "*.java")
"$JAVAC" -d "$BIN_DIR" $SOURCES

echo "Build successful! Binaries compiled to $BIN_DIR"
