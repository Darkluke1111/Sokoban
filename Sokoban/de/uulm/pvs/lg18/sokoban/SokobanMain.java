package de.uulm.pvs.lg18.sokoban;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import de.uulm.pvs.lg18.sokoban.utils.SokobanException;
import de.uulm.pvs.lg18.sokoban.utils.SokobanUtils;

/**
 * Main class of the Sokoban Game.
 * 
 * @author Lukas
 *
 */
public class SokobanMain {

  private static Scanner s;
  private static DbConnector dbc;

  // Debug mode
  public static final boolean log = true;

  /**
   * Runs the Game in the Console.
   */
  public static void main(String[] args) throws SokobanException, IOException {
    s = new Scanner(System.in);
    dbc = new DbConnector();

    int playerId = selectPlayer();

    List<SokobanLevel> slList = SokobanUtils.loadLevels("Levels/");
    SokobanLevel sl = selectLevel(slList);

    runGame(sl, playerId);

    if (log) {
      printDb();
    }

    s.close();
    dbc.close();
  }

  private static SokobanLevel selectLevel(List<SokobanLevel> slList) {
    int num;
    while (true) {

      System.out.println("Select one of the following levels:");
      for (int i = 0; i < slList.size(); i++) {
        System.out.println("[" + (i + 1) + "] " + slList.get(i).toString());
      }

      num = s.nextInt();
      s.nextLine();
      if (num >= 1 && num <= slList.size()) {
        break;
      }
    }

    return slList.get(num - 1);
  }

  /**
   * Starts the Game with the specified Level.
   */
  public static void runGame(SokobanLevel sl, int playerId) throws SokobanException {

    boolean running = true;
    char eingabe;
    while (running) {

      sl.printLevel();
      System.out.println("Where do you want to go? (N/E/S/W or X to exit)");

      eingabe = Character.toUpperCase(s.nextLine().charAt(0));

      switch (eingabe) {
        case 'X':
          running = false;
          dbc.addGame(sl.name, false, playerId);
          System.out.println("Bye!");
          break;
        case 'N':
          sl.moveNorth();
          break;
        case 'E':
          sl.moveEast();
          break;
        case 'S':
          sl.moveSouth();
          break;
        case 'W':
          sl.moveWest();
          break;
        default:
          break;
      }

      if (sl.hasWon()) {
        sl.printLevel();
        System.out.println("You win!");
        dbc.addGame(sl.name, true, playerId);
        running = false;
      }
    }
  }

  private static int selectPlayer() throws SokobanException {

    System.out.println("Hi, whats your name?");
    String player = s.nextLine();
    if (!dbc.doesPlayerExist(player)) {
      dbc.createPlayer(player);
    }
    int playerId = dbc.getPlayerId(player);
    return playerId;
  }

  private static void printDb() throws SokobanException {
    dbc.printPlayers();
    System.out.println();
    dbc.printGames();
  }
  
  
}
