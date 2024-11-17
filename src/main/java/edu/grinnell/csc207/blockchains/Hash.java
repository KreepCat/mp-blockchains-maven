package edu.grinnell.csc207.blockchains;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Encapsulated hashes.
 *
 * @author Alex Pollock
 * @author Kevin Tang
 * @author Samuel A. Rebelsky
 */
public class Hash {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  private byte[] bytes;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new encapsulated hash.
   *
   * @param data The data to copy into the hash.
   */
  public Hash(byte[] data) {
    this.bytes = data;
  } // Hash(byte[])

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Determine how many bytes are in the hash.
   *
   * @return the number of bytes in the hash.
   */
  public int length() {
    return this.bytes.length;
  } // length()

  /**
   * Get the ith byte.
   *
   * @param i The index of the byte to get, between 0 (inclusive) and length() (exclusive).
   *
   * @return the ith byte
   */
  public byte get(int i) {
    return this.bytes[i];
  } // get()

  /**
   * Get a copy of the bytes in the hash. We make a copy so that the client cannot change them.
   *
   * @return a copy of the bytes in the hash.
   */
  public byte[] getBytes() {
    return Arrays.copyOf(bytes, bytes.length);
  } // getBytes()

  /**
   * Convert to a hex string.
   *
   * @return the hash as a hex string.
   */
  public String toString() {
    String tmp = new String();
    StringBuilder toReturn = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      toReturn.append(String.format(tmp, Byte.toUnsignedInt(bytes[i])));
    } // for
    return toReturn.toString();
  } // toString()

  /**
   * Determine if this is equal to another object.
   *
   * @param other The object to compare to.
   *
   * @return true if the two objects are conceptually equal and false otherwise.
   */
  public boolean equals(Object other) {
    return ((other instanceof Hash) && (this.equals((Hash) other)));
  } // equals(Object)

  /**
   * Determine if another Hash is equal to this hash
   *
   * @param other The Hash to compare to this Hash.
   *
   * @return true if the two hashes are structurally equivalent and false otherwise.
   */
  public boolean equals(Hash other) {
    return (other.bytes.equals(this.bytes));
  } // eqv(Hash)

  /**
   * Get the hash code of this object.
   *
   * @return the hash code.
   */
  public int hashCode() {
    return this.toString().hashCode();
  } // hashCode()
} // class Hash
