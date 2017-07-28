package de.uulm.pvs.lg18.sokoban;

import de.uulm.pvs.lg18.sokoban.utils.SokobanException;
import de.uulm.pvs.lg18.sokoban.view.NameDialogController;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * main Class of a Sokoban application with GUI.
 * 
 * @author lg18
 *
 */
public class SokobanMainGui extends Application {

  private BorderPane rootLayout;
  private Stage primaryStage;
  private String playerName;
  private int playerId;
  private String levelPath;
  private SokobanLevel level;
  private static SokobanMainGui instance;
  private DbConnector dbc;

  public static void main(String[] args) {

    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    dbc = new DbConnector();
    instance = this;
    primaryStage.setOnCloseRequest(event -> {
      try {
        if (!quitLevel()) {
          event.consume();
        }
      } catch (SokobanException exep) {
        exep.printStackTrace();
      }
    });

    this.primaryStage = primaryStage;
    if (!openPlayerNameDialog()) {
      return;
    }
    initRootLayout();

  }

  /**
   * Opens a dialog window where the user can select his name. The name is saved in the apllication.
   * 
   * @return true if the operation was succsesfull, false otherwise
   */
  public boolean openPlayerNameDialog() throws SokobanException {
    try {
      // Load the fxml file and create a new stage for the popup dialog.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(SokobanMainGui.class.getResource("view/nameDialog.fxml"));
      AnchorPane page = (AnchorPane) loader.load();

      // Create the dialog Stage.
      Stage dialogStage = new Stage();
      dialogStage.setTitle("Select Name");
      Scene scene = new Scene(page);
      dialogStage.setScene(scene);
      dialogStage.setResizable(false);

      // Set the person into the controller.
      NameDialogController controller = loader.getController();
      controller.setDialogStage(dialogStage);

      // Show the dialog and wait until the user closes it
      dialogStage.showAndWait();

      if (controller.getClickedOk()) {
        playerName = controller.getName();
        playerId = dbc.getPlayerId(playerName);
        return true;
      } else {
        return false;
      }
    } catch (IOException exep) {
      exep.printStackTrace();
      return false;
    }
  }

  /**
   * Initializes the root layout of the Application using the fxml file.
   */
  public void initRootLayout() throws SokobanException {
    try {
      // Load root layout from FXML-File.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(SokobanMainGui.class.getResource("view/MainWindowView.fxml"));
      rootLayout = (BorderPane) loader.load();
      // Show the scene containing the root layout.
      Scene scene = new Scene(rootLayout);

      primaryStage.setTitle("Sokoban");
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException exep) {
      exep.printStackTrace();
    }
  }

  public SokobanLevel getLevel() {
    return level;
  }

  public static SokobanMainGui getInstance() {
    return instance;
  }
  
  public DbConnector getDbc() {
    return dbc;
  }

  /**
   * Opens a dialog where the user can select a Level. The level will then be loaded and stored in
   * this class.
   */
  public void openLevelSelectDialog() throws SokobanException {
    if (quitLevel()) {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new ExtensionFilter("XML-Files", "*.xml"));
      fc.setInitialDirectory(new File(SokobanConstants.LEVEL_PATH));
      fc.setTitle("Open Level");
      File file = fc.showOpenDialog(primaryStage);
      if (file == null) {
        return;
      }
      levelPath = file.getPath();
      level = new SokobanLevel(file.getPath());
    }
  }
  
  /**
   * Shows the top players of the game in a new window to the user.
   */
  public void openTopPlayerView() {
    try {

      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(SokobanMainGui.class.getResource("view/TopPlayersView.fxml"));
      AnchorPane topPlayersView = (AnchorPane) loader.load();

      Scene scene = new Scene(topPlayersView);
      Stage topPlayerStage = new Stage();
      topPlayerStage.setTitle("Top Players");
      topPlayerStage.setScene(scene);
      topPlayerStage.show();
    } catch (IOException exep) {
      exep.printStackTrace();
    }
  }

  /**
   * reloads the current level from the level file.
   */
  public void reloadLevel() throws SokobanException {
    if (quitLevel()) {
      level = new SokobanLevel(levelPath);
    }
  }

  /**
   * Should be called when leaving a level to update the database.
   */
  public boolean quitLevel() throws SokobanException {
    if (level != null) {
      if (!level.hasWon()) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Quitting current Level");
        alert.setHeaderText("Quitting an unfinished Level will count as 'not won'.");
        alert.setContentText("Do you want to continue?");
        ButtonType bt = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (bt != ButtonType.OK) {
          return false;
        }
      }
      dbc.addGame(level.name, level.hasWon(), playerId);
    }
    return true;
  }

}
