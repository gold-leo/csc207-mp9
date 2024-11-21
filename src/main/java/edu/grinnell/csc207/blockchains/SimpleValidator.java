package edu.grinnell.csc207.blockchains;

/**
 * Things that validate hashes. Simple version.
 *
 * @author Leo Goldman
 */
public class SimpleValidator implements HashValidator {
  /**
   * Determine if a hash meets some criterion.
   *
   * @param hash
   *   The hash we're checking.
   *
   * @return true if the hash is valid and false otehrwise.
   */
  public boolean isValid(Hash hash) {
    return (hash.length() >= 1) && (hash.get(0) == 0);
  } // isValid(Hash)
} // SimpleValidator
