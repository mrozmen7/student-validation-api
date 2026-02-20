#!/usr/bin/env bash
# pre-commit-verify.sh — Run Maven tests before allowing a commit.
# Intended to be called by .git/hooks/pre-commit.

# Strict mode: exit on error, unset variable, or pipe failure.
set -euo pipefail

# Move into the Maven module where pom.xml resides.
cd "$(dirname "$0")/../student-validation-ai"

echo "[pre-commit] Running tests in $(pwd) ..."

# Run tests quietly; non-zero exit from mvnw will propagate and block the commit.
./mvnw -q test

echo "[pre-commit] All tests passed."
