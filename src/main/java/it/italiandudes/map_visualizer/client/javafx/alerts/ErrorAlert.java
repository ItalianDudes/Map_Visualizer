package it.italiandudes.map_visualizer.client.javafx.alerts;

import it.italiandudes.map_visualizer.client.javafx.JFXDefs;
import it.italiandudes.map_visualizer.client.javafx.utils.ThemeHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public final class ErrorAlert extends Alert {

    //Constructors
    public ErrorAlert(String title, String header, String content){
        super(AlertType.ERROR);
        this.setResizable(true);
        ((Stage) getDialogPane().getScene().getWindow()).getIcons().add(JFXDefs.AppInfo.LOGO);
        if(title!=null) setTitle(title);
        if(header!=null) setHeaderText(header);
        if(content!=null) {
            TextArea area = new TextArea(content);
            area.setWrapText(true);
            area.setEditable(false);
            getDialogPane().setContent(area);
        }
        ThemeHandler.loadConfigTheme(getDialogPane().getScene().getRoot());
        showAndWait();
    }
    public ErrorAlert(String header, String content){
        this(null, header, content);
    }
    public ErrorAlert(){
        this(null,null,null);
    }

}