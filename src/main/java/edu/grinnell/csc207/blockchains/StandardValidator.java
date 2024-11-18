package edu.grinnell.csc207.blockchains;

/**
 * Things that validate hashes. Standard version.
 *
 * @author Leo Goldman
 */
public class StandardValidator implements HashValidator {
  /**
   * Determine if a hash meets some criterion.
   *
   * @param hash
   *   The hash we're checking.
   *
   * @return true if the hash is valid and false otehrwise.
   */
  public boolean isValid(Hash hash) {
    return (hash.length() >= 3) && (hash.get(0) == 0)
    && (hash.get(1) == 0) && (hash.get(2) == 0);
  } // isValid(Hash)
} // StandardValidator