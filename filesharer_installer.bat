
REM  Build the program using the gradle wrapper
cd /
cd "%ProgramFiles%\Filesharer"
call gradle wrapper
call ./gradlew shadowJar

REM  Create Necessary Directories and Files
cd /
cd "%USERPROFILE%"
mkdir Filesharer
cd Filesharer
mkdir tokens
mkdir downloads
echo "" > filesSaved.txt

REM  Create Desktop Shortcut
@echo off
echo [InternetShortcut] >> "%USERPROFILE%\Desktop\Filesharer.url"
echo URL="%ProgramFiles%\Filesharer\launcher.bat" >> "%USERPROFILE%\Desktop\Filesharer.url"
echo IconFile="%ProgramFiles%\Filesharer\src\main\resources\Filesharer.ico" >> "%USERPROFILE%\Desktop\Filesharer.url"
echo IconIndex=20 >> "%USERPROFILE%\Desktop\Filesharer.url"
timeout 4

@echo on
echo Installation completed succesfully! 
pause