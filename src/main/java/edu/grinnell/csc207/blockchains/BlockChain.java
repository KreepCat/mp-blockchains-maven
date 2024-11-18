package edu.grinnell.csc207.blockchains;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;
import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

/**
 * A full blockchain.
 *
 * @author Your Name Here
 */
public class BlockChain {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /**
   * First Node.
   */
  static BlockNode first;
  /**
   * Last Node.
   */
  BlockNode last;
  /**
   * Length of chain.
   */
  static int length;
  /**
   * Hash validator.
   */
  HashValidator simpleValidator = (hash) -> (hash.length() >= 1) && (hash.get(0) == 0);
  /**
   * Associative array to store balance.
   */
  static AssociativeArray<String, Integer> balance = new AssociativeArray<>();


  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check The validator used to check elements.
   * @throws NoSuchAlgorithmException
   */
  public BlockChain(HashValidator check) throws NoSuchAlgorithmException {
    //STUB: Should the length initially be 1 or 0 (Does the first count?)
    this.length = 0;
    this.first = new BlockNode(null, null,
        new Block(0, new Transaction("", "", 0), new Hash(new byte[] {}), simpleValidator));
    this.last = this.first;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+
  /**
   * @param blk input block.
   * @param mode mode = 1 if append a block (add new transaction), mode = 0 if remove a block
   * @throws NullKeyException NullKeyException
   * @throws KeyNotFoundException KeyNotFoundException
   */
  public void transaction(Block blk, int mode) throws NullKeyException, KeyNotFoundException {
    String from = blk.transaction.getSource();
    int amt = blk.transaction.getAmount();
    if (balance.hasKey(from) && mode == 1) {
      balance.set(from, balance.get(from) - amt);
    } else if (balance.hasKey(from) && mode == 0) {
      balance.set(from, balance.get(from) + amt);
    } else {
      deposit(blk, mode);
    } // if
    BlockNode newNode = new BlockNode(this.last, null, blk);
    this.last.setNext(newNode);
  } // transaction()

  /**
   * @param blk input block.
   * @param mode mode = 1 if append a block (add new transaction), mode = 0 if remove a block
   * @throws NullKeyException NullKeyException
   * @throws KeyNotFoundException KeyNotFoundException
   */
  public void deposit(Block blk, int mode) throws NullKeyException, KeyNotFoundException {
    String to = blk.transaction.getTarget();
    int amt = blk.transaction.getAmount();
    if (balance.hasKey(to) && mode == 1) {
      balance.set(to, balance.get(to) + amt);
    } else if (balance.hasKey(to) && mode == 0) {
      balance.set(to, balance.get(to) - amt);
    } else {
      balance.set(to, amt);
    } // if
  } // deposit()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that block.
   *
   * @param t The transaction that goes in the block.
   *
   * @return a new block with correct number, hashes, and such.
   * @throws NoSuchAlgorithmException
   */
  public Block mine(Transaction t) {
    long nonce;
    Random random = new Random();
    Block newBlock;
    do {
      nonce = random.nextLong();
      if (this.length == 0) {
        newBlock = new Block(length, t, null, nonce);
      } else {
        newBlock = new Block(length, t, this.getHash(), nonce);
      } // if/else
    } while (!simpleValidator.isValid(newBlock.getHash()));
    return newBlock;
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return this.length;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk The block to add to the end of the chain.
   */
  public void append(Block blk) {
    BlockNode dummy = new BlockNode(this.last, null, blk);
    this.last.setNext(dummy);
    this.last = dummy;
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's not removed) or true
   *         otherwise (in which case the last block is removed).
   * @throws KeyNotFoundException
   * @throws NullKeyException
   */
  public boolean removeLast() throws NullKeyException, KeyNotFoundException {
    if (this.length <= 1) {
      return false;
    } else {
      transaction(this.last.getBlock(), 0);
      this.last = this.last.getPrev();
      this.last.setNext(null);
      return true;
    } // if
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return this.last.getBlock().getHash();
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   * @throws NoSuchAlgorithmException
   */
  public boolean isCorrect() throws NoSuchAlgorithmException {
    // check balance
    for (int i = 0; i < balance.size(); i++) {
      if (balance.getElement(i).getVal() < 0) {
        return false;
      } // if
    } // for
    if (this.length == 0) {
      return true;
    } // if
    BlockNode currNode = this.first.getNext();
    // store prev before compute changes hash
    Hash prev = currNode.getBlock().getHash();
    currNode.getBlock().computeHash();
    // check case c, d
    if (prev != currNode.getBlock().getHash() || !simpleValidator.isValid(prev)) {
      return false;
    } // if
    Hash curr;
    while (currNode.getNext() != null) {
      currNode = currNode.getNext();
      curr = currNode.getBlock().getHash();
      currNode.getBlock().computeHash();
      // check case b, c, d
      if (!prev.equals(curr) || curr != currNode.getBlock().getHash()
          || !simpleValidator.isValid(curr)) {
        return false;
      } // if
      prev = curr;
    } // while
    return true;
  } // isCorrect()


  static Iterator<String> users() {
    Iterator<String> it = new Iterator<String>() {

      private int index = 0;

      public boolean hasNext() {
        return index < balance.size();
      } // hasNext

      public String next() {
        return balance.getElement(index).getKey();
      } // next
    };
    return it;
  } // users

  static Iterator<Block> blocks() {
    Iterator<Block> it = new Iterator<Block>() {

      private int index = 0;

      public boolean hasNext() {
        return index < length;
      } // hasNext

      public Block next() {
        index++;
        first = first.getNext();
        return first.getBlock();
      } // Next
    };
    return it;
  } // blocks

  static Iterator<Transaction> entries() {
    Iterator<Transaction> it = new Iterator<Transaction>() {

      private int index = 0;

      public boolean hasNext() {
        return index < length;
      } // hasNext

      public Transaction next() {
        index++;
        first = first.getNext();
        return first.getBlock().getTransaction();
      } // Next
    };
    return it;
  } // entries

} // class BlockChain
