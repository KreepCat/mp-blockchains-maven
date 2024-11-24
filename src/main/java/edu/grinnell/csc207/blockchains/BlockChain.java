package edu.grinnell.csc207.blockchains;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

/**
 * A full blockchain.
 *
 * @author Alex Pollock
 * @author Kevin Tang
 */
public class BlockChain {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /**
   * First Node.
   */
  BlockNode first;
  /**
   * Last Node.
   */
  BlockNode last;
  /**
   * Length of chain.
   */
  int length;
  /**
   * Hash validator.
   */
  HashValidator simpleValidator = (hash) -> (hash.length() >= 1) && (hash.get(0) == 0);
  /**
   * Associative array to store balance.
   */
  AssociativeArray<String, Integer> balance;


  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check The validator used to check elements.
   */
  public BlockChain(HashValidator check) {
    this.length = 1;
    this.simpleValidator = check;
    this.first = new BlockNode(null, null,
        new Block(0, new Transaction("", "", 0), new Hash(new byte[] {}), simpleValidator));
    this.last = this.first;
    this.balance = new AssociativeArray<String, Integer>();
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
    if (amt < 0) {
      amt = Integer.MAX_VALUE;
    }
    if (balance.hasKey(from) && mode == 1) {
      this.balance.set(from, balance.get(from) - amt);
      deposit(blk, mode);
    } else if (balance.hasKey(from) && mode == 0) {
      this.balance.set(from, balance.get(from) + amt);
      deposit(blk, mode);
    } else if (from.equals("")) {
      deposit(blk, mode);
    } else {
      if (mode == 1) {
        this.balance.set(from, 0 - amt);
      } else {
        this.balance.set(from, amt);
      }
    } // if/else
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
    } // if/else
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
   * @throws KeyNotFoundException
   * @throws NullKeyException
   */
  public Block mine(Transaction t) throws NullKeyException, KeyNotFoundException {
    Block tmp = new Block(length, t, getHash(), simpleValidator);
    return tmp;
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
   * @throws KeyNotFoundException
   * @throws NullKeyException
   *
   * @throws IllegalArgumentException if (a) the hash is not valid, (b) the hash is not appropriate
   *         for the contents, or (c) the previous hash is incorrect.
   */
  public void append(Block blk) throws IllegalArgumentException {
    if (!(simpleValidator.isValid(blk.getHash()))) {
      throw new IllegalArgumentException();
    }
    Block sampleBlock = new Block(blk.getNum(), blk.getTransaction(),
        this.last.getBlock().getHash(), blk.getNonce());
    // try {
    // sampleBlock.computeHash();
    // } catch (NoSuchAlgorithmException e) {
    // }
    if (!(blk.getHash().equals(sampleBlock.getHash()))) {
      throw new IllegalArgumentException();
    }
    BlockNode dummy = new BlockNode(this.last, null, blk);
    try {
      transaction(blk, 1);
    } catch (Exception e) {
    }
    this.last.setNext(dummy);
    this.last = dummy;
    this.length++;
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's not removed) or true
   *         otherwise (in which case the last block is removed).
   * @throws KeyNotFoundException
   * @throws NullKeyException
   */
  public boolean removeLast() {
    if (this.length <= 1) {
      return false;
    } else {
      try {
        transaction(this.last.getBlock(), 0);
      } catch (Exception e) {
      } // try/catch
      this.last = this.last.getPrev();
      this.last.setNext(null);
      this.length--;
      return true;
    } // if/else
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last block in the chain.
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
    if (this.length == 1) {
      return true;
    } // if
    BlockNode currNode = this.first.getNext();
    Block currBlock = currNode.getBlock();
    // store prev before compute changes hash
    Hash prev = currNode.getBlock().getHash();
    Block sampleBlock = new Block(currBlock.getNum(), currBlock.getTransaction(), currNode.getPrev().getBlock().getHash(), currBlock.getNonce());
    //currNode.getBlock().computeHash();
    // check case c, d
    if (!prev.equals(sampleBlock.getHash()) || !simpleValidator.isValid(prev)) {
      return false;
    } // if
    Hash curr;
    while (currNode.getNext() != null) {
      currNode = currNode.getNext();
      curr = currNode.getBlock().getHash();
      //currNode.getBlock().computeHash();
      currBlock = currNode.getBlock();
      sampleBlock = new Block(currBlock.getNum(), currBlock.getTransaction(), currNode.getPrev().getBlock().getHash(), currBlock.getNonce());
      // check case b, c, d
      if (!prev.equals(currNode.getPrev().getBlock().getHash()) || !curr.equals(sampleBlock.getHash())
          || !simpleValidator.isValid(curr)) {
        return false;
      } // if
      prev = curr;
    } // while
    return true;
  } // isCorrect()

  public void reset() throws NullKeyException, KeyNotFoundException {
    BlockNode curr = this.first.getNext();
    // for (int i = 0; i < balance.size(); i++) {
    // balance.remove(balance.getElement(0).getKey());
    // }
    this.balance = new AssociativeArray<String, Integer>();
    while (curr.getNext() != null) {
      transaction(curr.getBlock(), 1);
      curr = curr.getNext();
    }
  }


  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception If things are wrong at any block.
   */
  public void check() throws Exception {
    if (this.isCorrect()) {
      return;
    } // if
    throw new Exception();
  } // check()

  /**
   * Return an iterator of all the people who participated in the system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    Iterator<String> it = new Iterator<String>() {

      private int index = 0;

      public boolean hasNext() {
        return index < balance.size();
      } // hasNext

      public String next() {
        return balance.getElement(index++).getKey();
      } // next
    };
    return it;
  } // users

  /**
   * Find one user's balance.
   *
   * @param user The user whose balance we want to find.
   *
   * @return that user's balance (or 0, if the user is not in the system).
   * @throws NullKeyException
      * @throws KeyNotFoundException 
      */
     public int balance(String user) throws NullKeyException, KeyNotFoundException {
    this.reset();
    BlockNode currNode = this.first;
    int total = 0;
    while (currNode != null) {
      Transaction thisTransaction = currNode.getBlock().getTransaction();
      if (thisTransaction.getSource().equals(user)) {
        total -= thisTransaction.getAmount();
      }
      if (thisTransaction.getTarget().equals(user)) {
        total += thisTransaction.getAmount();
      }
      currNode = currNode.getNext();
    }
    return total;
  } // balance()

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    Iterator<Block> it = new Iterator<Block>() {
      private int index = 0;


      public boolean hasNext() {
        return index < length;
      } // hasNext

      public Block next() {
        BlockNode val = first;
        first = first.getNext();
        index++;

        // if (index > 1) {
        // try {
        // transaction(val.getBlock(), 0);
        // transaction(val.getPrev().getBlock(), 1);
        // } catch (NullKeyException | KeyNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // } else {
        // try {
        // transaction(val.getBlock(), 0);
        // } catch (NullKeyException | KeyNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        return val.getBlock();
      } // Next
    };
    return it;
  } // blocks

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Transaction> entries() {
    Iterator<Transaction> it = new Iterator<Transaction>() {

      private int index = 0;

      public boolean hasNext() {
        return index < length;
      } // hasNext

      public Transaction next() {
        BlockNode val = first;
        first = first.getNext();
        index++;
        return val.getBlock().getTransaction();
      } // next()
    };
    return it;
  } // entries

} // class BlockChain
