package Interface;

import Interface.Config.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("Library");
        var config = Config.getInstance();
        File file = new File(config.getIconPath());
        primaryStage.getIcons().add(new Image(file.toURI().toURL().toExternalForm()));
        var scene = new Scene(root, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
