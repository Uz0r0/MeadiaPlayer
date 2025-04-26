module com.example.mediaplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.example.mediaplayer to javafx.fxml;
    exports com.example.mediaplayer;
}