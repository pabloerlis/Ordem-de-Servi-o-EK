#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
SDK="$ANDROID_HOME"
TOOLS="$SDK/build-tools/35.0.0"
JAR="$SDK/platforms/android-35/android.jar"
mkdir -p build/classes build/dex build/out
"$TOOLS/aapt2" link -I "$JAR" --manifest android/AndroidManifest.xml --version-code 1 --version-name 1.0.0 -A android/assets -o build/unsigned.apk
javac -source 8 -target 8 -classpath "$JAR" -d build/classes android/src/MainActivity.java
"$TOOLS/d8" --lib "$JAR" --min-api 23 --output build/dex $(find build/classes -name '*.class')
(cd build/dex && zip -q ../unsigned.apk classes.dex)
"$TOOLS/zipalign" -f 4 build/unsigned.apk build/aligned.apk
KEYSTORE=build/debug.keystore
if [[ ! -f "$KEYSTORE" ]]; then
  keytool -genkeypair -keystore "$KEYSTORE" -storepass android -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=EK,C=BR"
fi
"$TOOLS/apksigner" sign --ks "$KEYSTORE" --ks-pass pass:android --key-pass pass:android --ks-key-alias androiddebugkey --out build/out/Checklist-OS-EK-v1.0.0.apk build/aligned.apk
"$TOOLS/apksigner" verify --verbose build/out/Checklist-OS-EK-v1.0.0.apk
