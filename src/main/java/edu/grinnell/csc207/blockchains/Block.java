package edu.grinnell.csc207.blockchains;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  int num;
  Transaction transaction;
  Hash prevHash;
  long nonce;
  Hash currHash;
  MessageDigest md;
  HashValidator simpleValidator = (hash) -> (hash.length() >= 1) && (hash.get(0) == 0);


  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param check The validator used to check the block.
   * @throws NoSuchAlgorithmException
   */
  Block(int num, Transaction transaction, Hash prevHash, HashValidator check)
      throws NoSuchAlgorithmException {
    this.num = num;
    this.transaction = transaction;
    this.prevHash = prevHash;
    this.nonce = BlockChain.mine(transaction);
    md = MessageDigest.getInstance("sha-256");
    // STUB (what does it mean "mining to choose a nonce"?)
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param nonce The nonce of the block.
   * @throws NoSuchAlgorithmException
   */
  Block(int num, Transaction transaction, Hash prevHash, long nonce)
      throws NoSuchAlgorithmException {
    this.num = num;
    this.transaction = transaction;
    this.prevHash = prevHash;
    this.nonce = nonce;
    md = MessageDigest.getInstance("sha-256");
    computeHash();
    // STUB
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already stored in the block.
   * 
   * @throws NoSuchAlgorithmException
   */
  void computeHash() throws NoSuchAlgorithmException {
    md.update(this.transaction.toString().getBytes());
    md.update((byte) this.num);
    md.update(this.prevHash.getBytes());
    md.update((byte) this.nonce);
    this.currHash = new Hash(md.digest());
    // STUB
  } // computeHash()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  int getNum() {
    return this.num;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  Transaction getTransaction() {
    return this.transaction;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  long getNonce() {
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
    return this.currHash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    return "Block " + num + "(Transaction: " + this.transaction.toString() + ", Nonce: "
        + this.nonce + ", prevHash: " + this.prevHash + ", hash: " + this.currHash.toString() + ")";
  } // toString()
} // class Block
