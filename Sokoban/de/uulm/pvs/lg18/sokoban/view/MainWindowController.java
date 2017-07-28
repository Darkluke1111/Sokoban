package de.uulm.pvs.lg18.sokoban.view;

import de.uulm.pvs.lg18.sokoban.GameScreen;
import de.uulm.pvs.lg18.sokoban.SokobanMainGui;
import de.uulm.pvs.lg18.sokoban.utils.SokobanException;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

/**
 * Controller for the main window of the application.
 * 
 * @author lg18
 *
 */
public class MainWindowController {

  private SokobanMainGui mainApp;
  private GameScreen gs;

  @FXML
  private Pane gameView;

  @FXML
  private void initialize() throws SokobanException {
    mainApp = SokobanMainGui.getInstance();
    gs = new GameScreen(mainApp, gameView);

    gameView.setFocusTraversable(true);
    initKeyListener();

  }

  /**
   * Initializes a keylistener for the gameview which handeln keypresses for moving the player.
   */
  private void initKeyListener() {
    gameView.setOnKeyPressed(event -> {
      if (mainApp.getLevel() != null) {
        KeyCode keyCode = event.getCode();
        switch (keyCode) {
          case UP:
          case W:
            mainApp.getLevel().moveNorth();
            break;
          case LEFT:
          case A:
            mainApp.getLevel().moveWest();
            break;
          case DOWN:
          case S:
            mainApp.getLevel().moveSouth();
            break;
          case RIGHT:
          case D:
            mainApp.getLevel().moveEast();
            break;
          default:
            break;
        }
        if (mainApp.getLevel().hasWon()) {
          showWinMessage();
        }
      }
    });
  }

  /**
   * Shows the win message.
   */
  private void showWinMessage() {
    System.out.println("You won!");
  }

  /**
   * Called when the user clicks the load menuitem.
   */
  @FXML
  private void onLoadLevel() throws SokobanException {
    mainApp.openLevelSelectDialog();
    gs.showLevel();
  }

  /**
   * Called when the user clicks the reload menuitem.
   */
  @FXML
  private void onReloadLevel() throws SokobanException {
    mainApp.reloadLevel();
    gs.showLevel();
  }
  
  /**
   * Called when the user clicks the corresponding menuitem.
   */
  @FXML
  private void onShowPlayerHighscore() {
    mainApp.openTopPlayerView();
  }
}
