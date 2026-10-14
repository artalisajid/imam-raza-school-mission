#!/bin/zsh
# Run LOCALLY in macOS Terminal by the account owner. Reads the Play-registered key password
# from macOS Keychain without printing or storing it in project files.
set -euo pipefail
umask 077
cd "$HOME/Documents/Imam_Raza_a.a_school"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export ANDROID_HOME="/opt/homebrew/share/android-commandlinetools"
export IMAM_UPLOAD_KEYSTORE="$HOME/.android/keystores/imam-raza-play-upload-2026.jks"
if [[ ! -f "$IMAM_UPLOAD_KEYSTORE" ]]; then
  echo "ERROR: Original Play upload key is missing: $IMAM_UPLOAD_KEYSTORE"
  exit 2
fi
export IMAM_UPLOAD_PASSWORD="$(/usr/bin/security find-generic-password -a imam-raza-school-mission -s imam-raza-play-upload-2026 -w)"
if [[ -z "$IMAM_UPLOAD_PASSWORD" ]]; then
  echo "ERROR: Keychain did not return the original upload-key password."
  exit 3
fi
# Verify this is the key already registered in the Play Console before signing.
EXPECTED="EA:30:F5:AA:AC:BF:47:04:EE:CB:67:5A:2E:20:F0:6A:EA:A6:3C:30"
ACTUAL="$("$JAVA_HOME/bin/keytool" -list -v -keystore "$IMAM_UPLOAD_KEYSTORE" -storepass:env IMAM_UPLOAD_PASSWORD -alias imam-raza-upload | sed -n 's/^.*SHA1: //p' | head -1 | tr -d '\r')"
if [[ "$ACTUAL" != "$EXPECTED" ]]; then
  echo "STOP: The original keystore does not match Play Console's expected upload certificate."
  echo "Expected SHA1: $EXPECTED"
  echo "Found SHA1: $ACTUAL"
  exit 4
fi
echo "Registered upload key fingerprint matched. Building Play bundle..."
./gradlew :app:bundleRelease :app:testDebugUnitTest --no-daemon --console=plain
BUNDLE="app/build/outputs/bundle/release/app-release.aab"
"$JAVA_HOME/bin/jarsigner" -verify "$BUNDLE" > /dev/null
CERT_SHA1="$("$JAVA_HOME/bin/keytool" -printcert -jarfile "$BUNDLE" | sed -n 's/^.*SHA1: //p' | head -1 | tr -d '\r')"
if [[ "$CERT_SHA1" != "$EXPECTED" ]]; then
  echo "ERROR: Built bundle has the wrong signing certificate."
  exit 5
fi
unset IMAM_UPLOAD_PASSWORD
echo "SUCCESS: Play-registered signature verified."
echo "Upload to Play Console:"
echo "$PWD/$BUNDLE"
