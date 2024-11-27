package edu.grinnell.csc207.blockchains;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A full blockchain.
 *
 * @author Nicole Moreno Gonzalez
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The validator for validating blocks.
  */
  HashValidator validator;

  /**
   * The first node in the chain.
  */
  Node firstBlock;

  /**
   * The last node in the chain.
  */
  Node lastBlock;

  /**
   * The size of the chain (number of blocks).
  */
  int size;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check
   *   The validator used to check elements.
   */
  public BlockChain(HashValidator check) {
    this.validator = check;
    Hash emptyHash = new Hash(new byte[] {});
    Transaction firstTransaction = new Transaction("", "", 0);
    Block b = new Block(0, firstTransaction, emptyHash, this.validator);
    this.firstBlock = new Node(b);
    this.lastBlock = this.firstBlock;
    this.size = 1;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Traverse the chain to find the second-to-last block.
   *
   * @return the second-to-last node.
   */
  private Node findSecondToLastNode() {
    Node current = this.firstBlock;
    while (current.next != this.lastBlock) {
      current = current.next;
    } // while
    return current;
  } // findSecondToLastNode()

  /**
   * Verify that a block is valid to append to the chain.
   *
   * @param block The block to validate.
   * @throws IllegalArgumentException if the block is invalid.
   */
  private void validateBlock(Block block) {
    if (!block.getPrevHash().equals(this.lastBlock.block.getHash())) {
      throw new IllegalArgumentException("Invalid previous hash.");
    } // if
    if (!this.validator.isValid(block.getHash())) {
      throw new IllegalArgumentException("Invalid hash.");
    } // if
    Block comparisonBlock = new Block(
        block.getNum(),
        block.getTransaction(),
        block.getPrevHash(),
        block.getNonce()
    );
    if (!block.getHash().equals(comparisonBlock.getHash())) {
      throw new IllegalArgumentException("Block hash does not match its contents.");
    } // if
  } // validateBlock()

  /**
   * Collect all unique users in the chain.
   *
   * @return a set of unique users.
   */
  private HashSet<String> collectUsers() {
    HashSet<String> userSet = new HashSet<>();
    Node current = this.firstBlock;
    while (current != null) {
      Transaction t = current.block.getTransaction();
      if (!t.getSource().isEmpty()) {
        userSet.add(t.getSource());
      } // if
      if (!t.getTarget().isEmpty()) {
        userSet.add(t.getTarget());
      } // if
      current = current.next;
    } // while
    return userSet;
  } // collectUsers()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that
   * block.
   *
   * @param t
   *   The transaction that goes in the block.
   *
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    int nonce = 0;
    Block block;
    do {
      block = new Block(this.size, t, this.lastBlock.block.getHash(), nonce);
      nonce++;
    } while (!this.validator.isValid(block.getHash()));
    return block;
  } // mine(Transaction)

  /**
   * Get the number of blocks currently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return this.size;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk
   *   The block to add to the end of the chain.
   *
   * @throws IllegalArgumentException if (a) the hash is not valid, (b)
   *   the hash is not appropriate for the contents, or (c) the previous
   *   hash is incorrect.
   */
  public void append(Block blk) {
    validateBlock(blk);
    Node newNode = new Node(blk);
    this.lastBlock.next = newNode;
    this.lastBlock = newNode;
    this.size++;
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's
   *   not removed) or true otherwise (in which case the last block
   *   is removed).
   */
  public boolean removeLast() {
    if (this.lastBlock == this.firstBlock) {
      return false;
    } // if
    this.lastBlock = findSecondToLastNode();
    this.lastBlock.next = null;
    this.size--;
    return true;
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return this.lastBlock.block.getHash();
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are
   * legal/correct at every step, (b) that every block has a correct
   * previous hash field, (c) that every block has a hash that is correct
   * for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   */
  public boolean isCorrect() {
    Node current = this.firstBlock;
    while (current.next != null) {
      Node nextNode = current.next;
      if (!nextNode.block.getPrevHash().equals(current.block.getHash())) {
        return false;
      } // if
      if (!this.validator.isValid(nextNode.block.getHash())) {
        return false;
      } // if
      Block recalculatedBlock = new Block(
          nextNode.block.getNum(),
          nextNode.block.getTransaction(),
          nextNode.block.getPrevHash(),
          nextNode.block.getNonce()
      );
      if (!nextNode.block.getHash().equals(recalculatedBlock.getHash())) {
        return false;
      } // if
      current = nextNode;
    } // while
    Iterator<String> userIterator = this.users();
    while (userIterator.hasNext()) {
      if (this.balance(userIterator.next()) < 0) {
        return false;
      } // if
    } // while
    Iterator<Transaction> transIterator = this.iterator();
    while (transIterator.hasNext()) {
      if (transIterator.next().getAmount() < 0) {
        return false;
      } // if
    } // while
    return true;
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are
   * legal/correct at every step, (b) that every block has a correct
   * previous hash field, (c) that every block has a hash that is correct
   * for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception
   *   If things are wrong at any block.
   */
  public void check() throws Exception {
    if (!this.isCorrect()) {
      throw new Exception("Blockchain is invalid.");
    } // if
  } // check()

  /**
   * Return an iterator of all the people who participated in the
   * system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    return collectUsers().iterator();
  } // users()

  /**
   * Find one user's balance.
   *
   * @param user
   *   The user whose balance we want to find.
   *
   * @return that user's balance (or 0, if the user is not in the system).
   */
  public int balance(String user) {
    int balance = 0;
    Node current = this.firstBlock;

    while (current != null) {
      Transaction t = current.block.getTransaction();
      if (user.equals(t.getSource())) {
        balance -= t.getAmount();
      } // if
      if (user.equals(t.getTarget())) {
        balance += t.getAmount();
      } // if
      current = current.next;
    } // while
    return balance;
  } // balance()

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      private Node current = BlockChain.this.firstBlock;

      @Override
      public boolean hasNext() {
        return current != null;
      } // hasNext()

      @Override
      public Block next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        Block toReturn = current.block;
        current = current.next;
        return toReturn;
      } // next()
    };
  } // blocks()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  @Override
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      private Node current = BlockChain.this.firstBlock;

      @Override
      public boolean hasNext() {
        return current != null;
      } // hasNext()

      @Override
      public Transaction next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        Transaction toReturn = current.block.getTransaction();
        current = current.next;
        return toReturn;
      } // next()
    };
  } // iterator()
} // class BlockChain

