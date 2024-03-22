package it.italiandudes.map_visualizer.master.javafx.controllers.tabs;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.data.elements.Equipment;
import it.italiandudes.map_visualizer.data.elements.Item;
import it.italiandudes.map_visualizer.data.enums.Category;
import it.italiandudes.map_visualizer.data.enums.EquipmentType;
import it.italiandudes.map_visualizer.data.enums.Rarity;
import it.italiandudes.map_visualizer.master.javafx.Client;
import it.italiandudes.map_visualizer.master.javafx.alerts.ErrorAlert;
import it.italiandudes.map_visualizer.master.javafx.components.SceneController;
import it.italiandudes.map_visualizer.master.javafx.components.Waypoint;
import it.italiandudes.map_visualizer.master.javafx.controllers.ControllerSceneMapSheet;
import it.italiandudes.map_visualizer.master.javafx.scenes.SceneMainMenu;
import it.italiandudes.map_visualizer.master.javafx.scenes.elements.*;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
public final class ControllerSceneTabElements {

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

    // Graphic Elements
    @FXML private TextField textFieldSearchBar;
    @FXML private ComboBox<Category> comboBoxCategory;
    @FXML private ComboBox<EquipmentType> comboBoxEquipmentType;
    @FXML private TableView<Item> tableViewInventory;
    @FXML private CheckBox checkBoxShowOwned;
    @FXML private TableColumn<Item, Integer> tableColumnInventoryID;
    @FXML private TableColumn<Item, String> tableColumnInventoryName;
    @FXML private TableColumn<Item, Rarity> tableColumnInventoryRarity;
    @FXML private TableColumn<Item, Double> tableColumnInventoryWeight;
    @FXML private TableColumn<Item, Integer> tableColumnInventoryCostMR;
    @FXML private TableColumn<Item, Integer> tableColumnInventoryQuantity;

    // Initialize
    @FXML
    private void initialize() {
        tableColumnInventoryID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        tableColumnInventoryName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnInventoryRarity.setCellValueFactory(new PropertyValueFactory<>("rarity"));
        tableColumnInventoryWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        tableColumnInventoryCostMR.setCellValueFactory(new PropertyValueFactory<>("costCopper"));
        tableColumnInventoryQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        comboBoxCategory.setItems(FXCollections.observableList(Category.categories));
        comboBoxEquipmentType.setItems(FXCollections.observableList(EquipmentType.types));
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        //noinspection StatementWithEmptyBody
                        while (!configurationComplete);
                        search();
                        return null;
                    }
                };
            }
        }.start();
    }

    // EDT
    @FXML
    public void search() {
        Category selectedCategory = comboBoxCategory.getSelectionModel().getSelectedItem();
        comboBoxEquipmentType.setDisable(selectedCategory == null || !selectedCategory.equals(Category.EQUIPMENT));
        EquipmentType equipmentType = comboBoxEquipmentType.getSelectionModel().getSelectedItem();
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            String query;
                            PreparedStatement ps;
                            if (selectedCategory != null) {
                                if (selectedCategory.equals(Category.EQUIPMENT) && equipmentType != null) {
                                    query = "SELECT i.id AS id FROM items AS i JOIN equipments AS e ON i.id = e.item_id WHERE i.name LIKE '%" + textFieldSearchBar.getText() + "%' AND i.category=? AND e.type=?" + (checkBoxShowOwned.isSelected()?" AND i.quantity>0;":";");
                                    ps = DBManager.preparedStatement(query);
                                    if (ps == null) {
                                        Platform.runLater(() -> {
                                            new ErrorAlert("ERRORE", "Errore di Connessione al database", "Non e' stato possibile consultare il database");
                                            Client.setScene(SceneMainMenu.getScene().getParent());
                                        });
                                        return null;
                                    }
                                    ps.setInt(1, selectedCategory.getDatabaseValue());
                                    ps.setInt(2, equipmentType.getDatabaseValue());
                                } else {
                                    query = "SELECT id FROM items WHERE name LIKE '%" + textFieldSearchBar.getText() + "%' AND category=?" + (checkBoxShowOwned.isSelected()?" AND quantity>0;":";");
                                    ps = DBManager.preparedStatement(query);
                                    if (ps == null) {
                                        Platform.runLater(() -> {
                                            new ErrorAlert("ERRORE", "Errore di Connessione al database", "Non e' stato possibile consultare il database");
                                            Client.setScene(SceneMainMenu.getScene().getParent());
                                        });
                                        return null;
                                    }
                                    ps.setInt(1, selectedCategory.getDatabaseValue());
                                }
                            } else {
                                query = "SELECT id FROM items WHERE name LIKE '%"+textFieldSearchBar.getText()+"%'" + (checkBoxShowOwned.isSelected()?" AND quantity>0;":";");
                                ps = DBManager.preparedStatement(query);
                                if (ps == null) {
                                    Platform.runLater(() -> {
                                        new ErrorAlert("ERRORE", "Errore di Connessione al database", "Non e' stato possibile consultare il database");
                                        Client.setScene(SceneMainMenu.getScene().getParent());
                                    });
                                    return null;
                                }
                            }

                            ResultSet result = ps.executeQuery();

                            ArrayList<Item> resultList = new ArrayList<>();

                            while (result.next()) {
                                resultList.add(new Item(result.getInt("id")));
                            }

                            ps.close();
                            Platform.runLater(() -> tableViewInventory.setItems(FXCollections.observableList(resultList)));
                        } catch (Exception e) {
                            Logger.log(e);
                            new ErrorAlert("ERRORE", "ERRORE DI CONNESSIONE", "Si e' verificato un errore durante la comunicazione con il database.");
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    @FXML
    private void resetSearchBarAndCategory() {
        textFieldSearchBar.setText("");
        comboBoxCategory.getSelectionModel().clearSelection();
        comboBoxEquipmentType.getSelectionModel().clearSelection();
        comboBoxEquipmentType.setDisable(true);
        search();
    }
    @Nullable
    private SceneController selectEquipmentScene(@Nullable final Item element) throws SQLException {
        EquipmentType equipmentType;
        if (element != null) {
            try {
                equipmentType = new Equipment(element.getName()).getType();
            } catch (SQLException e) {
                new ErrorAlert("ERRORE", "ERRORE COL DATABASE", "Si e' verificato un errore durante l'interrogazione del database.");
                return null;
            }
        } else {
            equipmentType = comboBoxEquipmentType.getSelectionModel().getSelectedItem();
            if (equipmentType == null) {
                new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "Per aggiungere dell'equipaggiamento devi prima selezionare il tipo.");
                return null;
            }
        }
        String elementName = element != null ? element.getName() : null;
        switch (equipmentType) {
            case ARMOR:
                return SceneElementArmor.getScene(elementName);
            case WEAPON:
                return SceneElementWeapon.getScene(elementName);
            case ADDON:
                return SceneElementAddon.getScene(elementName);
            default: // Invalid
                new ErrorAlert("ERRORE", "ERRORE NEL DATABASE", "L'elemento selezionato non possiede una categoria equipaggiamento valida.");
                return null;
        }
    }
    @FXML
    private void editElement() {
        Item element = tableViewInventory.getSelectionModel().getSelectedItem();
        if (element == null) return;
        SceneController scene;
        try {
            switch (element.getCategory()) {
                case ITEM:
                    scene = SceneElementItem.getScene(element.getName());
                    break;
                case EQUIPMENT:
                    scene = selectEquipmentScene(element);
                    break;
                case SPELL:
                    scene = SceneElementSpell.getScene(element.getName());
                    break;
                default: // Invalid
                    new ErrorAlert("ERRORE", "ERRORE NEL DATABASE", "L'elemento selezionato non possiede una categoria valida o non e' stata ancora implementata nell'applicazione.");
                    return;
            }
        } catch (SQLException e) {
            new ErrorAlert("ERRORE", "ERRORE NEL DATABASE", "L'elemento cercato non e' presente o il database e' danneggiato.");
            return;
        }
        if (scene == null) return;
        Stage popupStage = Client.initPopupStage(scene.getParent());
        popupStage.showAndWait();
        search();
        controllerSceneMapSheet.getControllerTabMap().fetchWaypointsFromDB();
    }
    public void editElement(@NotNull final Waypoint waypoint) {
        List<Item> matches = tableViewInventory.getItems().stream().filter(e -> waypoint.getName().equals(e.getName())).collect(Collectors.toList());
        if (matches.size() != 1) return;
        Item item = matches.get(0);
        tableViewInventory.getSelectionModel().select(item);
        editElement();
    }
    @FXML
    private void detectEnterOnRow(@NotNull final KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && tableViewInventory.getSelectionModel().getSelectedItem() != null) editElement();
    }
    @FXML
    private void doubleClickEdit(@NotNull final MouseEvent event) {
        if (event.getClickCount() >= 2) editElement();
    }
}
