package com.example.mediaplayer;

import controller.MainController;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    private ArrayList<File> songs = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private int id;
    private boolean isRepeatOn = false;
    private boolean isMixMode = false;
    private int lastRandomIndex = -1;

    @Override
    public void start(Stage stage) throws IOException {

        ListView<String> playList = new ListView<>();
        playList.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue != null) {
                        int selectedModule = playList.getSelectionModel().getSelectedIndex();
                        id = selectedModule;
                    }
                }
        );


        Label currentTrackText = new Label("Current Track: ");
        Label track = new Label();
        Label duration = new Label("00:00 / 00:00");
        Label ifno1 = new Label("P - pause/play");
        Label ifno2 = new Label("A - rewind to the left");
        Label ifno3 = new Label("D - rewind to the right");

        Slider trackSlider = new Slider();

        Button input = new Button("Choose playlist");
        Button mix = new Button("\uD83D\uDD00 Mix");
        Button previous  = new Button("⏮ Prev");
        Button play = new Button("▶ Play");
        Button pause = new Button("⏸ Pause");
        Button stop = new Button("⏹ Stop");
        Button next = new Button("⏭ Next");
        Button repeat = new Button("\uD83D\uDD01 Repeat");

        input.setTooltip(MainController.makeTooltipFast(new Tooltip("Новый плейлист")));
        mix.setTooltip(MainController.makeTooltipFast(new Tooltip("Случайный файл")));
        previous.setTooltip(MainController.makeTooltipFast(new Tooltip("Предыдущий файл")));
        play.setTooltip(MainController.makeTooltipFast(new Tooltip("Воспризвести с начала")));
        pause.setTooltip(MainController.makeTooltipFast(new Tooltip("Пауза")));
        stop.setTooltip(MainController.makeTooltipFast(new Tooltip("Остановить воспроизведение")));
        next.setTooltip(MainController.makeTooltipFast(new Tooltip("Следущий файл")));
        repeat.setTooltip(MainController.makeTooltipFast(new Tooltip("Повтор файла")));

        input.setOnAction(e ->{
            songs.clear();
            playList.getItems().clear();

            MainController.openDirectory(songs);

            for (int i = 0; i < songs.size(); i++){
                playList.getItems().add(FileUtils.getAudioFiles(songs.get(i)));
            }
        });

        mix.setOnAction(e ->{
            isMixMode = !isMixMode;
            if (isMixMode) {
                mix.setStyle("-fx-background-color: #228B22;");
            } else {
                mix.setStyle(null);
            }
        });

        previous.setOnAction(e ->{
            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                id--;
                if (id < 0){
                    id = songs.size() - 1;
                }
                playSong(id, track, playList, trackSlider, duration);
            }
        });

        play.setOnAction(e ->{
            id = playList.getSelectionModel().getSelectedIndex();

            playSong(id, track, playList, trackSlider, duration);
        });

        pause.setOnAction(e ->{
            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                pause.setTooltip(MainController.makeTooltipFast(new Tooltip("Воспроизвести")));
            } else {
                mediaPlayer.play();
                pause.setTooltip(MainController.makeTooltipFast(new Tooltip("Пауза")));
            }
        });

        stop.setOnAction(e ->{
            mediaPlayer.stop();
        });

        next.setOnAction(e ->{
            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                id++;

                if (id >= songs.size()){
                    id = 0;
                }
                playSong(id, track, playList, trackSlider, duration);
            }
        });

        repeat.setOnAction(e ->{
            isRepeatOn = !isRepeatOn;
            MainController.repeat(mediaPlayer, isRepeatOn, repeat);
        });


        VBox left = new VBox(15, input, ifno1, ifno2, ifno3);
        HBox buttons = new HBox(10, mix, previous, play, pause, stop, next, repeat);
        VBox right = new VBox(5, playList, currentTrackText, track, trackSlider, duration, buttons);

        HBox root = new HBox(70, left, right);

        Scene scene = new Scene(root, 645, 500);
        stage.setTitle("MediaPlayer!");
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case P -> {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.play();
                        }
                    }
                    event.consume();
                }
                case D -> {
                    if (mediaPlayer != null) {
                        Duration currentTime = mediaPlayer.getCurrentTime();
                        Duration jump = Duration.seconds(5);
                        mediaPlayer.seek(currentTime.add(jump));
                    }
                }
                case A -> {
                    if (mediaPlayer != null) {
                        Duration currentTime = mediaPlayer.getCurrentTime();
                        Duration jump = Duration.seconds(5);
                        Duration newTime = currentTime.subtract(jump);
                        if (newTime.lessThan(Duration.ZERO)) {
                            newTime = Duration.ZERO;
                        }
                        mediaPlayer.seek(newTime);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }

    private void playSong(int index, Label track, ListView playList, Slider trackSlider, Label duration) {
        if (songs.isEmpty()) return;

        if (isMixMode) {
            int randomIndex;
            do {
                randomIndex = (int) (Math.random() * songs.size());
            } while (randomIndex == lastRandomIndex && songs.size() > 1);

            id = randomIndex;
            lastRandomIndex = randomIndex;
        } else {
            if (index >= songs.size()) {
                id = 0;
            } else {
                id = index;
            }
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        mediaPlayer = MainController.play(mediaPlayer, songs.get(id));
        track.setText(FileUtils.getAudioFiles(songs.get(id)));
        playList.getSelectionModel().select(id);
        MainController.updateProgress(mediaPlayer, duration, trackSlider);

        mediaPlayer.setOnEndOfMedia(() -> {
            if (isRepeatOn) {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            } else {
                playSong(id + 1, track, playList, trackSlider, duration);
            }
        });
    }
}