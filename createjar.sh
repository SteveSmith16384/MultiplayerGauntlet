rm multiplayergauntlet.jar
./compile.sh
jar cvfm multiplayergauntlet.jar manifest.txt -C bin .
jar uvf multiplayergauntlet.jar data
cd libs
cp *.jar ..
cd ..
zip multiplayergauntlet multiplayergauntlet.jar tritonus_share.jar start_linux.sh start_windows.bat readme.txt update_linux_version.sh update_windows_version.bat
rm *.jar
cp -v multiplayergauntlet.zip ~/java/privateservers/NLRWebServer/webroot/
java -version
echo #sudo update-alternatives --config java

