package it.italiandudes.map_visualizer.master.javafx.components;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TextFieldMenuItem extends Menu {

    // Attributes
    @NotNull private final TextField textField;

    // Constructors
    public TextFieldMenuItem(@NotNull final String menuName, @NotNull final String promptText) {
        super(menuName);
        textField = new TextField();
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setPromptText(promptText);
        MenuItem menuItem = new MenuItem();
        menuItem.setGraphic(textField);
        getItems().add(menuItem);
    }

    // Methods
    @Nullable
    public String getTextFieldContent() {
        String content = textField.getText();
        if (content == null || content.replace("\t", "").replace(" ", "").isEmpty()) return null;
        return content;
    }
    @NotNull
    public TextField getTextField() {
        return textField;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFieldMenuItem)) return false;

        TextFieldMenuItem that = (TextFieldMenuItem) o;

        return getTextField().equals(that.getTextField());
    }
    @Override
    public int hashCode() {
        return getTextField().hashCode();
    }
    @Override
    public String toString() {
        return getTextFieldContent();
    }
}
