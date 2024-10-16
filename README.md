# Filesharer
This windows application allows to sync files between multiple devices using the Google Drive API with Google Drive as the cloud database. To use the app, follow the steps below.

## Installation (Windows Only)
To use this application, the following dependencies are required. 
1. Java version 11 or higher ([install](https://www.oracle.com/java/technologies/downloads/))
2. Gradle version 8.10.1 or higher ([install](https://gradle.org/install/))
3. Google Cloud project (follow the steps taken [here](https://developers.google.com/workspace/guides/create-project) and create a [credential](https://developers.google.com/drive/api/quickstart/java))

To use this application, we need to create a Google Drive Folder
1. Go to [Google Drive](https://drive.google.com/drive/home) and create a new folder (name does not matter). **This folder should only contain documents that are used by Filesharer.**
2. Open the folder and copy the ID after `https://drive.google.com/drive/folders/`. Save this ID for now.  

When all dependencies are installed, follow the steps outlined below to install it on your machine.
1. Clone the repository in `C:\Program Files\`
2. Open `Settings.java` in `src\main\java\Settings.java` and change DRIVE_FOLDER_ID to your Folder ID found above  
3. Open a command terminal in the Filesharer directory and enter `./gradlew shadowJar`
4. Create a Folder called "Filesharer" in `C:\Users\current_user\`
5. Inside the Filesharer folder place your credentials.json file
6. Also create two directories inside this folder, called `tokens` and `downloads`
7. Lastly create an empty text file `filesSaved.txt` 

## Using the application
The program can be launched using `java -jar C:\Users\current_user\Filesharer\build\libs\Filesharer-1.0-all.jar `. All paths to the files that will be uploaded to the Drive are stored in savedFiles.txt. 
To add additional files type `add` and put in the path of the file that needs to be saved. Paths can be removed from this file using `remove`. The file can then be uploaded using `upload`. Note that the first 
time using this program might open Google prompting to allow Filesharer to access your Google Drive Folder. Files can be downloaded to `C:\Users\current_user\Filesharer\downloads\` using `download`. To delete 
all files in the Drive folder, use `delete`. 

The application can be run without user input using `java -jar C:\Users\current_user\Filesharer\build\libs\Filesharer-1.0-all.jar boot` or 
`java -jar C:\Users\current_user\Filesharer\build\libs\Filesharer-1.0-all.jar shutdown`. These automatically download or delete and upload all files in savedFiles.txt respectively.
