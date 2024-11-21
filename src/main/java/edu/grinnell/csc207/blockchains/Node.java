package edu.grinnell.csc207.blockchains;

/**
 * Represents a node in the blockchain.
 * Stores a block and points to the next node.
 *
 * @author Nicole Moreno Gonzalez
 */

public class Node {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The block stored in this node.
   */
  Block block;

  /**
   * The next node in the chain.
   */
  Node next;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Constructor for a node that initializes the block and sets the next node to null.
   *
   * @param blck The block to store in the node.
   */
  public Node(Block blck) {
    this.block = blck;
    this.next = null;
  } // Node(block)

  /**
   * Constructor for a node that initializes the block and sets the next node explicitly.
   *
   * @param blck The block to store in the node.
   * @param nextNode The next node in the chain.
   */
  public Node(Block blck, Node nextNode) {
    this.block = blck;
    this.next = nextNode;
  } // Node (block, next)
} // class Node
