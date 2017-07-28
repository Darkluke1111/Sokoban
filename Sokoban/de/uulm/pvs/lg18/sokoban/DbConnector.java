package de.uulm.pvs.lg18.sokoban;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import de.uulm.pvs.lg18.sokoban.utils.SokobanException;

/**
 * Class which handles the Database connection for the Sokoban game.
 * 
 * @author lg18
 *
 */
public class DbConnector implements Closeable {
  
  String userName = "";
  String password = "";
  String dbpath = SokobanConstants.DB_PATH;
  String url = SokobanConstants.DB_URL_PREFIX + dbpath;
  Connection conn = null;

  public DbConnector() throws SokobanException {
    conn = getConnection();
  }

  /**
   * Gets the connection to the Player and highscore database.
   * 
   */
  public Connection getConnection() throws SokobanException {
    Connection conn = null;

    try {
      Class.forName("org.hsqldb.jdbcDriver");
      conn = DriverManager.getConnection(this.url, userName, password);
    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to connect to database!", sqle);
    } catch (ClassNotFoundException cnfe) {
      throw new SokobanException("Wasn't able to load Driver Class!", cnfe);
    }

    return conn;
  }


  /**
   * Checks whether the specified Player exists in the Database.
   * 
   * @return true if the player exists, otherwise false
   */
  public boolean doesPlayerExist(String name) throws SokobanException {

    String qstring = "SELECT COUNT(*) FROM players WHERE player_name = ?;";

    ResultSet rs = null;

    try (PreparedStatement ps = conn.prepareStatement(qstring)) {

      ps.setString(1, name);
      rs = ps.executeQuery();
      rs.next();
      int count = rs.getInt(1);
      rs.close();
      return count > 0;

    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to process DB-Quarry!", sqle);
    }
  }

  /**
   * Gets the PlayerID of the specified Player from the Database.
   */
  public int getPlayerId(String name) throws SokobanException {

    String qstring = "SELECT player_id FROM players WHERE player_name = ?;";

    ResultSet rs = null;

    try (PreparedStatement ps = conn.prepareStatement(qstring)) {

      ps.setString(1, name);
      rs = ps.executeQuery();
      rs.next();
      int pid = rs.getInt(1);
      rs.close();
      return pid;

    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to process DB-Quarry!", sqle);
    }

  }

  /**
   * Creates the player with the specified name in the database. The player will
   * have 0 played games and 0 won games. The player id is automaticly set by
   * the database.
   */
  public void createPlayer(String name) throws SokobanException {

    String qstring = "INSERT INTO players(player_name, games_played, games_won)  VALUES(?,0,0);";

    try (PreparedStatement ps = conn.prepareStatement(qstring)) {

      ps.setString(1, name);
      ps.execute();

    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to process DB-Quarry!", sqle);

    }
  }

  /**
   * Increases the number of wins and played games for the player with the
   * provided playerID.
   */
  public void playerWon(int playerId) throws SokobanException {
    String qstring = "UPDATE players SET " + "games_won = games_won + 1, "
        + "games_played = games_played + 1  " + "WHERE player_id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(qstring)) {

      ps.setInt(1, playerId);
      ps.execute();

    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to process DB-Quarry!", sqle);
    }
  }

  /**
   * Increases the number of played games for the player with the provided
   * playerID.
   */
  public void playerLost(int playerId) throws SokobanException {
    String qstring = "UPDATE players SET " + "games_played = games_played + 1  "
        + "WHERE player_id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(qstring)) {

      ps.setInt(1, playerId);
      ps.execute();

    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to process DB-Quarry!", sqle);
    }
  }

  /**
   * Adds a game to the database.
   */
  public void addGame(String levelName, boolean won, int playerId) throws SokobanException {
    String qstring = "INSERT INTO games(level_name,date,won,player_id) VALUES(?,?,?,?);";

    try (PreparedStatement ps = conn.prepareStatement(qstring)) {

      ps.setString(1, levelName);
      ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
      ps.setBoolean(3, won);
      ps.setInt(4, playerId);
      ps.execute();

      if (won) {
        playerWon(playerId);
      } else {
        playerLost(playerId);
      }

    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to process DB-Quarry!", sqle);
    }
  }

  /**
   * Prints the contents of the players-table in the commandline.
   */
  public void printPlayers() throws SokobanException {
    String qstring = "SELECT * FROM players;";

    ResultSet rs = null;

    try (PreparedStatement ps = conn.prepareStatement(qstring);) {

      rs = ps.executeQuery();
      ResultSetMetaData meta = rs.getMetaData();

      for (int i = 1; i <= meta.getColumnCount(); i++) {
        System.out.format("%20s", meta.getColumnName(i));
      }
      System.out.println();
      while (rs.next()) {
        for (int i = 1; i <= meta.getColumnCount(); i++) {
          System.out.format("%20s", rs.getString(i));
        }
        System.out.println();
      }

      rs.close();

    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to process DB-Quarry!", sqle);
    }
  }

  /**
   * Prints the contents of the games-table in the commandline.
   */
  public void printGames() throws SokobanException {
    String qstring = "SELECT * FROM games;";

    ResultSet rs = null;

    try (PreparedStatement ps = conn.prepareStatement(qstring);) {

      rs = ps.executeQuery();
      ResultSetMetaData meta = rs.getMetaData();

      for (int i = 1; i <= meta.getColumnCount(); i++) {
        System.out.format("%30s", meta.getColumnName(i));
      }
      System.out.println();
      while (rs.next()) {
        for (int i = 1; i <= meta.getColumnCount(); i++) {
          System.out.format("%30s", rs.getString(i));
        }
        System.out.println();
      }

      rs.close();

    } catch (SQLException sqle) {
      throw new SokobanException("Wasn't able to process DB-Quarry!", sqle);
    }
  }

  /**
   * Test method.
   */
  public static void main(String[] args) throws SokobanException, IOException {
    DbConnector instance = new DbConnector();
  
    instance.createPlayer("Lukas");
    boolean pe = instance.doesPlayerExist("Lukas");
    int pid = instance.getPlayerId("Lukas");
  
    System.out.println(pe + " id: " + pid);
    instance.printPlayers();
    instance.printGames();
    instance.close();
  }

  @Override
  public void close() throws IOException {
    try {
      conn.close();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }
  

}
