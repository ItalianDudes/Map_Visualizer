package it.italiandudes.map_visualizer.master.javafx.controllers.tabs;

import it.italiandudes.map_visualizer.master.javafx.Client;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;

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
    @FXML private AnchorPane anchorPaneWaypointLayer;
    @FXML private Label labelWaypointName;
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
        Menu addWaypointMenu = new Menu("Aggiungi Waypoint");
        TextField nameField = new TextField();
        nameField.setPromptText("Nome");
        MenuItem addWaypointOption = new MenuItem();
        addWaypointOption.setGraphic(nameField);
        addWaypointOption.setOnAction(event -> {
            // addWaypoint(nameField.getText(), e.getScreenX(), e.getScreenY());
            nameField.clear();
        });
        addWaypointMenu.getItems().add(addWaypointOption);
        MenuItem resetMap = new MenuItem("Reimposta Mappa");
        resetMap.setOnAction(ev -> resetMapPositionAndScale());
        contextMenu.getItems().addAll(addWaypointMenu, resetMap);
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
