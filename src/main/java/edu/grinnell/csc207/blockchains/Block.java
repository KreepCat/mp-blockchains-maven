package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Alex Pollock
 * @author Kevin Tang
 * @author Samuel A. Rebelsky
 */
public class Block {

  /**
   * The byte buffer used for ints.
   */
  static ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);

  /**
   * The byte buffer used for longs.
   */
  static ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);


  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /**
   * The position of the block in the chain.
   */
  int num;

  /**
   * The transaction on the block.
   */
  Transaction transaction;

  /**
   * The previous hash.
   */
  Hash prevHash;

  /**
   * The hash on this block.
   */
  Hash currHash;

  /**
   * The message digest used to generate the hash.
   */
  MessageDigest md;

  /**
   * The nonce.
   */
  long nonce;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param number The number of the block.
   * @param transact The transaction for the block.
   * @param preveiousHash The hash of the previous block.
   * @param check The validator used to check the block.
   */
  public Block(int number, Transaction transact, Hash preveiousHash, HashValidator check) {
    this.num = number;
    this.transaction = transact;
    this.prevHash = preveiousHash;
    Random rand = new Random();
    do {
      this.nonce = rand.nextLong();
      try {
        this.computeHash();
      } catch (NoSuchAlgorithmException e) {
      } // try/catch
    } while (!check.isValid(currHash));
    try {
      computeHash();
    } catch (NoSuchAlgorithmException e) {
    } // try/catch
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param number The number of the block.
   * @param transact The transaction for the block.
   * @param previousHash The hash of the previous block.
   * @param inputNonce The nonce of the block.
   */
  public Block(int number, Transaction transact, Hash previousHash, long inputNonce) {
    this.num = number;
    this.transaction = transact;
    this.prevHash = previousHash;
    this.nonce = inputNonce;
    try {
      this.computeHash();
    } catch (NoSuchAlgorithmException e) {
    } // try/catch
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Convert an integer into its bytes.
   *
   * @param i The integer to convert.
   *
   * @return The bytes of that integer.
   */
  static byte[] intToBytes(int i) {
    intBuffer.clear();
    return intBuffer.putInt(i).array();
  } // intToBytes(int)

  /**
   * Convert a long into its bytes.
   *
   * @param l The long to convert.
   *
   * @return The bytes in that long.
   */
  static byte[] longToBytes(long l) {
    longBuffer.clear();
    return longBuffer.putLong(l).array();
  } // longToBytes()

  /**
   * Compute the hash of the block given all the other info already stored in the block.
   *
   * @throws NoSuchAlgorithmException
   */
  void computeHash() throws NoSuchAlgorithmException {
    md = MessageDigest.getInstance("sha-256");
    md.update(intToBytes(this.getNum()));
    md.update(this.getTransaction().getSource().getBytes());
    md.update(this.getTransaction().getTarget().getBytes());
    md.update(intToBytes(this.getTransaction().getAmount()));
    md.update(this.getPrevHash().getBytes());
    md.update(longToBytes(this.getNonce()));
    this.currHash = new Hash(md.digest());
  } // computeHash()

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
  public Hash getPrevHash() {
    return this.prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  public Hash getHash() {
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
