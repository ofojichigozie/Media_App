package media_app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Media_App extends Application{
    /*****Controls for menu*****/
    MenuBar menuBar = new MenuBar();
    Menu fileMenu = new Menu("File");
    MenuItem openSingleFile = new MenuItem("Open file");
    MenuItem openMultiFile = new MenuItem("Open Multiple file");
    MenuItem exitFile = new MenuItem("Exit");
    /*****Controls for media*****/
    ImageView playImg = new ImageView(new Image("icons/play.png"));
    ImageView pauseImg = new ImageView(new Image("icons/pause.png"));
    ImageView stopImg = new ImageView(new Image("icons/stop.png"));
    ImageView prevImg = new ImageView(new Image("icons/prev.png"));
    ImageView nextImg = new ImageView(new Image("icons/next.png"));
    ImageView speakerImg = new ImageView(new Image("icons/speaker.png"));
    ImageView speakerMuteImg = new ImageView(new Image("icons/speaker_mute.png"));
    Button playPauseBtn = new Button();
    Button prevBtn = new Button();
    Button stopBtn = new Button();
    Button nextBtn = new Button();
    Button volBtn = new Button();
    Slider volCtrl = new Slider();
    ProgressBar progressBar = new ProgressBar(0);
    Label timeLbl = new Label("00:00:00 | 00:00:00");
    /*****Object for media*****/
    FileChooser singleFileChooser = new FileChooser();
    FileChooser multiFileChooser = new FileChooser();
    Media media = null;
    MediaPlayer mediaPlayer = null;
    MediaView mediaView = new MediaView();
    /*****Misc*****/
    String filename = null;
    int curTime, curHour, curMin, curSec, curRemainder;
    int totalTime, totalHour, totalMin, totalSec, totalRemaider;
    
    public void start(Stage stage){
        /*****Setting up the menu bar*****/
        singleFileChooser.getExtensionFilters().add(new ExtensionFilter("MP4 Videos", "*.mp4"));
        /*****Setting up the menu bar*****/
        fileMenu.getItems().addAll(openSingleFile, openMultiFile, exitFile);
        menuBar.getMenus().add(fileMenu);
        
        /*****Setting up the media controls*****/
        progressBar.prefWidthProperty().bind(stage.widthProperty());
        playPauseBtn.setGraphic(playImg);
        prevBtn.setGraphic(prevImg);
        stopBtn.setGraphic(stopImg);
        nextBtn.setGraphic(nextImg);
        volBtn.setGraphic(speakerImg);
        HBox controlPane = new HBox();
        controlPane.setStyle("-fx-background-color: #FFEEEE;");
        controlPane.setPadding(new Insets(2));
        controlPane.getChildren().addAll(playPauseBtn, prevBtn, stopBtn, nextBtn, volBtn, volCtrl, timeLbl);
        
        /*****Disable the media controls*****/
        playPauseBtn.setDisable(true);
        prevBtn.setDisable(true);
        stopBtn.setDisable(true);
        nextBtn.setDisable(true);
        volBtn.setDisable(true);
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");
        root.setTop(menuBar);
        //root.setCenter(mediaView);
        root.setCenter(new ImageView(new Image("icons/centreImg.png")));
        root.setBottom(new VBox(progressBar, controlPane));
        
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Gozmedia");
        stage.show();
        
        openSingleFile.setOnAction(e->{
            try{
                String initDirectory = System.getProperty("user.home") + "\\Videos";
                if(Files.exists(Paths.get(initDirectory))){
                    singleFileChooser.setInitialDirectory(new File(initDirectory));
                }
                File mediaFile = singleFileChooser.showOpenDialog(stage);
                if(mediaFile != null){
                    String path = mediaFile.getPath();
                    filename = mediaFile.getName();
                    media = new Media(new File(path).toURI().toString());
                    if(mediaPlayer != null){
                        mediaPlayer.dispose();
                    }
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaPlayer.setAutoPlay(true);
                    root.setCenter(mediaView);
                    //Update window title
                    stage.setTitle("Gozmedia - " + filename);
                    /**Activating progress bar**/
                    mediaPlayer.currentTimeProperty().addListener(ex->{
                        try{
                            /**Getting time of media file**/
                            totalTime = (int)mediaPlayer.getTotalDuration().toSeconds();
                            totalHour = totalTime / 3600;
                            totalMin = (totalTime % 3600) / 60;
                            totalSec = (totalTime % 3600) % 60;
                            curTime = (int)mediaPlayer.getCurrentTime().toSeconds();
                            curHour = curTime / 3600;
                            curMin = (curTime % 3600) / 60;
                            curSec = (curTime % 3600) % 60;
                            progressBar.setProgress(mediaPlayer.getCurrentTime().toSeconds() / mediaPlayer.getTotalDuration().toSeconds());
                            timeLbl.setText(String.format("%02d:%02d:%02d | %02d:%02d:%02d", curHour, curMin, curSec, totalHour, totalMin, totalSec));
                            if(curTime == totalTime){
                                progressBar.setProgress(0);
                                timeLbl.setText("00:00:00 | 00:00:00");
                                playPauseBtn.setGraphic(playImg);
                                root.setCenter(new ImageView(new Image("icons/centreImg.png")));
                            }
                        }catch(Exception exx){}
                    });
                    /*****Enable the media controls*****/
                    playPauseBtn.setDisable(false);
                    prevBtn.setDisable(false);
                    stopBtn.setDisable(false);
                    nextBtn.setDisable(false);
                    volBtn.setDisable(false);
                    //Update the playPauseBtn
                    playPauseBtn.setGraphic(pauseImg);
                }else{
                    root.setCenter(new ImageView(new Image("icons/centreImg.png")));
                }
            }catch(Exception ex){}
        });
        
        openMultiFile.setOnAction(e->{
            
        });
        
        exitFile.setOnAction(e->{
            System.exit(0);
        });
        
        playPauseBtn.setOnAction(e->{
            if(mediaPlayer != null){
                try{
                    if(playPauseBtn.getGraphic() == playImg){
                        mediaPlayer.play();
                        playPauseBtn.setGraphic(pauseImg);
                    }else{
                        mediaPlayer.pause();
                        playPauseBtn.setGraphic(playImg);
                    }
                }catch(Exception ex){}
            }
        });
        
        prevBtn.setOnAction(e->{
            if(mediaPlayer != null){
                try{
                    mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(30)));
                }catch(Exception ex){}
            }
        });
        
        stopBtn.setOnAction(e->{
            if(mediaPlayer != null){
                try{
                    mediaPlayer.stop();
                    stage.setTitle("Gozmedia");
                    playPauseBtn.setGraphic(playImg);
                }catch(Exception ex){}
            }
        });
        
        nextBtn.setOnAction(e->{
            if(mediaPlayer != null){
                try{
                    mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(30)));
                }catch(Exception ex){}
            }
        });
        
        volBtn.setOnAction(e->{
            if(mediaPlayer != null){
                try{
                    if(volBtn.getGraphic() == speakerImg){
                        mediaPlayer.setMute(true);
                        volBtn.setGraphic(speakerMuteImg);
                    }else{
                        mediaPlayer.setMute(false);
                        volBtn.setGraphic(speakerImg);
                    }
                }catch(Exception ex){}
            }
        });
        
        volCtrl.valueProperty().addListener(e->{
            if(mediaPlayer != null){
                mediaPlayer.setVolume(volCtrl.getValue() / 100);
            }
        });
    }
    
    public static void main(String[] args){
        Application.launch(args);
    }
}