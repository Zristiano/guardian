package model;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> {

    private class Node{
        K key;
        V val;
        Node prev;
        Node next;
        Node(K k, V v, Node p, Node n){
            key = k;
            val = v;
            prev = p;
            next = n;
        }
    }

    private int capacity;
    private Node head;
    private Node tail;
    private Map<K,Node> map;


    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = new Node(null, null,null,null);
        tail = new Node(null, null, head,null);
        head.next = tail;
        map = new HashMap<>();
    }

    public V get(K key) {
        Node node = map.get(key);
        if(node==null) return null;
        node.prev.next = node.next;
        node.next.prev = node.prev;

        node.prev = tail.prev;
        node.next = tail;

        tail.prev.next = node;
        tail.prev = node;
        return node.val;
    }

    public void put(K key, V value) {
        Node node = map.get(key);
        if(node != null){
            node.val = value;
            return;
        }

        node = new Node(key,value,tail.prev,tail);
        tail.prev.next = node;
        tail.prev = node;
        map.put(key,node);

        if(map.size() > capacity){
            Node rm = head.next;
            map.remove(rm.key);
            head.next = rm.next;
            rm.next.prev = head;
        }
    }

}
