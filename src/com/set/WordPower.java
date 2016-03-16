package com.set;
import java.net.URL;

import com.set.gui.controller.FXMLWordPowerController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class WordPower extends Application{
	private Image icon;
	private URL mainWindowUrl;
	@Override
    public void start(Stage stage) throws Exception {
       stage.setTitle("Word Power");
       mainWindowUrl = getClass().getResource("/com/set/gui/resources/fxml/fxml_wordpower.fxml");
       icon = new Image(getClass().getResourceAsStream("/com/set/gui/resources/img/word_power.png"));
       FXMLLoader fxmlLoader = new FXMLLoader(mainWindowUrl);
       Parent root = (Parent) fxmlLoader.load();
       FXMLWordPowerController fwpc = fxmlLoader.getController();
       
       Scene scene = new Scene(root);
       stage.getIcons().add(icon);
       stage.setScene(scene);
       fwpc.setStage(stage);
       stage.show();
//       fwpc.handleIndexDir(new ActionEvent());
       fwpc.handleChooseMode();
    }
 
    public static void main(String[] args) {
        launch(args);
        // query results on MobyDick10
//        another night
//        normal
//        10,2,3,6,9
//        not
//        4,5
//
//        followed suit
//        3,5
//        10,2,7,9
//
//        followed -suit + tree
//        1,10,2,7,9
//        "a few hours"
//        1,6
//        "a NEAR/2 hours"
//        1,6
    }
}


