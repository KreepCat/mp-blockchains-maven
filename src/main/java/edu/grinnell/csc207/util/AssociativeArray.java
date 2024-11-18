package edu.grinnell.csc207.util;

import static java.lang.reflect.Array.newInstance;

/**
 * A basic implementation of Associative Arrays with keys of type K
 * and values of type V. Associative Arrays store key/value pairs
 * and permit you to look up values by key.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @author Kevin Tang
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V>[] pairs;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings({ "unchecked" })
  public AssociativeArray() {
    // Creating new arrays is sometimes a PITN.
    this.pairs = (KVPair<K, V>[]) newInstance((new KVPair<K, V>()).getClass(),
        DEFAULT_CAPACITY);
    this.size = 0;
  } // AssociativeArray()

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   *
   * @return a new copy of the array
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> clone = new AssociativeArray<K, V>();
    for (int i = 0; i < this.size; i++) {
      try {
        clone.set(this.pairs[i].key, this.pairs[i].val);
      } catch (NullKeyException | KeyNotFoundException e) {
      } // try catch
    } // for loop
    return clone;
  } // clone()

  /**
   * Convert the array to a string.
   *
   * @return a string of the form "{Key0:Value0, Key1:Value1, ... KeyN:ValueN}"
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < this.size; i++) {
      str.append(this.pairs[i].toString());
      if (i < this.size - 1) {
        str.append(", ");
      } //if statement
    } //for loop
    return ("{" + str.toString() + "}");
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to
   * get(key) will return value.
   *
   * @param key The key whose value we are seeting.
   * @param value The value of that key.
   *
   * @throws NullKeyException If the client provides a null key.
   * @throws KeyNotFoundException If key not found.
   */
  public void set(K key, V value) throws NullKeyException, KeyNotFoundException {
    if (key == null) {
      throw new NullKeyException();
    } //if statement
    if (this.size == this.pairs.length) {
      this.expand();
    } //if statement
    if (!this.hasKey(key)) {
      this.pairs[this.size] = new KVPair<>(key, value);
      size++;
    } else {
      this.pairs[this.find(key)].val = value;
    } //if statement
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @param key A key
   * @return value stored at that key
   * @throws KeyNotFoundException when the key is null or does not appear in the associative array.
   */
  public V get(K key) throws KeyNotFoundException {
    return this.pairs[this.find(key)].val;
  } // get(K)

  /**
   * Determine if key appears in the associative array. Should
   * return false for the null key.
   * @param key A key
   * @return whether key exist
   */
  public boolean hasKey(K key) {
    for (int i = 0; i < this.size; i++) {
      if (this.pairs[i].key.equals(key)) {
        return true;
      } //if statement
    } //for loop
    return false;
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls
   * to get(key) will throw an exception. If the key does not appear
   * in the associative array, does nothing.
   * @param key A key
   */
  public void remove(K key) {
    if (this.hasKey(key)) {
      try {
        int index = this.find(key);
        this.pairs[index].key = null;
        this.pairs[index].val = null;
        --this.size;
        if (index != this.size) {
          this.pairs[index].key = this.pairs[this.size].key;
          this.pairs[index].val = this.pairs[this.size].val;
        } //if statement
      } catch (KeyNotFoundException e) {
      } //try catch
    } //if statement
  } // remove(K)

  /**
   * Determine how many key/value pairs are in the associative array.
   * @return size of the array
   */
  public int size() {
    return this.size;
  } // size()

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Expand the underlying array.
   */
  void expand() {
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key.
   * If no such entry is found, throws an exception.
   *
   * @param key The key of the entry.
   * @return index of the key
   * @throws KeyNotFoundException If the key does not appear in the associative array.
   */
  int find(K key) throws KeyNotFoundException {
    for (int i = 0; i < this.size; i++) {
      if (this.pairs[i].key.equals(key)) {
        return i;
      } //if statement
    } //for loop
    throw new KeyNotFoundException();
  } // find(K)

  public KVPair<K, V> getElement(int index) {
    return this.pairs[index];
}

} // class AssociativeArray
