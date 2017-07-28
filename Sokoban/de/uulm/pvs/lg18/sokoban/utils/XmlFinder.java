package de.uulm.pvs.lg18.sokoban.utils;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

/**
 * Class, which finds all xml-Files when used in Files.walkFileTree(...).
 * @author Lukas
 *
 */
public class XmlFinder extends SimpleFileVisitor<Path> {

  private final PathMatcher matcher;
  private int numMatches = 0;
  List<Path> xmlList;

  XmlFinder(String pattern) {
    matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    xmlList = new LinkedList<>();
  }

  // Compares the glob pattern against
  // the file or directory name.
  void find(Path file) {
    Path name = file.getFileName();
    if (name != null && matcher.matches(name)) {
      xmlList.add(file);
    }
  }

  // Prints the total number of
  // matches to standard out.
  void done() {
    System.out.println("Matched: " + numMatches);
  }

  // Invoke the pattern matching
  // method on each file.
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
    find(file);
    return CONTINUE;
  }

  // Invoke the pattern matching
  // method on each directory.
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    find(dir);
    return CONTINUE;
  }
  
  public List<Path> getMatchedPaths() {
    return xmlList;
  }

}
