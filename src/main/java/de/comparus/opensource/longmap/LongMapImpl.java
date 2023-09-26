package de.comparus.opensource.longmap;

import java.util.*;

public class LongMapImpl<V> implements LongMap<V> {
    private Node<Long, V>[] nodeMass;
    private int size = 0;
    private int threshold;

    public LongMapImpl() {
        nodeMass = new Node[16];
        threshold = (int) (nodeMass.length * 0.75);
    }

    @Override
    public V put(long key, V value) {
        checkForWorkload();

        Node<Long, V> newNode = new Node<>(key, value);
        int index = newNode.hash;

        if (Objects.isNull(nodeMass[index])) {
            nodeMass[index] = new Node<>(null, null);
            nodeMass[index].getNodes().add(newNode);
            size++;
        }

        List<Node<Long, V>> existedNodes = nodeMass[index].getNodes();
        for (int i = 0; i < existedNodes.size(); i++) {
            if (Objects.equals(newNode.getKey(), existedNodes.get(i).getKey())
                    && (!Objects.equals(newNode.getValue(), existedNodes.get(i).getValue()))) {
                newNode.setValue(value);
            }
            if (existedNodes.get(i).hashCode() == newNode.hashCode()
                    && (!Objects.equals(existedNodes.get(i).getKey(), newNode.getKey()))) {
                existedNodes.add(newNode);
                size++;
            }
        }
        return value;
    }

    @Override
    public V get(long key) {
        int index = cell(key);
        if (index < nodeMass.length && nodeMass[index] != null) {
            List<Node<Long, V>> allExistedNodes = nodeMass[index].getNodes();
            for (Node<Long, V> node : allExistedNodes) {
                if (Objects.equals(node.getKey(), key)) {
                    return node.getValue();
                }
            }
        }
        throw new RuntimeException(String.format("Value with key %s does not exist", key));
    }

    @Override
    public V remove(long key) {
        int index = cell(key);
        List<Node<Long, V>> allNodesByIndex = nodeMass[index].getNodes();

        V deletedValue = null;
        for (Node<Long, V> node : allNodesByIndex) {
            if (Objects.equals(node.getKey(), key)) {
                allNodesByIndex.remove(node);
                deletedValue = node.getValue();
                size--;
            }
        }
        return deletedValue;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        try {
            return get(key) != null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public boolean containsValue(V value) {
        if (Objects.nonNull(value) && size > 0) {
            for (Node<Long, V> nMass : nodeMass) {
                if (Objects.isNull(nMass)) {
                    continue;
                }
                for (Node<Long, V> node : nMass.getNodes()) {
                    if (Objects.equals(value, node.getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public long[] keys() {
        Set<Long> keys = new HashSet<>();
        for (Node<Long, V> nMass : nodeMass) {
            if (Objects.isNull(nMass)) {
                continue;
            }
            for (Node<Long, V> node : nMass.getNodes()) {
                keys.add(node.getKey());
            }
        }
        long[] longs = new long[keys.size()];

        Iterator<Long> value = keys.iterator();
        for (int i = 0; i < longs.length; i++) {
            longs[i] = value.next();
        }

        return longs;
    }

    @Override
    public V[] values() {
        List<V> values = new ArrayList<>();
        for (Node<Long, V> nMass : nodeMass) {
            if (Objects.isNull(nMass)) {
                continue;
            }
            for (Node<Long, V> node : nMass.getNodes()) {
                values.add(node.getValue());
            }
        }
        return (V[]) values.toArray(new Object[0]);
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        if (nodeMass != null && size > 0) {
            Arrays.stream(nodeMass).forEach(node -> node = null);
        }
        size = 0;
    }

    private void checkForWorkload() {
        if (size + 1 >= threshold) {
            threshold *= 2;
            Node<Long, V>[] previousNodeMass = nodeMass;
            size = 0;
            nodeMass = new Node[nodeMass.length * 2];

            Arrays.stream(previousNodeMass).forEach(node -> {
                if (node != null) {
                    node.getNodes().forEach(n -> put(n.getKey(), n.getValue()));
                }
            });
        }
    }

    private int cell(Long key) {
        if (key == null) {
            return 0;
        }
        return (int) ((key.hashCode() & Long.MAX_VALUE) % 16);
    }


    private class Node<K, V> {
        private K key;
        private V value;
        private int hash;
        private List<Node<K, V>> nodes;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            nodes = new LinkedList<>();
            hash = hash();
        }

        public List<Node<K, V>> getNodes() {
            return nodes;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public int hash() {
            return hashCode() % nodeMass.length;
        }

        @Override
        public int hashCode() {
            if (key == null) {
                return 0;
            }
            return (int) ((key.hashCode() & Long.MAX_VALUE) % 16);
        }
    }
}
