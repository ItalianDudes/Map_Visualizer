package it.italiandudes.map_visualizer.master.javafx.controllers.elements;

import it.italiandudes.idl.common.ImageHandler;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.Client;
import it.italiandudes.map_visualizer.master.javafx.utils.JFXDefs;
import it.italiandudes.map_visualizer.master.javafx.alerts.ConfirmationAlert;
import it.italiandudes.map_visualizer.master.javafx.alerts.ErrorAlert;
import it.italiandudes.map_visualizer.master.javafx.alerts.InformationAlert;
import it.italiandudes.map_visualizer.master.javafx.utils.UIElementConfigurator;
import it.italiandudes.map_visualizer.master.utils.SheetDataHandler;
import it.italiandudes.map_visualizer.data.elements.Addon;
import it.italiandudes.map_visualizer.data.elements.Item;
import it.italiandudes.map_visualizer.data.enums.AddonSlot;
import it.italiandudes.map_visualizer.data.enums.Category;
import it.italiandudes.map_visualizer.data.enums.Rarity;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public final class ControllerSceneElementAddon {

    // Attributes
    private volatile boolean configurationComplete = false;
    private Addon addon = null;
    private JSONObject addonStructure = null;
    private String imageExtension = null;

    // Methods
    public void setAddon(@NotNull final Addon addon) {
        this.addon = addon;
    }
    public void setAddonStructure(@NotNull final JSONObject addonStructure) {
        this.addonStructure = addonStructure;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Graphics Elements
    @FXML
    private TextField textFieldName;
    @FXML private TextField textFieldWeight;
    @FXML private Spinner<Integer> spinnerQuantity;
    @FXML private ComboBox<String> comboBoxRarity;
    @FXML private TextField textFieldMR;
    @FXML private TextField textFieldMA;
    @FXML private TextField textFieldME;
    @FXML private TextField textFieldMO;
    @FXML private TextField textFieldMP;
    @FXML private TextArea textAreaDescription;
    @FXML private ImageView imageViewItem;
    @FXML private ComboBox<AddonSlot> comboBoxSlot;
    @FXML private TextField textFieldEffectCA;
    @FXML private TextField textFieldEffectLife;
    @FXML private TextField textFieldEffectLoad;
    @FXML private TextField textFieldEffectLifePerc;
    @FXML private TextField textFieldEffectLoadPerc;
    @FXML private TextArea textAreaOtherEffects;

    // Old Values
    private int oldValueQuantity = 0;

    // Initialize
    @FXML
    @SuppressWarnings("DuplicatedCode")
    private void initialize() {
        setOnChangeTriggers();
        onLostFocusFireActionEvent();
        imageViewItem.setImage(JFXDefs.AppInfo.LOGO);
        comboBoxRarity.setItems(FXCollections.observableList(Rarity.colorNames));
        comboBoxRarity.getSelectionModel().selectFirst();
        spinnerQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 1));
        spinnerQuantity.getEditor().setTextFormatter(UIElementConfigurator.configureNewIntegerTextFormatter());
        comboBoxRarity.buttonCellProperty().bind(Bindings.createObjectBinding(() -> {
            Rarity identifiedRarity = null;
            for (Rarity rarity : Rarity.values()) {
                if (rarity.getTextedRarity().equals(comboBoxRarity.getSelectionModel().getSelectedItem())) {
                    identifiedRarity = rarity;
                }
            }
            if (identifiedRarity == null) return null;
            final Color color = identifiedRarity.getColor();

            // Get the arrow button of the combo-box
            StackPane arrowButton = (StackPane) comboBoxRarity.lookup(".arrow-button");
            return new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setTextFill(Color.BLACK);
                    if (empty || item == null) {
                        setBackground(Background.EMPTY);
                        setText("");
                    } else {
                        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                        setText(item);
                    }
                    // Set the background of the arrow also
                    if (arrowButton != null)
                        arrowButton.setBackground(getBackground());
                }
            };
        }, comboBoxRarity.valueProperty()));
        comboBoxSlot.setItems(FXCollections.observableList(AddonSlot.ADDON_SLOTS));
        comboBoxSlot.getSelectionModel().selectFirst();
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        //noinspection StatementWithEmptyBody
                        while (!configurationComplete);
                        if (addon != null) initExistingAddon();
                        else if (addonStructure != null) initExistingAddon(addonStructure);
                        return null;
                    }
                };
            }
        }.start();
    }

    // OnChange Triggers Setter
    private void setOnChangeTriggers() {
        spinnerQuantity.getEditor().textProperty().addListener((observable -> validateQuantity()));
    }

    // Lost Focus On Action Fire Event
    private void onLostFocusFireActionEvent() {
        spinnerQuantity.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) validateQuantity();
        });
    }

    // EDT
    @SuppressWarnings("DuplicatedCode")
    private void validateQuantity() {
        try {
            int qty = Integer.parseInt(spinnerQuantity.getEditor().getText());
            if (qty < 0) throw new NumberFormatException();
            oldValueQuantity = qty;
            spinnerQuantity.getValueFactory().setValue(qty);
        } catch (NumberFormatException e) {
            spinnerQuantity.getValueFactory().setValue(oldValueQuantity);
            new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "La quantita' deve essere un numero intero maggiore o uguale a 0.");
        }
    }
    @FXML
    private void removeImage() {
        imageViewItem.setImage(JFXDefs.AppInfo.LOGO);
        imageExtension = null;
    }
    @FXML
    @SuppressWarnings("DuplicatedCode")
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un Contenuto Multimediale");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", Defs.Resources.SQL.SUPPORTED_IMAGE_EXTENSIONS));
        fileChooser.setInitialDirectory(new File(Defs.JAR_POSITION).getParentFile());
        File imagePath;
        try {
            imagePath = fileChooser.showOpenDialog(Client.getStage().getScene().getWindow());
        } catch (IllegalArgumentException e) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            imagePath = fileChooser.showOpenDialog(Client.getStage().getScene().getWindow());
        }
        if(imagePath!=null) {
            File finalImagePath = imagePath;
            Service<Void> imageReaderService = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() {
                            try {
                                BufferedImage img = ImageIO.read(finalImagePath);
                                if (img == null) {
                                    Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di Lettura", "Impossibile leggere il contenuto selezionato."));
                                    return null;
                                }
                                Platform.runLater(() -> imageViewItem.setImage(SwingFXUtils.toFXImage(img, null)));
                                imageExtension = ImageHandler.getImageExtension(finalImagePath.getAbsolutePath());
                            }catch (IOException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di Lettura", "Impossibile leggere il contenuto selezionato."));
                            }
                            return null;
                        }
                    };
                }
            };
            imageReaderService.start();
        }
    }
    @FXML
    private void backToSheet() {
        textFieldName.getScene().getWindow().hide();
    }
    @FXML
    private void save() {
        if (textFieldName.getText().replace(" ", "").isEmpty()) {
            new ErrorAlert("ERRORE", "Errore di Inserimento", "Non e' stato assegnato un nome all'oggetto.");
            return;
        }
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override @SuppressWarnings("DuplicatedCode")
                    protected Void call() {
                        try {
                            double weight;
                            try {
                                String textWeight = textFieldWeight.getText();
                                if (textWeight == null || textWeight.replace(" ", "").isEmpty()) {
                                    weight = 0;
                                } else {
                                    weight = Double.parseDouble(textFieldWeight.getText());
                                    if (weight < 0) throw new NumberFormatException("The weight is less than 0");
                                }
                            } catch (NumberFormatException e) {
                                Logger.log(e);
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di Inserimento", "Il peso deve essere un numero a virgola mobile positivo!"));
                                return null;
                            }

                            String oldName = null;
                            int mr, ma, me, mo, mp;
                            try {
                                String strMR = textFieldMR.getText();
                                if (strMR == null || strMR.replace(" ", "").isEmpty()) {
                                    mr = 0;
                                } else {
                                    mr = Integer.parseInt(strMR);
                                }
                                String strMA = textFieldMA.getText();
                                if (strMA == null || strMA.replace(" ", "").isEmpty()) {
                                    ma = 0;
                                } else {
                                    ma = Integer.parseInt(strMA);
                                }
                                String strME = textFieldME.getText();
                                if (strME == null || strME.replace(" ", "").isEmpty()) {
                                    me = 0;
                                } else {
                                    me = Integer.parseInt(strME);
                                }
                                String strMO = textFieldMO.getText();
                                if (strMO == null || strMO.replace(" ", "").isEmpty()) {
                                    mo = 0;
                                } else {
                                    mo = Integer.parseInt(strMO);
                                }
                                String strMP = textFieldMP.getText();
                                if (strMP == null || strMP.replace(" ", "").isEmpty()) {
                                    mp = 0;
                                } else {
                                    mp = Integer.parseInt(strMP);
                                }
                                if (mr < 0 || ma < 0 || me < 0 || mo < 0 || mp < 0) throw new NumberFormatException("A number is negative");
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di Inserimento", "Le valute devono essere dei numeri interi positivi!"));
                                return null;
                            }
                            int lifeEffect, loadEffect, caEffect;
                            double lifeEffectPerc, loadEffectPerc;
                            try {
                                lifeEffect = Integer.parseInt(textFieldEffectLife.getText());
                                loadEffect = Integer.parseInt(textFieldEffectLoad.getText());
                                caEffect = Integer.parseInt(textFieldEffectCA.getText());
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "Gli effetti sulla vita, sul carico e sulla CA devono essere dei numeri interi"));
                                return null;
                            }

                            try {
                                lifeEffectPerc = Double.parseDouble(textFieldEffectLifePerc.getText());
                                if (lifeEffectPerc <= -100) throw new NumberFormatException();
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "L'effetto percentuale sulla vita deve essere un numero intero o decimale (decimale con punto) maggiore di -100"));
                                return null;
                            }
                            try {
                                loadEffectPerc = Double.parseDouble(textFieldEffectLoadPerc.getText());
                                if (loadEffectPerc < -100) throw new NumberFormatException();
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "L'effetto percentuale sul carico deve essere un numero intero o decimale (decimale con punto) maggiore o uguale a di -100"));
                                return null;
                            }

                            String otherEffects = textAreaOtherEffects.getText();
                            if (addon == null) {
                                if (Addon.checkIfExist(textFieldName.getText())) {
                                    Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di Inserimento", "Esiste gia' qualcosa con questo nome registrato!"));
                                    return null;
                                }
                                Item item = new Item(
                                        null,
                                        imageViewItem.getImage(),
                                        imageExtension,
                                        textFieldName.getText(),
                                        mr,
                                        ma,
                                        me,
                                        mo,
                                        mp,
                                        textAreaDescription.getText(),
                                        comboBoxRarity.getSelectionModel().getSelectedItem(),
                                        Category.EQUIPMENT,
                                        weight,
                                        spinnerQuantity.getValue()
                                );
                                addon = new Addon(
                                        item,
                                        comboBoxSlot.getSelectionModel().getSelectedItem(),
                                        lifeEffect, lifeEffectPerc, loadEffect, loadEffectPerc,
                                        caEffect, otherEffects, false
                                );
                            } else {
                                assert addon.getEquipmentID()!=null;
                                assert addon.getAddonID()!=null;
                                oldName = addon.getName();
                                Item item = new Item(
                                        addon.getItemID(),
                                        imageViewItem.getImage(),
                                        imageExtension,
                                        textFieldName.getText(),
                                        mr,
                                        ma,
                                        me,
                                        mo,
                                        mp,
                                        textAreaDescription.getText(),
                                        comboBoxRarity.getSelectionModel().getSelectedItem(),
                                        Category.EQUIPMENT,
                                        weight,
                                        spinnerQuantity.getValue()
                                );
                                addon = new Addon(
                                        item,
                                        addon.getEquipmentID(),
                                        addon.getAddonID(),
                                        comboBoxSlot.getSelectionModel().getSelectedItem(),
                                        lifeEffect, lifeEffectPerc, loadEffect, loadEffectPerc,
                                        caEffect, otherEffects, addon.isEquipped()
                                );
                            }

                            addon.saveIntoDatabase(oldName);
                            Platform.runLater(() -> new InformationAlert("SUCCESSO", "Salvataggio dei Dati", "Salvataggio dei dati completato con successo!"));
                        } catch (Exception e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Salvataggio", "Si e' verificato un errore durante il salvataggio dei dati");
                                textFieldName.getScene().getWindow().hide();
                            });
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    @FXML
    private void exportAddonStructure() {
        if (textFieldName.getText().replace(" ", "").isEmpty()) {
            new ErrorAlert("ERRORE", "Errore di Inserimento", "Non e' stato assegnato un nome all'oggetto.");
            return;
        }
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override @SuppressWarnings("DuplicatedCode")
                    protected Void call() {
                        try {
                            double weight;
                            try {
                                String textWeight = textFieldWeight.getText();
                                if (textWeight == null || textWeight.replace(" ", "").isEmpty()) {
                                    weight = 0;
                                } else {
                                    weight = Double.parseDouble(textFieldWeight.getText());
                                    if (weight < 0) throw new NumberFormatException("The weight is less than 0");
                                }
                            } catch (NumberFormatException e) {
                                Logger.log(e);
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di Inserimento", "Il peso deve essere un numero a virgola mobile positivo!"));
                                return null;
                            }

                            int mr, ma, me, mo, mp;
                            try {
                                String strMR = textFieldMR.getText();
                                if (strMR == null || strMR.replace(" ", "").isEmpty()) {
                                    mr = 0;
                                } else {
                                    mr = Integer.parseInt(strMR);
                                }
                                String strMA = textFieldMA.getText();
                                if (strMA == null || strMA.replace(" ", "").isEmpty()) {
                                    ma = 0;
                                } else {
                                    ma = Integer.parseInt(strMA);
                                }
                                String strME = textFieldME.getText();
                                if (strME == null || strME.replace(" ", "").isEmpty()) {
                                    me = 0;
                                } else {
                                    me = Integer.parseInt(strME);
                                }
                                String strMO = textFieldMO.getText();
                                if (strMO == null || strMO.replace(" ", "").isEmpty()) {
                                    mo = 0;
                                } else {
                                    mo = Integer.parseInt(strMO);
                                }
                                String strMP = textFieldMP.getText();
                                if (strMP == null || strMP.replace(" ", "").isEmpty()) {
                                    mp = 0;
                                } else {
                                    mp = Integer.parseInt(strMP);
                                }
                                if (mr < 0 || ma < 0 || me < 0 || mo < 0 || mp < 0) throw new NumberFormatException("A number is negative");
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di Inserimento", "Le valute devono essere dei numeri interi positivi!"));
                                return null;
                            }
                            int lifeEffect, loadEffect, caEffect;
                            double lifeEffectPerc, loadEffectPerc;
                            try {
                                lifeEffect = Integer.parseInt(textFieldEffectLife.getText());
                                loadEffect = Integer.parseInt(textFieldEffectLoad.getText());
                                caEffect = Integer.parseInt(textFieldEffectCA.getText());
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "Gli effetti sulla vita, sul carico e sulla CA devono essere dei numeri interi"));
                                return null;
                            }

                            try {
                                lifeEffectPerc = Double.parseDouble(textFieldEffectLifePerc.getText());
                                if (lifeEffectPerc <= -100) throw new NumberFormatException();
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "L'effetto percentuale sulla vita deve essere un numero intero o decimale (decimale con punto) maggiore di -100"));
                                return null;
                            }
                            try {
                                loadEffectPerc = Double.parseDouble(textFieldEffectLoadPerc.getText());
                                if (loadEffectPerc < -100) throw new NumberFormatException();
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "L'effetto percentuale sul carico deve essere un numero intero o decimale (decimale con punto) maggiore o uguale a di -100"));
                                return null;
                            }

                            String otherEffects = textAreaOtherEffects.getText();

                            Item item = new Item(
                                    null,
                                    imageViewItem.getImage(),
                                    imageExtension,
                                    textFieldName.getText(),
                                    mr,
                                    ma,
                                    me,
                                    mo,
                                    mp,
                                    textAreaDescription.getText(),
                                    comboBoxRarity.getSelectionModel().getSelectedItem(),
                                    Category.EQUIPMENT,
                                    weight,
                                    spinnerQuantity.getValue()
                            );
                            Addon exportableAddon = new Addon(
                                    item,
                                    comboBoxSlot.getSelectionModel().getSelectedItem(),
                                    lifeEffect, lifeEffectPerc, loadEffect, loadEffectPerc,
                                    caEffect, otherEffects, false
                            );

                            String addonCode = exportableAddon.exportElement();
                            Platform.runLater(() -> {
                                if (new ConfirmationAlert("ESPORTAZIONE", "Esportazione dei Dati", "Codice elemento pronto, vuoi esportarlo su file?").result) {
                                    Platform.runLater(() -> SheetDataHandler.exportElementCodeIntoFile(addonCode));
                                } else {
                                    ClipboardContent content = new ClipboardContent();
                                    content.putString(addonCode);
                                    Client.getSystemClipboard().setContent(content);
                                    new InformationAlert("SUCCESSO", "Esportazione dei Dati", "Dati esportati con successo nella clipboard di sistema!");
                                }
                            });
                        } catch (Exception e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Esportazione", "Si e' verificato un errore durante l'esportazione dei dati");
                                textFieldName.getScene().getWindow().hide();
                            });
                        }
                        return null;
                    }
                };
            }
        }.start();
    }

    // Methods
    private void initExistingAddon(@NotNull final JSONObject addonStructure) {
        try {
            Addon tempAddon = new Addon(addonStructure);

            imageExtension = tempAddon.getImageExtension();
            int CC = tempAddon.getCostCopper();
            int CP = CC / 1000;
            CC -= CP * 1000;
            int CG = CC / 100;
            CC -= CG * 100;
            int CE = CC / 50;
            CC -= CE * 50;
            int CS = CC / 10;
            CC -= CS * 10;

            BufferedImage bufferedImage = null;
            try {
                if (tempAddon.getBase64image() != null && imageExtension != null) {
                    byte[] imageBytes = Base64.getDecoder().decode(tempAddon.getBase64image());
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
                    bufferedImage = ImageIO.read(imageStream);
                } else if (tempAddon.getBase64image() != null && imageExtension == null) {
                    throw new IllegalArgumentException("Image without declared extension");
                }
            } catch (IllegalArgumentException e) {
                Logger.log(e);
                tempAddon.setBase64image(null);
                tempAddon.setImageExtension(null);
                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di lettura", "L'immagine ricevuta dal database non è leggibile"));
                return;
            }

            int finalCC = CC;
            BufferedImage finalBufferedImage = bufferedImage;

            Platform.runLater(() -> {
                textFieldName.setText(tempAddon.getName());
                textFieldWeight.setText(String.valueOf(tempAddon.getWeight()));
                comboBoxRarity.getSelectionModel().select(tempAddon.getRarity().getTextedRarity());
                textFieldMR.setText(String.valueOf(finalCC));
                textFieldMA.setText(String.valueOf(CS));
                textFieldME.setText(String.valueOf(CE));
                textFieldMO.setText(String.valueOf(CG));
                textFieldMP.setText(String.valueOf(CP));
                textAreaDescription.setText(tempAddon.getDescription());
                if (finalBufferedImage != null && imageExtension != null) {
                    imageViewItem.setImage(SwingFXUtils.toFXImage(finalBufferedImage, null));
                } else {
                    imageViewItem.setImage(JFXDefs.AppInfo.LOGO);
                }
                spinnerQuantity.getValueFactory().setValue(tempAddon.getQuantity());
                comboBoxSlot.getSelectionModel().select(tempAddon.getSlot());
                textFieldEffectCA.setText(String.valueOf(tempAddon.getCaEffect()));
                textFieldEffectLife.setText(String.valueOf(tempAddon.getLifeEffect()));
                textFieldEffectLifePerc.setText(String.valueOf(tempAddon.getLifePercentageEffect()));
                textFieldEffectLoad.setText(String.valueOf(tempAddon.getLoadEffect()));
                textFieldEffectLoadPerc.setText(String.valueOf(tempAddon.getLoadPercentageEffect()));
                textAreaOtherEffects.setText(tempAddon.getOtherEffects());
            });
        } catch (Exception e) {
            Logger.log(e);
            Platform.runLater(() -> {
                new ErrorAlert("ERRORE", "Errore di Importazione", "La struttura dei dati non e' valida.");
                textFieldName.getScene().getWindow().hide();
            });
        }
    }
    private void initExistingAddon() {
        try {
            imageExtension = addon.getImageExtension();
            int CC = addon.getCostCopper();
            int CP = CC / 1000;
            CC -= CP * 1000;
            int CG = CC / 100;
            CC -= CG * 100;
            int CE = CC / 50;
            CC -= CE * 50;
            int CS = CC / 10;
            CC -= CS * 10;

            BufferedImage bufferedImage = null;
            try {
                if (addon.getBase64image() != null && imageExtension != null) {
                    byte[] imageBytes = Base64.getDecoder().decode(addon.getBase64image());
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
                    bufferedImage = ImageIO.read(imageStream);
                } else if (addon.getBase64image() != null && imageExtension == null) {
                    throw new IllegalArgumentException("Image without declared extension");
                }
            } catch (IllegalArgumentException e) {
                Logger.log(e);
                addon.setBase64image(null);
                addon.setImageExtension(null);
                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di lettura", "L'immagine ricevuta dal database non è leggibile"));
                return;
            }

            int finalCC = CC;
            BufferedImage finalBufferedImage = bufferedImage;
            Platform.runLater(() -> {
                textFieldName.setText(addon.getName());
                textFieldWeight.setText(String.valueOf(addon.getWeight()));
                comboBoxRarity.getSelectionModel().select(addon.getRarity().getTextedRarity());
                textFieldMR.setText(String.valueOf(finalCC));
                textFieldMA.setText(String.valueOf(CS));
                textFieldME.setText(String.valueOf(CE));
                textFieldMO.setText(String.valueOf(CG));
                textFieldMP.setText(String.valueOf(CP));
                textAreaDescription.setText(addon.getDescription());
                if (finalBufferedImage != null && imageExtension != null) {
                    imageViewItem.setImage(SwingFXUtils.toFXImage(finalBufferedImage, null));
                } else {
                    imageViewItem.setImage(JFXDefs.AppInfo.LOGO);
                }
                spinnerQuantity.getValueFactory().setValue(addon.getQuantity());
                comboBoxSlot.getSelectionModel().select(addon.getSlot());
                textFieldEffectCA.setText(String.valueOf(addon.getCaEffect()));
                textFieldEffectLife.setText(String.valueOf(addon.getLifeEffect()));
                textFieldEffectLifePerc.setText(String.valueOf(addon.getLifePercentageEffect()));
                textFieldEffectLoad.setText(String.valueOf(addon.getLoadEffect()));
                textFieldEffectLoadPerc.setText(String.valueOf(addon.getLoadPercentageEffect()));
                textAreaOtherEffects.setText(addon.getOtherEffects());
            });
        } catch (Exception e) {
            Logger.log(e);
            Platform.runLater(() -> {
                new ErrorAlert("ERRORE", "Errore di Lettura", "Impossibile leggere l'elemento dal database");
                textFieldName.getScene().getWindow().hide();
            });
        }
    }
}
