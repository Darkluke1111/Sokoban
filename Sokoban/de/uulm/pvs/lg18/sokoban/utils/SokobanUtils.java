package de.uulm.pvs.lg18.sokoban.utils;

import de.uulm.pvs.lg18.sokoban.SokobanLevel;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility methods for the Sokoban Game.
 * 
 * @author Lukas
 *
 */
public class SokobanUtils {

  /**
   * Loads all Soloban Levels in the specified Directory and returns them in a
   * List.
   */
  public static List<SokobanLevel> loadLevels(String path) throws SokobanException {
    List<SokobanLevel> sl = new LinkedList<>();

    FileSystem fs = FileSystems.getDefault();
    Path dir = fs.getPath(path);

    XmlFinder xmlFinder = new XmlFinder("*.xml");

    try {
      Files.walkFileTree(dir, xmlFinder);
    } catch (IOException ioe) {
      throw new SokobanException("Couldn't load Sokoban Levels!", ioe);
    }

    for (Path file : xmlFinder.getMatchedPaths()) {
      try {
        sl.add(new SokobanLevel(file.toString()));
      } catch (SokobanException soe) {
        System.out.println("Wasn't able to build Level from file: " + file.toString());
        soe.printStackTrace();
      }
    }

    return sl;
  }

}
