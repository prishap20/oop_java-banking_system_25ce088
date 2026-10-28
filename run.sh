#!/usr/bin/env bash
# ==============================================================================
# Run Script for OOP Java Banking System
# Roll / Student ID: 25CE088
# ==============================================================================

set -e

# Detect Java Runtime
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA="java"
elif [ -x "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/java" ]; then
    JAVA="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/java"
else
    echo "Error: Java Runtime (java) not found. Please install JDK 11+ or set JAVA_HOME."
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BIN_DIR="$SCRIPT_DIR/bin"

if [ ! -d "$BIN_DIR" ] || [ ! -f "$BIN_DIR/com/bank/Main.class" ]; then
    echo "Binaries not found. Running build first..."
    bash "$SCRIPT_DIR/build.sh"
fi

cd "$SCRIPT_DIR"
exec "$JAVA" -cp "$BIN_DIR" com.bank.Main "$@"
