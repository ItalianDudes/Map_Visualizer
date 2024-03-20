package it.italiandudes.map_visualizer.master.javafx.controllers.tabs;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.Client;
import it.italiandudes.map_visualizer.master.javafx.alerts.ErrorAlert;
import it.italiandudes.map_visualizer.master.javafx.components.TextFieldMenuItem;
import it.italiandudes.map_visualizer.master.javafx.components.waypoints.Waypoint;
import it.italiandudes.map_visualizer.master.javafx.scenes.elements.*;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class ControllerSceneTabMap {

    // Attributes
    private final BooleanProperty wPressed = new SimpleBooleanProperty();
    private final BooleanProperty aPressed = new SimpleBooleanProperty();
    private final BooleanProperty sPressed = new SimpleBooleanProperty();
    private final BooleanProperty dPressed = new SimpleBooleanProperty();
    private final BooleanBinding keyPressed = wPressed.or(aPressed).or(sPressed).or(dPressed);
    private double scaledWidth;
    private double scaledHeight;
    private double mapMovementOriginSceneX;
    private double mapMovementOriginSceneY;
    private double mapMovementOriginLayoutX;
    private double mapMovementOriginLayoutY;
    private static final int MOVEMENT_KEY_PRESSED = 2;

    // Graphic Elements
    @FXML private AnchorPane anchorPaneMapContainer;
    @FXML private ImageView imageViewMap;
    @FXML private Label labelWaypointName;
    @FXML private AnchorPane anchorPaneWaypointLayer;
    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (wPressed.get()) {
                imageViewMap.setLayoutY(imageViewMap.getLayoutY() + MOVEMENT_KEY_PRESSED);
            }
            if (sPressed.get()) {
                imageViewMap.setLayoutY(imageViewMap.getLayoutY() - MOVEMENT_KEY_PRESSED);
            }
            if (aPressed.get()) {
                imageViewMap.setLayoutX(imageViewMap.getLayoutX() + MOVEMENT_KEY_PRESSED);
            }
            if (dPressed.get()) {
                imageViewMap.setLayoutX(imageViewMap.getLayoutX() - MOVEMENT_KEY_PRESSED);
            }
            mapAtBorders();
        }
    };

    // Initialize
    @FXML
    private void initialize() {
        imageViewMap.setFitWidth(anchorPaneMapContainer.getWidth());
        imageViewMap.setFitHeight(anchorPaneMapContainer.getHeight());
        scaledWidth = imageViewMap.getFitWidth();
        scaledHeight = imageViewMap.getFitHeight();
        keyPressed.addListener(((observable, oldValue, newValue) -> {
            if (!oldValue) timer.start();
            else timer.stop();
        }));
        anchorPaneMapContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            imageViewMap.setFitWidth(newValue.doubleValue());
            scaledWidth = newValue.doubleValue() * imageViewMap.getScaleX();
        });
        anchorPaneMapContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            imageViewMap.setFitHeight(newValue.doubleValue());
            scaledHeight = newValue.doubleValue() * imageViewMap.getScaleY();
        });
        imageViewMap.layoutXProperty().addListener((observable, oldValue, newValue) -> anchorPaneWaypointLayer.setLayoutX(newValue.doubleValue()));
        imageViewMap.layoutYProperty().addListener((observable, oldValue, newValue) -> anchorPaneWaypointLayer.setLayoutY(newValue.doubleValue()));
        imageViewMap.scaleXProperty().addListener((observable, oldValue, newValue) -> anchorPaneWaypointLayer.setScaleX(newValue.doubleValue()));
        imageViewMap.scaleYProperty().addListener((observable, oldValue, newValue) -> anchorPaneWaypointLayer.setScaleY(newValue.doubleValue()));
        imageViewMap.fitWidthProperty().addListener((observable, oldValue, newValue) -> anchorPaneWaypointLayer.setPrefWidth(newValue.doubleValue()));
        imageViewMap.fitHeightProperty().addListener((observable, oldValue, newValue) -> anchorPaneWaypointLayer.setPrefHeight(newValue.doubleValue()));
        // fetchWaypointsFromDB();
    }

    // EDT
    public void mapKeyPressed(@NotNull final KeyEvent e) {
        if (e.getCode() == KeyCode.W) wPressed.set(true);
        if (e.getCode() == KeyCode.A) aPressed.set(true);
        if (e.getCode() == KeyCode.S) sPressed.set(true);
        if (e.getCode() == KeyCode.D) dPressed.set(true);
    }
    public void mapKeyReleased(@NotNull final KeyEvent e) {
        if (e.getCode() == KeyCode.W) wPressed.set(false);
        if (e.getCode() == KeyCode.A) aPressed.set(false);
        if (e.getCode() == KeyCode.S) sPressed.set(false);
        if (e.getCode() == KeyCode.D) dPressed.set(false);
    }
    public void mapContextMenu(@NotNull final ContextMenuEvent e) {
        ContextMenu contextMenu = new ContextMenu();
        Point2D pos = anchorPaneWaypointLayer.screenToLocal(e.getScreenX(), e.getScreenY());

        // Elements
        Menu elementMenu = new Menu("Aggiungi Elemento");
        MenuItem addItem = new MenuItem("Nuovo Oggetto");
        addItem.setOnAction(ev -> {
            try {
                Client.initPopupStage(SceneElementItem.getScene((String) null).getParent()).showAndWait();
            } catch (SQLException ex) {
                Logger.log(ex);
                new ErrorAlert("ERRORE", "Errore di Database", "Si e' verificato un'errore durante la comunicazione con il database");
            }
        });
        Menu equipmentMenu = new Menu("Nuovo Equipaggiamento");
        MenuItem addArmor = new MenuItem("Nuova Armatura");
        addArmor.setOnAction(ev -> {
            try {
                Client.initPopupStage(SceneElementArmor.getScene((String) null).getParent()).showAndWait();
            } catch (SQLException ex) {
                Logger.log(ex);
                new ErrorAlert("ERRORE", "Errore di Database", "Si e' verificato un'errore durante la comunicazione con il database");
            }
        });
        MenuItem addAddon = new MenuItem("Nuovo Addon");
        addAddon.setOnAction(ev -> {
            try {
                Client.initPopupStage(SceneElementAddon.getScene((String) null).getParent()).showAndWait();
            } catch (SQLException ex) {
                Logger.log(ex);
                new ErrorAlert("ERRORE", "Errore di Database", "Si e' verificato un'errore durante la comunicazione con il database");
            }
        });
        MenuItem addWeapon = new MenuItem("Nuova Arma");
        addWeapon.setOnAction(ev -> {
            /*
            try {
                Client.initPopupStage(SceneElementWeapon.getScene((String) null).getParent()).showAndWait();
            } catch (SQLException ex) {
                Logger.log(ex);
                new ErrorAlert("ERRORE", "Errore di Database", "Si e' verificato un'errore durante la comunicazione con il database");
            }*/
            Waypoint waypoint = new Waypoint("Test", Defs.Resources.SVG.SVG_WEAPON, pos);
            anchorPaneWaypointLayer.getChildren().add(waypoint);
            configureWaypointContenxtMenu(waypoint);
        });
        equipmentMenu.getItems().addAll(addArmor, addAddon, addWeapon);
        MenuItem addSpell = new MenuItem("Nuovo Incantesimo");
        addSpell.setOnAction(ev -> {
            try {
                Client.initPopupStage(SceneElementSpell.getScene((String) null).getParent()).showAndWait();
            } catch (SQLException ex) {
                Logger.log(ex);
                new ErrorAlert("ERRORE", "Errore di Database", "Si e' verificato un'errore durante la comunicazione con il database");
            }
        });
        elementMenu.getItems().addAll(addItem, equipmentMenu, addSpell);

        // Entities
        Menu entityMenu = new Menu("Aggiungi Entita'");
        MenuItem addPlayer = new TextFieldMenuItem("Nuovo Giocatore", "Nome Giocatore");
        MenuItem addNPC = new MenuItem("Nuovo NPC");
        MenuItem addEnemy = new MenuItem("Nuovo Nemico");
        Menu armyMenu = new Menu("Nuovo Esercito");
        MenuItem addAllyArmy = new MenuItem("Nuovo Esercito Alleato");
        MenuItem addEnemyArmy = new MenuItem("Nuovo Esercito Nemico");
        armyMenu.getItems().addAll(addAllyArmy, addEnemyArmy);
        entityMenu.getItems().addAll(addPlayer, addNPC, addEnemy, armyMenu);

        // Objectives
        Menu objectiveMenu = new Menu("Aggiungi Obiettivo");
        MenuItem addMainMission = new TextFieldMenuItem("Nuova Missione Principale", "Nome Missione");
        MenuItem addSecondaryMission = new TextFieldMenuItem("Nuova Missione Secondaria", "Nome Missione");
        MenuItem addNPCInteraction = new TextFieldMenuItem("Nuova Interazione NPC", "Nome NPC");
        objectiveMenu.getItems().addAll(addMainMission, addSecondaryMission, addNPCInteraction);

        // Points of Interest
        Menu pointsOfInterestMenu = new Menu("Aggiungi Punto di Interesse");
        MenuItem addShop = new TextFieldMenuItem("Nuovo Negozio", "Nome Negozio");
        MenuItem addTavern = new TextFieldMenuItem("Nuova Taverna", "Nome Taverna");
        MenuItem addOffice = new TextFieldMenuItem("Nuovo Ufficio", "Nome Ufficio");
        pointsOfInterestMenu.getItems().addAll(addShop, addTavern, addOffice);

        MenuItem resetMap = new MenuItem("Reimposta Mappa");
        resetMap.setOnAction(ev -> resetMapPositionAndScale());
        contextMenu.getItems().addAll(elementMenu, entityMenu, objectiveMenu, pointsOfInterestMenu, resetMap);
        contextMenu.setAutoHide(true);
        contextMenu.show(Client.getStage(), e.getScreenX(), e.getScreenY());
    }
    public void mapClicked() {
        imageViewMap.requestFocus();
    }
    public void mapMouseDragged(@NotNull final MouseEvent e) {
        double offsetX = e.getSceneX() - mapMovementOriginSceneX;
        double offsetY = e.getSceneY() - mapMovementOriginSceneY;
        double newLayoutX = mapMovementOriginLayoutX + offsetX;
        double newLayoutY = mapMovementOriginLayoutY + offsetY;
        imageViewMap.setLayoutX(newLayoutX);
        imageViewMap.setLayoutY(newLayoutY);
        mapAtBorders();
    }
    public void mapMousePressed(@NotNull final MouseEvent e) {
        mapMovementOriginSceneX = e.getSceneX();
        mapMovementOriginSceneY = e.getSceneY();
        mapMovementOriginLayoutX = imageViewMap.getLayoutX();
        mapMovementOriginLayoutY = imageViewMap.getLayoutY();
    }
    public void mapZoom(@NotNull final ScrollEvent e) {
        double zoomFactor = 1.01;
        double deltaY = e.getDeltaY();
        if (deltaY < 0) {
            zoomFactor = 0.95;
        }

        double potentialScaleX = imageViewMap.getScaleX() * zoomFactor;
        double potentialScaleY = imageViewMap.getScaleY() * zoomFactor;

        if (potentialScaleX >= 1 && potentialScaleY >= 1) {
            imageViewMap.setScaleX(potentialScaleX);
            imageViewMap.setScaleY(potentialScaleY);
        } else {
            imageViewMap.setScaleX(1);
            imageViewMap.setScaleY(1);
        }
        scaledWidth = imageViewMap.getFitWidth() * imageViewMap.getScaleX();
        scaledHeight = imageViewMap.getFitHeight() * imageViewMap.getScaleY();
        mapAtBorders();
    }

    // Waypoint Methods
    private void configureWaypointContenxtMenu(@NotNull Waypoint waypoint) {
        waypoint.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                waypoint.setScaleX(2);
                waypoint.setScaleY(2);
                labelWaypointName.setText(waypoint.getName());
                labelWaypointName.setVisible(true);
            } else {
                waypoint.setScaleX(1);
                waypoint.setScaleY(1);
                labelWaypointName.setText("");
                labelWaypointName.setVisible(false);
            }
        });
        waypoint.setOnScroll(ControllerSceneTabMap.this::mapZoom);
    }

    // Methods
    private double layoutToCornerX(double layoutX) {
        double widthIncrease = scaledWidth - imageViewMap.getFitWidth();
        return layoutX - (widthIncrease/2);
    }
    private double layoutToCornerY(double layoutY) {
        double heightIncrease = scaledHeight - imageViewMap.getFitHeight();
        return layoutY - (heightIncrease/2);
    }
    private double cornerToLayoutX(double cornerX) {
        double widthIncrease = scaledWidth - imageViewMap.getFitWidth();
        return cornerX + (widthIncrease/2);
    }
    private double cornerToLayoutY(double cornerY) {
        double heightIncrease = scaledHeight - imageViewMap.getFitHeight();
        return cornerY + (heightIncrease/2);
    }
    private void resetMapPositionAndScale() {
        imageViewMap.setLayoutX(0);
        imageViewMap.setLayoutY(0);
        imageViewMap.setScaleX(1);
        imageViewMap.setScaleY(1);
        scaledWidth = imageViewMap.getFitWidth();
        scaledHeight = imageViewMap.getFitHeight();
    }
    private void mapAtBorders() {
        double leftBound = 0;
        double rightBound = anchorPaneMapContainer.getWidth();
        double bottomBound =  anchorPaneMapContainer.getHeight();
        double widthDifference = scaledWidth - anchorPaneMapContainer.getWidth();
        double heightDifference = scaledHeight - anchorPaneMapContainer.getHeight();
        double topBound = 0;

        double cornerX = layoutToCornerX(imageViewMap.getLayoutX());
        double cornerY = layoutToCornerY(imageViewMap.getLayoutY());

        if (widthDifference <= 0) {
            if (cornerX < leftBound) {
                imageViewMap.setLayoutX(cornerToLayoutX(leftBound));
            }
            if (cornerX + scaledWidth > rightBound) {
                imageViewMap.setLayoutX(cornerToLayoutX(rightBound - scaledWidth));
            }
        } else {
            if (cornerX > leftBound) {
                imageViewMap.setLayoutX(cornerToLayoutX(leftBound));
            }
            if (cornerX + scaledWidth < rightBound) {
                imageViewMap.setLayoutX(cornerToLayoutX(rightBound - scaledWidth));
            }
        }

        if (heightDifference <= 0) {
            if (cornerY < topBound) {
                imageViewMap.setLayoutY(cornerToLayoutY(topBound));
            }
            if (cornerY + scaledHeight > bottomBound) {
                imageViewMap.setLayoutY(cornerToLayoutY(bottomBound - scaledHeight));
            }
        } else {
            if (cornerY > topBound) {
                imageViewMap.setLayoutY(cornerToLayoutY(topBound));
            }
            if (cornerY + scaledHeight < bottomBound) {
                imageViewMap.setLayoutY(cornerToLayoutY(bottomBound - scaledHeight));
            }
        }
    }
}
