package edu.cmu.andrew.okaberintarou;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class MerkleHellman {

    private static final int LIST_LENGTH = 640;

    // (w,q,r) is private key
    private SinglyLinkedList w;

    private BigInteger q;

    private BigInteger r;

    // b is public key
    private SinglyLinkedList b;

    public MerkleHellman() {
        init();
    }

    private void init() {
        Random rnd = new Random();
        w = new SinglyLinkedList();

        BigInteger sum = BigInteger.valueOf(0);
        String data[] = new String[]{"2", "7", "11", "21", "42", "89", "180", "354"};
        for (int i = 0; i < data.length; i++) {
            BigInteger t = new BigInteger(data[i]);
            w.addAtEndNode(t);
            sum = sum.add(t);
        }
        for (int i = data.length; i < LIST_LENGTH; i++) {
            long v = (rnd.nextLong() % 5) + 3;
            sum = sum.add(BigInteger.valueOf(v));
            w.addAtEndNode(sum);
            sum = sum.multiply(BigInteger.valueOf(2));
        }

        do {
            q = sum.add(new BigInteger(sum.bitLength(), new Random()));
        } while (q.equals(sum));

        r = getCoprime(q);

        calcb();

    }

    private BigInteger getCoprime(BigInteger limit) {
        BigInteger rndInt = limit.subtract(BigInteger.valueOf(1));
        while (rndInt.compareTo(BigInteger.valueOf(1)) > 0) {
            if (limit.gcd(rndInt).equals(BigInteger.valueOf(1))) {
                break;
            }
            rndInt = rndInt.subtract(BigInteger.valueOf(1));
        }
        if (rndInt.equals(BigInteger.valueOf(1))) {
            throw new ArithmeticException("Could not find valid coprime of " + limit);
        }
        return rndInt;
    }

    private void calcb() {
        b = new SinglyLinkedList();
        w.reset();
        while (w.hasNext()) {
            BigInteger v = (BigInteger) w.next();
            b.addAtEndNode(v.multiply(r).mod(q));
        }
    }

    public String encrypt(String text) {
        StringBuilder sb = new StringBuilder();
        char array[] = text.toCharArray();
        BigInteger sum = BigInteger.valueOf(0);
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            String val = Integer.toBinaryString(array[i]);

            if (val.length() < 8) {
                sb.delete(0, sb.length());
                for (int k = 0; k < 8 - val.length(); k++) {
                    sb.append('0');
                }
                sb.append(val);
                val = sb.toString();
            }

            for (int k = 0; k < 8; k++) {
                if (val.charAt(k) == '1') {
                    sum = sum.add((BigInteger) b.getObjectAt(j));
                }
                j++;
            }
        }

        return sum.toString();
    }

    public String decrypt(String ciphertext) {
        BigInteger s = r.modInverse(q);
        BigInteger c = new BigInteger(ciphertext);
        BigInteger cp = c.multiply(s).mod(q);

        int index = LIST_LENGTH - 1, highest = -1;
        SinglyLinkedList list = new SinglyLinkedList();
        while (cp.compareTo(BigInteger.valueOf(0)) > 0 && index >= 0) {
            BigInteger bi = (BigInteger) w.getObjectAt(index);
            if (bi.compareTo(cp) <= 0) {
                if (index > highest) {
                    highest = index;
                }
                cp = cp.subtract(bi);
                list.addAtEndNode(index);
            }
            index--;
        }

        highest = (highest + 7) / 8;
        char array[] = new char[highest];
        list.reset();
        while (list.hasNext()) {
            index = (Integer) list.next();
            array[index / 8] |= (1 << (7 - (index % 8)));
        }

        return new String(array);
    }

    public static void main(String[] args) {

        MerkleHellman crypto = new MerkleHellman();
        Scanner scanner = new Scanner(System.in);

        for (; ; ) {
            System.out.println("Enter a string and I will encrypt it as single large integer.");
            String data = scanner.nextLine();
            if (data.length() > 80) {
                System.out.println("The string entered is too long(should be less than 80 characters in length),try again!");
                continue;
            }
            System.out.println("Clear text:");
            System.out.println(data);
            System.out.printf("Number of clear text bytes = %d\n", data.length());
            String ciphertext = crypto.encrypt(data);
            System.out.printf("%s is encrypted as\n%s\n", data, ciphertext);
            System.out.printf("Resulf of decryption: %s\n", crypto.decrypt(ciphertext));
        }
    }
}
