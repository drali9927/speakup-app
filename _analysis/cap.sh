#!/bin/bash
# usage: cap.sh <name>
ADB=/Users/alikavyani/Library/Android/sdk/platform-tools/adb
W="/Users/alikavyani/Desktop/speak up/_analysis"
N="$1"
$ADB exec-out screencap -p > "$W/screens/$N.png"
$ADB shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1
$ADB pull /sdcard/ui.xml "$W/ui/$N.xml" >/dev/null 2>&1
echo "captured: $N  ($(du -h "$W/screens/$N.png" | cut -f1))"
$ADB shell dumpsys activity activities 2>/dev/null | grep -m1 "topResumedActivity\|ResumedActivity" | sed 's/.*u0 //;s/ .*//'
