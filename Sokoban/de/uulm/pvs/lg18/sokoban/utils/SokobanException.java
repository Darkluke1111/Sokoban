package de.uulm.pvs.lg18.sokoban.utils;

/**
 * Exception used by the Sokoban Game.
 *
 */
public class SokobanException extends Exception {

  private static final long serialVersionUID = -728673158958185566L;

  public SokobanException(String msg) {
    super(msg);
  }

  public SokobanException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
