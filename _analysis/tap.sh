#!/bin/bash
ADB=/Users/alikavyani/Library/Android/sdk/platform-tools/adb
W="/Users/alikavyani/Desktop/speak up/_analysis"
OUT=$("$W/ui.sh" tap "$1")
echo "$OUT" | head -1
C=$(echo "$OUT" | grep '^COORD' | awk '{print $2,$3}')
[ -n "$C" ] && $ADB shell input tap $C && sleep ${2:-3}
