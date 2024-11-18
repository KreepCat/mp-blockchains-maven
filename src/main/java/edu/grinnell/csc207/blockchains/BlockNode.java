package edu.grinnell.csc207.blockchains;

public class BlockNode {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  private BlockNode prevNode;
  private BlockNode nextNode;
  private Block info;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+
  public BlockNode(BlockNode prev, BlockNode next, Block info) {
    this.prevNode = prev;
    this.nextNode = next;
    this.info = info;
  } // BlockNode()

  public Block getBlock() {
    return this.info;
  }

  public void setBlock(Block newInfo) {
    this.info = newInfo;
  }

  public void setNext(BlockNode next) {
    this.nextNode = next;
  }

  public void setPrev(BlockNode prev) {
    this.prevNode = prev;
  }

  public BlockNode getNext(BlockNode next) {
    return this.nextNode;
  }

  public BlockNode getPrev() {
    return this.prevNode;
  }
}
