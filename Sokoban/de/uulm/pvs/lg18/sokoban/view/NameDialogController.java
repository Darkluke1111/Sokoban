package de.uulm.pvs.lg18.sokoban.view;

import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller for the name dialog.
 * 
 * @author lg18
 *
 */
public class NameDialogController {

  @FXML
  private TextField nameField;
  @FXML
  private Button okButton;
  @FXML
  private AnchorPane mainPane;

  private Stage dialogStage;
  private boolean clickedOk = false;
  private String namePattern = "\\w{3,20}";
  private String name;

  @FXML
  public void initialize() {

  }

  private void initAccelerators() {
    dialogStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER),
        new Runnable() {
          @Override
          public void run() {
            okButton.fire();
          }
        });
  }

  /**
   * Called when the ok button is clicked.
   */
  @FXML
  private void onOkClick() {
    System.out.println("Ok klicked!");
    if (isValid(nameField.getText())) {
      clickedOk = true;
      name = nameField.getText();
      dialogStage.close();
    } else {
      Alert alert = new Alert(AlertType.WARNING);
      alert.initOwner(dialogStage);
      alert.setTitle("Invalid Name");
      alert.setHeaderText("Invalid Name entered!");
      alert.setContentText("Name should match the pattern " + namePattern + ".");

      alert.showAndWait();
    }
  }

  /**
   * Is called when the user clicks the cancel button.
   */
  @FXML
  private void onCancelClick() {
    // TODO
  }

  /**
   * Checks whether the user clicked ok.
   * 
   * @return true if ok was clicked
   */
  public boolean getClickedOk() {
    return clickedOk;
  }

  public String getName() {
    return name;
  }

  /**
   * Sets the dialog stage which is controlled by the controller.
   */
  public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
    initAccelerators();
  }

  private boolean isValid(String name) {
    return Pattern.matches(namePattern, name);
  }
}
