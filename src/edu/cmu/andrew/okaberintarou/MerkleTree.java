package edu.cmu.andrew.okaberintarou;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class MerkleTree {

    private SinglyLinkedList list;

    public MerkleTree(SinglyLinkedList text) throws NoSuchAlgorithmException {
        if (text.countNodes() < 1) {
            throw new IllegalArgumentException("empty line, expect at least one line");
        }
        list = new SinglyLinkedList();
        if ((text.countNodes() & 1) == 1) {
            text.addAtEndNode(text.getLast());
        }
        list.addAtEndNode(text);


        SinglyLinkedList newList = new SinglyLinkedList();

        text.reset();
        while (text.hasNext()) {
            String line = (String) text.next();
            newList.addAtEndNode(h(line));
        }
        list.addAtEndNode(newList);

        for (SinglyLinkedList cur = (SinglyLinkedList) list.getLast();
             cur.countNodes() != 1;
             cur = (SinglyLinkedList) list.getLast()) {

            newList = new SinglyLinkedList();
            cur.reset();
            while (cur.hasNext()) {
                String h1 = (String) cur.next();
                assert (cur.hasNext());
                String h2 = (String) cur.next();
                newList.addAtEndNode(h(h1 + h2));
            }

            if (newList.countNodes() > 1 && (newList.countNodes() & 1) == 1) {
                newList.addAtEndNode(newList.getLast());
            }
            list.addAtEndNode(newList);
        }
    }

    public String getRoot() {
        SinglyLinkedList last = (SinglyLinkedList) list.getLast();
        return (String) last.getLast();
    }

    public static String h(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash =
                digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 31; i++) {
            byte b = hash[i];
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        for (; ; ) {
            System.out.print("Enter file name: ");
            String filename = scanner.nextLine();
            try {
                Scanner fileScanner = new Scanner(new FileInputStream(filename));
                SinglyLinkedList text = new SinglyLinkedList();
                while (fileScanner.hasNext()) {
                    text.addAtEndNode(fileScanner.nextLine());
                }
                try {
                    MerkleTree tree = new MerkleTree(text);
                    System.out.printf("The Merkel root of file %s is:\n%s\n", filename, tree.getRoot());
                } catch (NoSuchAlgorithmException e) {
                    System.err.println(e.getCause() + " Exit...");
                    System.exit(1);
                }
            } catch (FileNotFoundException e) {
                System.err.printf("Cannot open %s:%s\n", filename, e.getCause());
            }
        }
    }
}
