package de.uulm.pvs.lg18.sokoban;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.uulm.pvs.lg18.sokoban.SokobanLevel.Tile;
import de.uulm.pvs.lg18.sokoban.utils.SokobanException;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * Implements the Gamescreen in a grid pane with the tiles as objects in the scene Tree.
 * 
 * @author Lukas
 *
 */
public class GameScreen implements GameScreenInterface {
  SokobanMainGui mainApp;
  Pane parent;
  String ressourcePath = "ressources/";
  int topOffset = 1;

  Map<Tile, Image> imageMap = new HashMap<>();
  
  /**
   * Constructs a GameScreen and initializes it with the parent pane and the main app.
   */
  public GameScreen(SokobanMainGui main, Pane parent) throws SokobanException {
    this.mainApp = main;
    this.parent = parent;
    loadImages();
  }


  @Override
  public void showLevel() {
    SokobanLevel level = mainApp.getLevel();
    if (level == null) {
      return;
    }
    int height = level.getHeight();
    int width = level.getWidth();

    GridPane grid = new GridPane();
    
    FlowPane fp = new FlowPane();
    fp.setHgap(10);
    Label nameLabel = new Label(level.name);
    Label difficultyLabel = new Label(level.difficulty.toString());
    fp.getChildren().addAll(nameLabel, difficultyLabel);
    
    grid.add(fp, 0, 0, width, 1);
    
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        ImageView imageView = new ImageView();
        makeTileImageBinding(imageView, level, i, j);
        grid.add(imageView, i, j + topOffset);
      }
    }
    grid.translateXProperty().bind(parent.widthProperty().subtract(grid.widthProperty()).divide(2));
    grid.translateYProperty().bind(parent.heightProperty().subtract(grid.heightProperty()).divide(2));
    
    parent.getChildren().removeAll(parent.getChildren());
    parent.getChildren().add(grid);
  }

  /**
   * Binds the tile from the model to the imageView for the tile.
   */
  private void makeTileImageBinding(ImageView imageView, SokobanLevel level, int xpos, int ypos) {
    imageView.imageProperty().bind(new ObjectBinding<Image>() {

      {
        bind(level.levelTileProperty(xpos, ypos));
      }

      @Override
      protected Image computeValue() {
        Tile tile = level.levelTileProperty(xpos, ypos).get();
        return imageMap.get(tile);
      }
    });
  }

  /**
   * Loads the Tile Imgages.
   */
  private void loadImages() throws SokobanException {
    for (Tile tile : Tile.values()) {
      File file = new File(SokobanConstants.RESSOURCE_PATH + tile.toString().toLowerCase() + ".png");

      try (InputStream is = new FileInputStream(file)) {
        Image image = new Image(is);
        imageMap.put(tile, image);
      } catch (FileNotFoundException exep) {
        throw new SokobanException(
            "Wasn't able to find image file " + file.getAbsolutePath() + " for " + tile.toString(),
            exep);
      } catch (IOException exep) {
        throw new SokobanException("Error while trying to load the file " + file.getAbsolutePath()
            + " for " + tile.toString(), exep);
      }

    }
  }
}
