package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import util.FileUtils;


import java.io.File;
import java.util.ArrayList;

import static util.FileUtils.formatTime;

public class MainController {
    private static BooleanProperty isTracking = new SimpleBooleanProperty(false);

    public static void openDirectory(ArrayList<File> songs) {

        File selectedDirectory = FileUtils.chooseDirectory();
        File[] audioFiles = new File[0];

        if (selectedDirectory != null) {
            audioFiles = selectedDirectory.listFiles(file ->
                    file.getName().endsWith(".mp3") || file.getName().endsWith(".wav"));
        }

        for (File audioFile : audioFiles) {
            songs.add(audioFile);
        }
    }

    public static MediaPlayer play(MediaPlayer mediaPlayer, File song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(song.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();

        return mediaPlayer;
    }

    public static void repeat(MediaPlayer mediaPlayer, boolean isRepeatOn, Button button){
        if (isRepeatOn) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            button.setStyle("-fx-background-color: #228B22;");
        } else {
            mediaPlayer.setCycleCount(1);
            button.setStyle(null);
        }
    }

    public static void updateProgress(MediaPlayer mediaPlayer, Label durationLabel, Slider trackSlider) {
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!isTracking()) {
                Duration current = mediaPlayer.getCurrentTime();
                Duration total = mediaPlayer.getTotalDuration();
                if (total != null && !total.isUnknown()) {
                    durationLabel.setText(formatTime(current) + " / " + formatTime(total));
                    trackSlider.setValue(current.toSeconds());
                }
            }
        });

        mediaPlayer.setOnReady(() -> {
            Duration total = mediaPlayer.getMedia().getDuration();
            if (total != null && !total.isUnknown()) {
                trackSlider.setMax(total.toSeconds());
            }
        });

        trackSlider.setOnMousePressed(e -> setTracking(true));
        trackSlider.setOnMouseReleased(e -> {
            setTracking(false);
            mediaPlayer.seek(Duration.seconds(trackSlider.getValue()));
        });
    }

    public static void setTracking(boolean value) {
        isTracking.set(value);
    }

    public static boolean isTracking() {
        return isTracking.get();
    }

    public static Tooltip makeTooltipFast(Tooltip tooltip) {
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.ZERO);
        return tooltip;
    }

    public static int mix(MediaPlayer currentPlayer, ArrayList<File> songs, Label trackLabel, Label duration, ListView<String> playList, Slider trackSlider) {
        if (songs.isEmpty()) {
            return -1;
        }

        int randomIndex = (int) (Math.random() * songs.size());

        if (currentPlayer != null) {
            currentPlayer.stop();
        }

        Media media = new Media(songs.get(randomIndex).toURI().toString());
        MediaPlayer newPlayer = new MediaPlayer(media);
        newPlayer.play();

        trackLabel.setText(FileUtils.getAudioFiles(songs.get(randomIndex)));
        playList.getSelectionModel().select(randomIndex);
        updateProgress(newPlayer, duration, trackSlider);

        return randomIndex;
    }

}


