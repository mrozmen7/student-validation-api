#!/usr/bin/env bash
# pre-commit-verify.sh — Run Maven tests before allowing a commit.
# Intended to be called by .git/hooks/pre-commit.

# Strict mode: exit on error, unset variable, or pipe failure.
set -euo pipefail

# Move into the Maven module where pom.xml resides.
# Resolve the real script location (follows symlinks) so the path is correct
# regardless of whether this script is called directly or via .git/hooks/.
SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "$0")")" && pwd)"
cd "$SCRIPT_DIR/../student-validation-ai"

echo "[pre-commit] Running tests in $(pwd) ..."

# Run tests quietly; non-zero exit from mvnw will propagate and block the commit.
./mvnw -q test

echo "[pre-commit] All tests passed."
