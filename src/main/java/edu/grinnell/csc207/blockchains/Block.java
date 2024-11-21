package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Nicole Moreno Gonzalez
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The block number in the blockchain.
  */
  int num;

  /**
   * The transaction stored in this block.
  */
  Transaction transaction;

  /**
   * The hash of the previous block in the chain.
  */
  Hash prevHash;

  /**
   * The nonce that makes the block's hash valid.
  */
  long nonce;

  /**
   * The hash of this block, computed based on its contents.
  */
  Hash hash;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and
   * previous hash, mining to choose a nonce that meets the requirements
   * of the validator.
   *
   * @param number
   *   The number of the block.
   * @param transac
   *   The transaction for the block.
   * @param previousHash
   *   The hash of the previous block.
   * @param check
   *   The validator used to check the block.
   */
  public Block(int number, Transaction transac, Hash previousHash,
      HashValidator check) {
    this.num = number;
    this.transaction = transac;
    this.prevHash = previousHash;
    long tempNonce = 0;
    Hash tempHash;
    do {
      tempNonce++;
      this.nonce = tempNonce;
      tempHash = computeHash();
    } while (!check.isValid(tempHash));
    this.nonce = tempNonce;
    this.hash = tempHash;
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param number
   *   The number of the block.
   * @param transac
   *   The transaction for the block.
   * @param previousHash
   *   The hash of the previous block.
   * @param aNonce
   *   The nonce of the block.
   */
  public Block(int number, Transaction transac, Hash previousHash, long aNonce) {
    this.num = number;
    this.transaction = transac;
    this.prevHash = previousHash;
    this.nonce = aNonce;
    this.hash = computeHash();
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already
   * stored in the block.
   *
   * @return new computed hash
   */
  private Hash computeHash() {

    MessageDigest md = createMessageDigest();

    md.update(ByteBuffer.allocate(Integer.BYTES).putInt(num).array());
    md.update(transaction.getSource().getBytes());
    md.update(transaction.getTarget().getBytes());
    md.update(ByteBuffer.allocate(Integer.BYTES).putInt(transaction.getAmount()).array());
    md.update(prevHash.getBytes());
    md.update(ByteBuffer.allocate(Long.BYTES).putLong(nonce).array());

    return new Hash(md.digest());
  } // computeHash()

  /**
   * Create a MessageDigest instance for SHA-256.
   *
   * @return The MessageDigest instance.
   */
  private static MessageDigest createMessageDigest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not available");
    } // try/catch
  } // createMessageDigest()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.num;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return this.transaction;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  Hash getPrevHash() {
    return this.prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
    return this.hash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  @Override
  public String toString() {
    String transactionStr = transaction.toString();
    return String.format(
        "Block %d (Transaction: %s, Nonce: %d, prevHash: %s, hash: %s)",
        num, transactionStr, nonce, prevHash.toString(), hash.toString());
  } // toString()
} // class Block
