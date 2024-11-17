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
  BlockNode first;
  BlockNode last;
  int length;
  HashValidator simpleValidator = (hash) -> (hash.length() >= 1) && (hash.get(0) == 0);
  AssociativeArray<String, Integer> balance = new AssociativeArray<>();


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
    this.first = new BlockNode(null, null, mine(new Transaction(null, null, 0)));
    this.last = this.first;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+
  //mode = 1 if append a block (add new transaction), mode = 0 if remove a block (step back for a transaction)
  public void transaction(Block blk, int mode) throws NullKeyException, KeyNotFoundException {
    String from = blk.transaction.getSource();
    int amt = blk.transaction.getAmount();
    if (balance.hasKey(from) && mode == 1) {
      balance.set(from, balance.get(from) - amt);
    } else if (balance.hasKey(from) && mode == 0) {
      balance.set(from, balance.get(from) + amt);
    } else {
      deposit(blk, mode);
    }
    BlockNode newNode = new BlockNode(this.last, null, blk);
    this.last.setNext(newNode);
  } // transaction()


  public void deposit(Block blk, int mode) throws NullKeyException, KeyNotFoundException {
    String to = blk.transaction.getTarget();
    int amt = blk.transaction.getAmount();
    if (balance.hasKey(to) && mode == 1) {
      balance.set(to, balance.get(to) + amt);
    } else if (balance.hasKey(to) && mode == 0) {
      balance.set(to, balance.get(to) - amt);
    }else {
      balance.set(to, amt);
    }
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
  public Block mine(Transaction t) throws NoSuchAlgorithmException {
    long nonce;
    Random random = new Random();
    Block tmpBlock;
    do {
      nonce = random.nextLong();
      tmpBlock = new Block(length, t, this.last.getBlock().prevHash, nonce);
    } while (!simpleValidator.isValid(tmpBlock.getHash()));
    return tmpBlock;
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
      */
  public void append(Block blk) throws NullKeyException, KeyNotFoundException {
    transaction(blk, 1);
    BlockNode newNode = new BlockNode(this.last, null, blk);
    this.last.setNext(newNode);
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
      transaction(this.last.info, 0);
      this.last = this.last.getPrev();
      this.last.setNext(null);
      return true;
    }
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return this.last.info.currHash;
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   */
  public boolean isCorrect() {
    for(int i = 0; i < balance.size(); i++) {
      if (balance.getElement(i).getVal() < 0) {
        return false;
      }
    }
    //STUB (I don't quite understand what should we do to check hash (there're 3 cases)). Is it enough just using hashvalidator?)
  } // isCorrect()

  
  Iterator<String> users(){
    Iterator<String> it = new Iterator<String>() {

      private int index = 0;

      public boolean hasNext() {
        return index < balance.size();
      }

      public String next() {
        return balance.getElement(index).getKey();
      }
    };
    return it;
  }

  Iterator<Block> blocks(){
    Iterator<Block> it = new Iterator<Block>() {

      private int index = 0;
      
      public boolean hasNext() {
        return index < length;
      }

      public Block next() {
        index++;
        return first.info;
      }
    };
    return it;
  }

  Iterator<Transaction> entries(){
    Iterator<Transaction> it = new Iterator<Transaction>() {

      private int index = 0;
      
      public boolean hasNext() {
        return index < length;
      }

      public Transaction next() {
        index++;
        return first.info.getTransaction();
      }
    };
    return it;
  }

} // class BlockChain
