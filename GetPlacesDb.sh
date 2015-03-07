#!/bin/bash
# Retrieves places.sqlite into ~/delete/
#
# Works based on http://stackoverflow.com/questions/22703254/copying-files-in-adb-shell-with-run-as
#
cat <<EOF |  ~/Development/android-sdk-macosx/platform-tools/adb shell
run-as com.vmware.android.googleplaces
ls -la
cp ./databases/places.sqlite ./
chmod 777 places.sqlite
echo test1
exit
ls -la
cd /sdcard/
mkdir serkan
cp /data/data/com.vmware.android.googleplaces/places.sqlite /sdcard/serkan/
ls -la ./serkan/
echo test2
exit
EOF

#
# Following 3 attempts all failed to deliver what I want to achieve.
#
# 1. This works. Nexus 5, Android5.0.1.
#~/Development/android-sdk-macosx/platform-tools/adb shell "cd /sdcard/; ls -la; echo testtest; cd serkan;ls -la"

# 2. This hangs, you have to do ctrl^c on command line. Nexus 5, Android5.0.1.
#~/Development/android-sdk-macosx/platform-tools/adb shell "cd /sdcard/; ls -la; echo testtest; cd serkan;ls -la; run-as com.vmware.android.googleplaces"

# 3. This says cannot execute pwd.Nexus 5, Android5.0.1.
#~/Development/android-sdk-macosx/platform-tools/adb shell "cd /sdcard/; ls -la; echo testtest; cd serkan;ls -la; run-as com.vmware.android.googleplaces 'pwd'"

#
# Continue with places.sqlite import
#
cd ~/delete/
ls -la # for testing purposes
~/Development/android-sdk-macosx/platform-tools/adb pull /sdcard/serkan/places.sqlite ./
ls -la

cat <<EOF | sqlite3 places.sqlite
select * from place;
.exit
EOF
exit 0