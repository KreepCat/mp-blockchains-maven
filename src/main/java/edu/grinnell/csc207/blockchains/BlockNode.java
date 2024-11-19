package edu.grinnell.csc207.blockchains;

/**
 * Wrap the Block in a node.
 *
 * @author Alex Pollock
 * @author Kevin Tang
 */
public class BlockNode {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /**
   * Previous Node of BlockNode.
   */
  private BlockNode prevNode;

  /**
   * Next Node of BlockNode.
   */
  private BlockNode nextNode;

  /**
   * Block stored by BlockNode.
   */
  private Block info;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * The basic constructor for the node version of block.
   * @param prev the previous node
   * @param next the next node
   * @param information the block
   */
  public BlockNode(BlockNode prev, BlockNode next, Block information) {
    this.prevNode = prev;
    this.nextNode = next;
    this.info = info;
  } // BlockNode()

  /**
   * Get the block.
   * @return the block
   */
  public Block getBlock() {
    return this.info;
  } // getBlock()

  /**
   * Get the next node.
   * @return the next node
   */
  public BlockNode getNext() {
    return this.nextNode;
  } // getNext()

  /**
   * Get the previous node.
   * @return the previous node
   */
  public BlockNode getPrev() {
    return this.prevNode;
  } // getPrev()

  /**
   * Set the block.
   * @param newInfo the block
   */
  public void setBlock(Block newInfo) {
    this.info = newInfo;
  } // setBlock(Block)

  /**
   * Set the next node.
   * @param next the next node
   */
  public void setNext(BlockNode next) {
    this.nextNode = next;
  } // setNext()

  /**
   * Set the previous node.
   * @param prev the previous node
   */
  public void setPrev(BlockNode prev) {
    this.prevNode = prev;
  } // setPrev()
} // BlockNode
