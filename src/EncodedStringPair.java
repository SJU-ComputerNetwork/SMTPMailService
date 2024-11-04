
public class EncodedStringPair<K, V> {
	public K str;
    public V encodingMethod;

    public EncodedStringPair(K key, V value) {
        this.str = key;
        this.encodingMethod = value;
    }
}
