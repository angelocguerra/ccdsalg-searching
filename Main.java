import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;


public class Main {

    static int collisionCount = 0;
    static int collisionCount2 = 0;

    public static void main(String[] args) throws NoSuchAlgorithmException {

        Scanner scan = new Scanner(System.in);
        Scanner scan2 = new Scanner(System.in);
        int userInput, numChar, k, hash;
        String DNA;

        System.out.println("MCO2: Machine Project Sorting");
        System.out.println("Please input your choice: ");
        System.out.println("[1] - Hash Table");
        System.out.println("[2] - Binary Search Tree");
        userInput = scan.nextInt();
        scan.nextLine();

        switch (userInput){
            case 1:
                System.out.println("Enter the number of characters in the DNA Sequence: ");
                numChar = scan2.nextInt();
                scan2.nextLine();
                System.out.println("Input k for k-mer distribution:");
                k = scan2.nextInt();
                scan2.nextLine();
                System.out.println("Select your Hashing Function: ");
                System.out.println("[1] - MD5");
                System.out.println("[2] - SHA-256");
                hash = scan2.nextInt();
                scan2.nextLine();
                DNA = dnaRandomizer(numChar);
                System.out.println(DNA);
                switch (hash){
                    case 1:
                        printSubstringsHT(createSubstringsHTMD5(DNA, k));
                        System.out.println("Collision count: " + collisionCount);
                        break;
                    case 2:
                        printSubstringsHT(createSubstringsHTSHA(DNA, k));
                        System.out.println("Collision count: " + collisionCount2);
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                System.out.println("Enter the number of characters in the DNA Sequence: ");
                numChar = scan2.nextInt();
                scan2.nextLine();
                System.out.println("Input k for k-mer distribution:");
                k = scan2.nextInt();
                scan2.nextLine();
                DNA = dnaRandomizer(numChar);
                System.out.println(DNA);
                BinaryTree substrings = createSubstringsBST(DNA, k);
                substrings.inOrderTraversal(substrings.root);
                substrings.deleteTree();
                break;
            default:
                break;
        }
    }

    private static String dnaRandomizer(int n){
        StringBuilder DNA = new StringBuilder("2"); int num;
        Random rand = new Random();
        int upperBound = 4;
        num = rand.nextInt(upperBound);
        switch (num) {
            case (0) -> DNA = new StringBuilder("a");
            case (1) -> DNA = new StringBuilder("c");
            case (2) -> DNA = new StringBuilder("g");
            case (3) -> DNA = new StringBuilder("t");
        }
        for (int i = 0; i < n-1; i++){
            num = rand.nextInt(upperBound);
            switch (num) {
                case (0) -> DNA.append("a");
                case (1) -> DNA.append("c");
                case (2) -> DNA.append("g");
                case (3) -> DNA.append("t");
            }
        }

        return DNA.toString();
    }

    private static String bytesToHex(byte[] hash){
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String encryptStringSha(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return bytesToHex(md.digest(input.getBytes(StandardCharsets.UTF_8)));
    }

    private static String encryptString(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] messageDigest = md.digest(input.getBytes());

        BigInteger bigInt = new BigInteger(1, messageDigest);

        return bigInt.toString(16);
    }

    private static Hashtable<String, String> createSubstringsHTSHA(String DNA, int size) throws NoSuchAlgorithmException {
        Hashtable<String, String> substrings = new Hashtable<>();
        String tempoKey;
        for (int i = DNA.length()-size; i >= 0; i--){
            String tempo = DNA.substring(i, i+size);
            tempoKey = encryptStringSha(tempo);
            if (!substrings.containsValue(tempo)){
                while (substrings.containsKey(tempoKey)){
                    collisionCount2++;
                    tempoKey += 1;
                    System.out.println("Collision at key " + tempoKey + ". Trying " + tempoKey+1);
                }
                substrings.put(tempoKey, tempo);
            }
        }
        return substrings;
    }

    private static Hashtable<String, String> createSubstringsHTMD5(String DNA, int size) throws NoSuchAlgorithmException {
        Hashtable<String, String> substrings = new Hashtable<>();
        String encryptedKey;
        for (int i = DNA.length()-size; i >= 0; i--){
            String tempo = DNA.substring(i, i+size);
            encryptedKey = encryptString(tempo);
            if (!substrings.containsValue(tempo)){
                while (substrings.containsKey(encryptedKey)){
                    System.out.println("Collision at key " + encryptedKey + ". Trying " + encryptedKey+1);
                    encryptedKey += 1;
                    collisionCount++;
                }
                substrings.put(encryptString(tempo), tempo);
            }
        }
        return substrings;
    }

    private static void printSubstringsHT(Hashtable<String, String> substrings){
        for (String key: substrings.keySet()){
            System.out.println(key + ": " + substrings.get(key));
        }
    }

    private static BinaryTree createSubstringsBST(String DNA, int size){
        BinaryTree substrings = new BinaryTree();
        for (int i = DNA.length()-size; i>= 0; i--){
            String tempo = DNA.substring(i, i+size);
            if (!substrings.contains(substrings.root, tempo)){
                substrings.addNode(tempo);
            }
        }
        return substrings;
    }
}

class Node{
    String name;

    Node leftChild;
    Node rightChild;

    Node(String name){
        this.name = name;
    }
}

class BinaryTree {

    Node root;

    public void addNode(String name){
        Node newNode = new Node(name);
        Node focusNode;
        Node parent;

        if (root == null){
            root = newNode;
        } else {
            focusNode = root;
            while (true){
                parent = focusNode;
                if (newNode.name.compareTo(focusNode.name) < 0){
                    focusNode = focusNode.leftChild;
                    if (focusNode == null){
                        parent.leftChild = newNode;
                        return;
                    }
                } else {
                    focusNode = focusNode.rightChild;
                    if (focusNode == null){
                        parent.rightChild = newNode;
                        return;
                    }
                }
            }
        }
    }

    public boolean contains(Node node, String value){
        if (node == null) return false;
        else if (node.name.equals(value)) return true;
        else return contains(node.leftChild, value) || contains(node.rightChild, value);
    }

    public void inOrderTraversal(Node focusNode){
        if (focusNode != null){
            inOrderTraversal(focusNode.leftChild);
            System.out.println(focusNode.name);
            inOrderTraversal(focusNode.rightChild);
        }
    }

    public void deleteTree(){
        root = null;
    }
}
