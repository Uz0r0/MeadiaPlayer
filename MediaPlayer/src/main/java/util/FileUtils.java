package util;

import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;

public class FileUtils {

    public static File chooseDirectory(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        return selectedDirectory;
    }

    public static String getAudioFiles(File directory){
        String fileName = directory.getName();

        if (fileName.endsWith(".mp3")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }

        return fileName;
    }

    public static String formatTime(Duration duration){
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
