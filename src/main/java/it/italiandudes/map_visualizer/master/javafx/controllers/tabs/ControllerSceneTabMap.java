package it.italiandudes.map_visualizer.master.javafx.controllers.tabs;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.data.enums.WaypointType;
import it.italiandudes.map_visualizer.master.javafx.Client;
import it.italiandudes.map_visualizer.master.javafx.alerts.ErrorAlert;
import it.italiandudes.map_visualizer.master.javafx.components.TextFieldMenuItem;
import it.italiandudes.map_visualizer.master.javafx.components.Waypoint;
import it.italiandudes.map_visualizer.master.javafx.controllers.ControllerSceneMapSheet;
import it.italiandudes.map_visualizer.master.javafx.scenes.elements.*;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings({"DuplicatedCode", "ExtractMethodRecommender"})
public final class ControllerSceneTabMap {

    // Attributes
    private ControllerSceneMapSheet controllerSceneMapSheet;
    private volatile boolean configurationComplete = false;

    // Methods
    public void setControllerSceneMapSheet(@NotNull final ControllerSceneMapSheet controllerSceneMapSheet) {
        this.controllerSceneMapSheet = controllerSceneMapSheet;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

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
        fetchWaypointsFromDB();
    }

    // Waypoint Fetcher
    public void fetchWaypointsFromDB() {
        anchorPaneWaypointLayer.getChildren().clear();
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        String query = "SELECT * FROM waypoints;";
                        try (PreparedStatement ps = DBManager.preparedStatement(query)) {
                            if (ps == null) throw new SQLException("Prepared statement is null");
                            ResultSet result = ps.executeQuery();
                            while (result.next()) {
                                Waypoint waypoint = new Waypoint(result.getString("name"), WaypointType.values()[result.getInt("type")], new Point2D(result.getDouble("center_x"), result.getDouble("center_y")));
                                Platform.runLater(() -> anchorPaneWaypointLayer.getChildren().add(waypoint));
                                configureElementWaypointContextMenu(waypoint);
                            }
                        } catch (SQLException e) {
                            DBManager.showDatabaseErrorMessageAndBackToMenu(e);
                        }
                        return null;
                    }
                };
            }
        }.start();
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
            Waypoint waypoint = new Waypoint(WaypointType.ELEMENT_ITEM, pos);
            Client.initPopupStage(SceneElementItem.getScene(waypoint).getParent()).showAndWait();
            if (waypoint.getName().replace("\t", "").replace(" ", "").isEmpty()) return;
            anchorPaneWaypointLayer.getChildren().add(waypoint);
            configureElementWaypointContextMenu(waypoint);
            controllerSceneMapSheet.getControllerTabElements().search();
        });
        Menu equipmentMenu = new Menu("Nuovo Equipaggiamento");
        MenuItem addArmor = new MenuItem("Nuova Armatura");
        addArmor.setOnAction(ev -> {
            Waypoint waypoint = new Waypoint(WaypointType.ELEMENT_ARMOR, pos);
            Client.initPopupStage(SceneElementArmor.getScene(waypoint).getParent()).showAndWait();
            if (waypoint.getName().replace("\t", "").replace(" ", "").isEmpty()) return;
            anchorPaneWaypointLayer.getChildren().add(waypoint);
            configureElementWaypointContextMenu(waypoint);
            controllerSceneMapSheet.getControllerTabElements().search();
        });
        MenuItem addAddon = new MenuItem("Nuovo Addon");
        addAddon.setOnAction(ev -> {
            Waypoint waypoint = new Waypoint(WaypointType.ELEMENT_ADDON, pos);
            Client.initPopupStage(SceneElementAddon.getScene(waypoint).getParent()).showAndWait();
            if (waypoint.getName().replace("\t", "").replace(" ", "").isEmpty()) return;
            anchorPaneWaypointLayer.getChildren().add(waypoint);
            configureElementWaypointContextMenu(waypoint);
            controllerSceneMapSheet.getControllerTabElements().search();
        });
        MenuItem addWeapon = new MenuItem("Nuova Arma");
        addWeapon.setOnAction(ev -> {
            Waypoint waypoint = new Waypoint(WaypointType.ELEMENT_WEAPON, pos);
            Client.initPopupStage(SceneElementWeapon.getScene(waypoint).getParent()).showAndWait();
            if (waypoint.getName().replace("\t", "").replace(" ", "").isEmpty()) return;
            anchorPaneWaypointLayer.getChildren().add(waypoint);
            configureElementWaypointContextMenu(waypoint);
            controllerSceneMapSheet.getControllerTabElements().search();
        });
        equipmentMenu.getItems().addAll(addArmor, addAddon, addWeapon);
        MenuItem addSpell = new MenuItem("Nuovo Incantesimo");
        addSpell.setOnAction(ev -> {
            Waypoint waypoint = new Waypoint(WaypointType.ELEMENT_SPELL, pos);
            Client.initPopupStage(SceneElementSpell.getScene(waypoint).getParent()).showAndWait();
            if (waypoint.getName().replace("\t", "").replace(" ", "").isEmpty()) return;
            anchorPaneWaypointLayer.getChildren().add(waypoint);
            configureElementWaypointContextMenu(waypoint);
            controllerSceneMapSheet.getControllerTabElements().search();
        });
        elementMenu.getItems().addAll(addItem, equipmentMenu, addSpell);

        // Entities
        Menu entityMenu = new Menu("Aggiungi Entita'");
        TextFieldMenuItem addPlayer = new TextFieldMenuItem("Nuovo Giocatore", "Nome Giocatore");
        TextFieldMenuItem addNPC = new TextFieldMenuItem("Nuovo NPC", "Nome NPC");
        TextFieldMenuItem addEnemy = new TextFieldMenuItem("Nuovo Nemico", "Nome Nemico");
        TextFieldMenuItem addStrongEnemy = new TextFieldMenuItem("Nuovo Nemico Potente", "Nome Nemico Potente");
        TextFieldMenuItem addBoss = new TextFieldMenuItem("Nuovo Boss", "Nome Boss");
        entityMenu.getItems().addAll(addPlayer, addNPC, addEnemy, addStrongEnemy, addBoss);

        // Objectives
        Menu objectiveMenu = new Menu("Aggiungi Obiettivo");
        MenuItem addMission = new TextFieldMenuItem("Nuova Missione", "Nome Missione");
        objectiveMenu.getItems().addAll(addMission);

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
    private void configureElementWaypointContextMenu(@NotNull Waypoint waypoint) {
        waypoint.hoverProperty().addListener((observable, oldValue, newValue) -> {
            waypoint.setZoom(newValue);
            if (newValue) {
                labelWaypointName.setText(waypoint.getName());
                labelWaypointName.setVisible(true);
            } else {
                labelWaypointName.setText("");
                labelWaypointName.setVisible(false);
            }
        });
        waypoint.setOnScroll(ControllerSceneTabMap.this::mapZoom);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editWaypoint = new MenuItem("Modifica");
        editWaypoint.setOnAction(ev -> controllerSceneMapSheet.getControllerTabElements().editElement(waypoint));
        MenuItem deleteWaypoint = new MenuItem("Elimina");
        deleteWaypoint.setOnAction(ev -> new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() {
                            String query = "DELETE FROM waypoints WHERE id=?;";
                            assert waypoint.getWaypointID() != null;
                            try (PreparedStatement ps = DBManager.preparedStatement(query)) {
                                if (ps == null) throw new SQLException("Prepared statement is null");
                                ps.setInt(1, waypoint.getWaypointID());
                                ps.executeUpdate();
                                Platform.runLater(() -> anchorPaneWaypointLayer.getChildren().remove(waypoint));
                            } catch (SQLException e) {
                                DBManager.showDatabaseErrorMessageAndBackToMenu(e);
                            }
                            return null;
                        }
                    };
                }
            }.start());
        contextMenu.getItems().addAll(editWaypoint, deleteWaypoint);
        contextMenu.setAutoHide(true);
        waypoint.setOnContextMenuRequested(e -> contextMenu.show(Client.getStage(), e.getScreenX(), e.getScreenY()));
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
