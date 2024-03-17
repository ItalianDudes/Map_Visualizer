package it.italiandudes.map_visualizer.client.javafx.controllers.elements;

import it.italiandudes.idl.common.ImageHandler;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.client.javafx.Client;
import it.italiandudes.map_visualizer.client.javafx.JFXDefs;
import it.italiandudes.map_visualizer.client.javafx.alerts.ConfirmationAlert;
import it.italiandudes.map_visualizer.client.javafx.alerts.ErrorAlert;
import it.italiandudes.map_visualizer.client.javafx.alerts.InformationAlert;
import it.italiandudes.map_visualizer.client.javafx.utils.UIElementConfigurator;
import it.italiandudes.map_visualizer.client.utils.SheetDataHandler;
import it.italiandudes.map_visualizer.data.elements.Armor;
import it.italiandudes.map_visualizer.data.elements.Item;
import it.italiandudes.map_visualizer.data.enums.ArmorSlot;
import it.italiandudes.map_visualizer.data.enums.ArmorWeightCategory;
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

public final class ControllerSceneElementArmor {

    // Attributes
    private volatile boolean configurationComplete = false;
    private Armor armor = null;
    private JSONObject armorStructure = null;
    private String imageExtension = null;

    // Methods
    public void setArmor(@NotNull final Armor armor) {
        this.armor = armor;
    }
    public void setArmorStructure(@NotNull final JSONObject armorStructure) {
        this.armorStructure = armorStructure;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Graphics Elements
    @FXML private TextField textFieldName;
    @FXML private TextField textFieldWeight;
    @FXML private ComboBox<ArmorWeightCategory> comboBoxWeightCategory;
    @FXML private Spinner<Integer> spinnerQuantity;
    @FXML private ComboBox<String> comboBoxRarity;
    @FXML private TextField textFieldMR;
    @FXML private TextField textFieldMA;
    @FXML private TextField textFieldME;
    @FXML private TextField textFieldMO;
    @FXML private TextField textFieldMP;
    @FXML private TextArea textAreaDescription;
    @FXML private ImageView imageViewItem;
    @FXML private ComboBox<ArmorSlot> comboBoxSlot;
    @FXML private TextField textFieldEffectCA;
    @FXML private TextField textFieldEffectLife;
    @FXML private TextField textFieldEffectLoad;
    @FXML private TextField textFieldEffectLifePerc;
    @FXML private TextField textFieldEffectLoadPerc;
    @FXML private TextArea textAreaOtherEffects;

    // Old Values
    private int oldValueQuantity = 0;

    // Initialize
    @FXML @SuppressWarnings("DuplicatedCode")
    private void initialize() {
        setOnChangeTriggers();
        onLostFocusFireActionEvent();
        imageViewItem.setImage(JFXDefs.AppInfo.LOGO);
        comboBoxRarity.setItems(FXCollections.observableList(Rarity.colorNames));
        comboBoxRarity.getSelectionModel().selectFirst();
        comboBoxWeightCategory.setItems(FXCollections.observableList(ArmorWeightCategory.ARMOR_WEIGHT_CATEGORIES));
        comboBoxWeightCategory.getSelectionModel().selectFirst();
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
        comboBoxSlot.setItems(FXCollections.observableList(ArmorSlot.ARMOR_SLOTS));
        comboBoxSlot.getSelectionModel().selectFirst();
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        //noinspection StatementWithEmptyBody
                        while (!configurationComplete);
                        if (armor != null) initExistingArmor();
                        else if (armorStructure != null) initExistingArmor(armorStructure);
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
    @FXML @SuppressWarnings("DuplicatedCode")
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
    private void exportArmorStructure() {
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
                            ArmorWeightCategory weightCategory = comboBoxWeightCategory.getSelectionModel().getSelectedItem();

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
                                loadEffectPerc = Double.parseDouble(textFieldEffectLoadPerc.getText());
                                if (lifeEffectPerc <= -100 || loadEffectPerc < -100) throw new NumberFormatException();
                            } catch (NumberFormatException e) {
                                Platform.runLater(() -> new ErrorAlert("ERRORE", "ERRORE DI INSERIMENTO", "Gli effetti percentuale sulla vita e sul carico devono essere dei numeri interi o decimali (decimale con punto) maggiori di -100"));
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
                            Armor exportableArmor = new Armor(
                                    item,
                                    comboBoxSlot.getSelectionModel().getSelectedItem(),
                                    lifeEffect, lifeEffectPerc, loadEffect, loadEffectPerc,
                                    caEffect, otherEffects, weightCategory, false
                            );

                            String armorCode = exportableArmor.exportElement();
                            Platform.runLater(() -> {
                                if (new ConfirmationAlert("ESPORTAZIONE", "Esportazione dei Dati", "Codice elemento pronto, vuoi esportarlo su file?").result) {
                                    Platform.runLater(() -> SheetDataHandler.exportElementCodeIntoFile(armorCode));
                                } else {
                                    ClipboardContent content = new ClipboardContent();
                                    content.putString(armorCode);
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
                            ArmorWeightCategory weightCategory = comboBoxWeightCategory.getSelectionModel().getSelectedItem();
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
                            if (armor == null) {
                                if (Armor.checkIfExist(textFieldName.getText())) {
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
                                armor = new Armor(
                                        item,
                                        comboBoxSlot.getSelectionModel().getSelectedItem(),
                                        lifeEffect, lifeEffectPerc, loadEffect, loadEffectPerc,
                                        caEffect, otherEffects, weightCategory, false
                                );
                            } else {
                                assert armor.getEquipmentID()!=null;
                                assert armor.getArmorID()!=null;
                                oldName = armor.getName();
                                Item item = new Item(
                                        armor.getItemID(),
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
                                armor = new Armor(
                                        item,
                                        armor.getEquipmentID(),
                                        armor.getArmorID(),
                                        comboBoxSlot.getSelectionModel().getSelectedItem(),
                                        lifeEffect, lifeEffectPerc, loadEffect, loadEffectPerc,
                                        caEffect, otherEffects, weightCategory, armor.isEquipped()
                                );
                            }

                            armor.saveIntoDatabase(oldName);
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
    // Methods
    private void initExistingArmor(@NotNull final JSONObject armorStructure) {
        try {
            Armor tempArmor = new Armor(armorStructure);
            imageExtension = tempArmor.getImageExtension();
            int CC = tempArmor.getCostCopper();
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
                if (tempArmor.getBase64image() != null && imageExtension != null) {
                    byte[] imageBytes = Base64.getDecoder().decode(tempArmor.getBase64image());
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
                    bufferedImage = ImageIO.read(imageStream);
                } else if (tempArmor.getBase64image() != null && imageExtension == null) {
                    throw new IllegalArgumentException("Image without declared extension");
                }
            } catch (IllegalArgumentException e) {
                Logger.log(e);
                tempArmor.setBase64image(null);
                tempArmor.setImageExtension(null);
                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di lettura", "L'immagine ricevuta dal database non è leggibile"));
                return;
            }

            int finalCC = CC;
            BufferedImage finalBufferedImage = bufferedImage;

            Platform.runLater(() -> {
                textFieldName.setText(tempArmor.getName());
                textFieldWeight.setText(String.valueOf(tempArmor.getWeight()));
                comboBoxRarity.getSelectionModel().select(tempArmor.getRarity().getTextedRarity());
                textFieldMR.setText(String.valueOf(finalCC));
                textFieldMA.setText(String.valueOf(CS));
                textFieldME.setText(String.valueOf(CE));
                textFieldMO.setText(String.valueOf(CG));
                textFieldMP.setText(String.valueOf(CP));
                textAreaDescription.setText(tempArmor.getDescription());
                if (finalBufferedImage != null && imageExtension != null) {
                    imageViewItem.setImage(SwingFXUtils.toFXImage(finalBufferedImage, null));
                } else {
                    imageViewItem.setImage(JFXDefs.AppInfo.LOGO);
                }
                spinnerQuantity.getValueFactory().setValue(tempArmor.getQuantity());
                comboBoxSlot.getSelectionModel().select(tempArmor.getSlot());
                textFieldEffectCA.setText(String.valueOf(tempArmor.getCaEffect()));
                textFieldEffectLife.setText(String.valueOf(tempArmor.getLifeEffect()));
                textFieldEffectLifePerc.setText(String.valueOf(tempArmor.getLifePercentageEffect()));
                textFieldEffectLoad.setText(String.valueOf(tempArmor.getLoadEffect()));
                textFieldEffectLoadPerc.setText(String.valueOf(tempArmor.getLoadPercentageEffect()));
                textAreaOtherEffects.setText(tempArmor.getOtherEffects());
                comboBoxWeightCategory.getSelectionModel().select(tempArmor.getWeightCategory());
            });
        } catch (Exception e) {
            Logger.log(e);
            Platform.runLater(() -> {
                new ErrorAlert("ERRORE", "Errore di Importazione", "La struttura dei dati non e' valida.");
                textFieldName.getScene().getWindow().hide();
            });
        }
    }
    private void initExistingArmor() {
        try {
            imageExtension = armor.getImageExtension();
            int CC = armor.getCostCopper();
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
                if (armor.getBase64image() != null && imageExtension != null) {
                    byte[] imageBytes = Base64.getDecoder().decode(armor.getBase64image());
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
                    bufferedImage = ImageIO.read(imageStream);
                } else if (armor.getBase64image() != null && imageExtension == null) {
                    throw new IllegalArgumentException("Image without declared extension");
                }
            } catch (IllegalArgumentException e) {
                Logger.log(e);
                armor.setBase64image(null);
                armor.setImageExtension(null);
                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di lettura", "L'immagine ricevuta dal database non è leggibile"));
                return;
            }

            int finalCC = CC;
            BufferedImage finalBufferedImage = bufferedImage;
            Platform.runLater(() -> {
                textFieldName.setText(armor.getName());
                textFieldWeight.setText(String.valueOf(armor.getWeight()));
                comboBoxRarity.getSelectionModel().select(armor.getRarity().getTextedRarity());
                textFieldMR.setText(String.valueOf(finalCC));
                textFieldMA.setText(String.valueOf(CS));
                textFieldME.setText(String.valueOf(CE));
                textFieldMO.setText(String.valueOf(CG));
                textFieldMP.setText(String.valueOf(CP));
                textAreaDescription.setText(armor.getDescription());
                if (finalBufferedImage != null && imageExtension != null) {
                    imageViewItem.setImage(SwingFXUtils.toFXImage(finalBufferedImage, null));
                } else {
                    imageViewItem.setImage(JFXDefs.AppInfo.LOGO);
                }
                spinnerQuantity.getValueFactory().setValue(armor.getQuantity());
                comboBoxSlot.getSelectionModel().select(armor.getSlot());
                textFieldEffectCA.setText(String.valueOf(armor.getCaEffect()));
                textFieldEffectLife.setText(String.valueOf(armor.getLifeEffect()));
                textFieldEffectLifePerc.setText(String.valueOf(armor.getLifePercentageEffect()));
                textFieldEffectLoad.setText(String.valueOf(armor.getLoadEffect()));
                textFieldEffectLoadPerc.setText(String.valueOf(armor.getLoadPercentageEffect()));
                textAreaOtherEffects.setText(armor.getOtherEffects());
                comboBoxWeightCategory.getSelectionModel().select(armor.getWeightCategory());
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
