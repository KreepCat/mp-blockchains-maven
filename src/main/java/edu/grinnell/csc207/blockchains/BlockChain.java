package edu.grinnell.csc207.blockchains;

import java.util.Random;

/**
 * A full blockchain.
 *
 * @author Your Name Here
 */
public class BlockChain {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  BlockNode first;
  BlockNode last;
  int length;
  HashValidator simpleValidator = (hash) -> (hash.length() >= 1) && (hash.get(0) == 0);


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
    this.length = 0;
    this.first = new BlockNode(null, null, mine(new Transaction(null, null, 0)));
    this.last = this.first;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

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
    Hash hash;
    long nonce;
    Random random = new Random();
    do {
      nonce = random.nextLong();
      hash = Block.computeHash(this.length, t, this.last, nonce);
    } while (! simpleValidator.isValid(hash));
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return 2;   // STUB
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk
   *   The block to add to the end of the chain.
   */
  public void append(Block blk) {
    // STUB
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's
   *   not removed) or true otherwise (in which case the last block
   *   is removed).
   */
  public boolean removeLast() {
    return true;        // STUB
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return new Hash(new byte[] {2, 0, 7});   // STUB
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
    return true;        // STUB
  } // isCorrect()

} // class BlockChain
