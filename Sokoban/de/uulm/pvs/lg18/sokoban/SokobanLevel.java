package de.uulm.pvs.lg18.sokoban;

import de.uulm.pvs.lg18.sokoban.utils.SokobanException;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class, representing a Sokoban Level.
 * 
 * @author Lukas
 *
 */
public class SokobanLevel {

  public final String name;
  public final Difficulty difficulty;
  public final List<String> authors;

  private final int width;
  private final int height;
  private final ObjectProperty<Tile>[][] level;

  /**
   * Builds a Sokoban Level from a xml file with validy checks.
   * 
   * @param path
   *          Path to the xml-File
   */
  public SokobanLevel(String path) throws SokobanException {

    Schema schema;

    // load schema
    try {
      schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
          .newSchema(new File("sokoban/Sokoban.xsd"));
    } catch (SAXException sax) {
      throw new SokobanException("Fehler beim lesen des Levels!", sax);
    }

    // create DocumentBuilder
    DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();

    fact.setSchema(schema);
    fact.setNamespaceAware(true);

    DocumentBuilder db;
    try {
      db = fact.newDocumentBuilder();
    } catch (ParserConfigurationException pce) {
      throw new SokobanException("Error in parser Configuration", pce);
    }

    // read the Document
    Document doc;

    try {
      doc = db.parse(new File(path));
    } catch (SAXException saxe) {
      throw new SokobanException("Error while parsing file!", saxe);
    } catch (IOException ioe) {
      throw new SokobanException("Error while reading file!", ioe);
    }

    NodeList authorNodes = doc.getElementsByTagName("Author");
    authors = new LinkedList<>();

    for (int i = 0; i < authorNodes.getLength(); i++) {
      authors.add(authorNodes.item(i).getTextContent());
    }

    name = doc.getElementsByTagName("LevelName").item(0).getTextContent();

    if (doc.getElementsByTagName("Difficulty").getLength() == 0) {
      difficulty = Difficulty.NONE;
    } else {
      difficulty = Difficulty
          .valueOf(doc.getElementsByTagName("Difficulty").item(0).getTextContent());
    }

    Node levelNode = doc.getElementsByTagName("LevelData").item(0);

    width = Integer.parseInt(levelNode.getAttributes().getNamedItem("width").getTextContent());
    height = Integer.parseInt(levelNode.getAttributes().getNamedItem("height").getTextContent());

    level = (ObjectProperty<Tile>[][]) Array.newInstance(ObjectProperty[].class, height);

    String[] levelLines = levelNode.getTextContent().split("\n|\r");
    for (int i = 0; i < height; i++) {
      // level[i] = levelLines[i].toCharArray();
      int length = levelLines[i].length();
      level[i] = (ObjectProperty<Tile>[]) Array.newInstance(ObjectProperty.class, length);
      for (int j = 0; j < length; j++) {
        Tile tile = Tile.fromChar(levelLines[i].charAt(j));
        level[i][j] = new SimpleObjectProperty<>(tile);
      }

      if (level[i].length != width) {
        throw new SokobanException(
            "Width shouild be " + width + " but is " + level[i].length + " in line " + i + "!");
      }
    }

  }

  /**
   * Moves the Player north.
   */
  public boolean moveNorth() {
    return movePlayer(SokobanLevel.Direction.NORTH);

  }

  /**
   * Moves the Player east.
   */
  public boolean moveEast() {
    return movePlayer(SokobanLevel.Direction.EAST);
  }

  /**
   * Moves the Player south.
   */
  public boolean moveSouth() {
    return movePlayer(SokobanLevel.Direction.SOUTH);
  }

  /**
   * Moves the Player west.
   */
  public boolean moveWest() {
    return movePlayer(SokobanLevel.Direction.WEST);
  }

  /**
   * Checks whether the player has won.
   * 
   * @return true if the player has won, otherwise false
   */
  public boolean hasWon() {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (getTileAt(j, i) == Tile.TARGET) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Bewegt den Spieler auf dem Spielfeld in die angegebene Richtung, falls möglich. Falls der Weg
   * blockiert ist, bleibt die Position des Spielers unverändert und es with false zurückgegeben.
   * 
   * @param board
   *          SOkoban Spielfeld
   * @param dir
   *          Bewegungsrichtung
   * @return true, falls die Bewegung erfolgreich war, sonst false.
   */
  private boolean movePlayer(Direction dir) {
    Point player = findPlayer();
    Point newPlayer = dir.getTargetPoint(player);

    if (getTileAt(newPlayer).isChestType()) {
      if (!moveChest(newPlayer, dir)) {
        return false;
      }
    }

    if (getTileAt(newPlayer) == Tile.AIR) {
      if (getTileAt(player) == Tile.PLAYER_ON_TARGET) {
        setTileAt(player, Tile.TARGET);
      } else {
        setTileAt(player, Tile.AIR);
      }
      setTileAt(newPlayer, Tile.PLAYER);
      return true;
    }

    if (getTileAt(newPlayer) == Tile.TARGET) {
      if (getTileAt(player) == Tile.PLAYER_ON_TARGET) {
        setTileAt(player, Tile.TARGET);
      } else {
        setTileAt(player, Tile.AIR);
      }
      setTileAt(newPlayer, Tile.PLAYER_ON_TARGET);
      return true;
    }

    return false;

  }

  private boolean moveChest(Point location, Direction dir) {
    Point newLocation = dir.getTargetPoint(location);
    Tile obstacle = getTileAt(newLocation);
    if (!getTileAt(location).isChestType()) {
      return false;
    }

    switch (obstacle) {
      case TARGET:
        if (getTileAt(location) == Tile.CHEST_ON_TARGET) {
          setTileAt(location, Tile.TARGET);
        } else {
          setTileAt(location, Tile.AIR);
        }
        setTileAt(newLocation, Tile.CHEST_ON_TARGET);
        return true;
      case AIR:
        if (getTileAt(location) == Tile.CHEST_ON_TARGET) {
          setTileAt(location, Tile.TARGET);
        } else {
          setTileAt(location, Tile.AIR);
        }
        setTileAt(newLocation, Tile.CHEST);
        return true;
      default:
        return false;
    }

  }

  public Tile getTileAt(Point point) {
    return level[point.y][point.x].get();
  }

  public Tile getTileAt(int xpos, int ypos) {
    return level[ypos][xpos].get();
  }

  public void setTileAt(Point point, Tile newTile) {
    level[point.y][point.x].set(newTile);
  }

  public void setTileAt(int xpos, int ypos, Tile newTile) {
    level[ypos][xpos].set(newTile);
  }

  public ObjectProperty<Tile> levelTileProperty(Point point) {
    return level[point.y][point.x];
  }

  public ObjectProperty<Tile> levelTileProperty(int xpos, int ypos) {
    return level[ypos][xpos];
  }

  /**
   * Gibt die Position des Spielers auf dem Spielfeld als {@link Point} zurück.
   * 
   * @return Die Position des Spielers
   */
  public Point findPlayer() {
    for (int i = 0; i < height; i++) { // laufe durch Spalten
      for (int j = 0; j < width; j++) { // laufe durch Zeilen
        if (getTileAt(j, i).isPlayerType()) {
          return new Point(j, i);
        }
      }
    }
    System.out.println("no Player on the Board!");
    return null;
  }

  @Override
  public String toString() {
    return name + " " + difficulty;
  }

  /**
   * Prints the Sokoba Level into stdout.
   */
  public void printLevel() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        sb.append(level[i][j].get().symbol);
      }
      sb.append('\n');
    }
    System.out.println(sb.toString());
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  /**
   * Repräsentation der vier Bewegungsrichtungen auf dem Sokoban-Spielfeld.
   * 
   * @author Lukas
   *
   */
  enum Direction {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0);

    int dx;
    int dy;

    Direction(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
    }

    public Point getTargetPoint(Point start) {
      Point target = new Point(start.x + dx, start.y + dy);
      return target;
    }
  }

  /**
   * represents the Sokoban Difficulty.
   * 
   * @author lg18
   *
   */
  enum Difficulty {
    NONE, EASY, MEDIUM, HARD, IMPOSSIBLE;
  }

  /**
   * represents a Tile Type of the Sokoban game.
   * 
   * @author Lukas
   *
   */
  enum Tile {
    WALL('#'), AIR(' '), TARGET('.'), PLAYER('@'), PLAYER_ON_TARGET('+'), CHEST(
        '$'), CHEST_ON_TARGET('*');

    public char symbol;

    private Tile(char symbol) {
      this.symbol = symbol;
    }

    public static Tile fromChar(char symbol) {
      for (Tile tile : Tile.values()) {
        if (tile.symbol == symbol) {
          return tile;
        }
      }
      return null;
    }

    public boolean isChestType() {
      return this == Tile.CHEST || this == Tile.CHEST_ON_TARGET;
    }

    public boolean isPlayerType() {
      return this == Tile.PLAYER || this == Tile.PLAYER_ON_TARGET;
    }
  }

}
