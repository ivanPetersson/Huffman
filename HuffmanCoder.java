/*
 * Authors: Ivan Petersson ivpe1346, Jakob Vikström javi1669
 * */

import java.util.*;

public class HuffmanCoder {
    Map<Character, Integer> characters;
    Map<Character, Node> nodes = new HashMap<>();
    Node root;

    public EncodedMessage<?, ?> encode(String msg) {
        characters = setup(msg);
        StringBuilder code = new StringBuilder();
        root = buildHuffmanTree();
        createBinaryCode(root, "");
        for (char c : msg.toCharArray()) {
            Node node = nodes.get(c);
            code.append(node.binaryCode);
        }
        EncodedMessage<Node, String> message = new EncodedMessage<>(root, code.toString());
        return message;
    }

    public String decode(EncodedMessage<?, ?> msg) {
        StringBuilder message = new StringBuilder();
        char[] binaryMessage = msg.message.toString().toCharArray();
        root = (Node) msg.header;
        Node current = root;
        for (char c : binaryMessage) {
            if (c == '0') {
                current = current.left;
            } else if (c == '1') {
                current = current.right;
            }
            if (current.left == null && current.right == null) {
                message.append(current.character);
                current = root;
            }
        }
        return message.toString();
    }

    public Map<Character, Integer> setup(String msg) {
        characters = new HashMap<>();
        for (char ch : msg.toCharArray()) {
            characters.putIfAbsent(ch, 0);
            if (characters.containsKey(ch))
                characters.put(ch, characters.get(ch) + 1);
        }
        return characters;
    }

    public Node buildHuffmanTree() {
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : characters.entrySet()) {
            Node node = new Node(entry.getKey(), entry.getValue());
            nodes.put(node.character, node);
            nodeQueue.add(node);
        }
        while (nodeQueue.size() != 1) {
            Node left = nodeQueue.poll();
            Node right = nodeQueue.poll();
            Node parent = new Node(left.frequency + right.frequency, left, right);
            nodeQueue.add(parent);
        }
        return nodeQueue.poll();
    }

    public void createBinaryCode(Node node, String code) {
        if (!node.hasChildren()) {
            node.binaryCode = code;
            node.compressed = true;
            createBinaryCode(root, "");
        } else if (!node.left.compressed)
            createBinaryCode(node.left, code + "0");
        else if (!node.right.compressed)
            createBinaryCode(node.right, code + "1");
        else if (node.left.compressed && node.right.compressed && !(root.left.compressed && root.right.compressed)) {
            node.compressed = true;
            createBinaryCode(root, "");
        }
    }

    // det här är en y kommentar
    class Node implements Comparable<Node> {
        Character character;
        int frequency;
        Node left, right;
        String binaryCode;
        boolean compressed;

        public Node(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
            this.left = null;
            this.right = null;
        }

        public Node(int frequency, Node left, Node right) {
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        public boolean hasChildren() {
            return left != null && right != null;
        }

        @Override
        public int compareTo(Node o) {
            return this.frequency - o.frequency;
        }

        public String toString() {
            return character + " " + frequency;
        }

    }

}