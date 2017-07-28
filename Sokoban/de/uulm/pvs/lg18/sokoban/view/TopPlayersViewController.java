package de.uulm.pvs.lg18.sokoban.view;

import de.uulm.pvs.lg18.sokoban.DbConnector;
import de.uulm.pvs.lg18.sokoban.SokobanMainGui;
import javafx.fxml.FXML;

/**
 * Controlls the TopPlayersView.
 * @author lg18
 *
 */
public class TopPlayersViewController {
  
  SokobanMainGui mainApp;
  DbConnector dbc;
  
  @FXML
  private void initialize() {
    mainApp = SokobanMainGui.getInstance();
    dbc = mainApp.getDbc();
    //do something smart
  }
}
