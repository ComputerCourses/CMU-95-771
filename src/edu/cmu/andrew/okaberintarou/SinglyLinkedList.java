package edu.cmu.andrew.okaberintarou;

import edu.colorado.nodes.ObjectNode;

import java.math.BigInteger;

public class SinglyLinkedList {

    private ObjectNode head;

    private ObjectNode tail;

    private int countNodes;

    private ObjectNode iter;

    public SinglyLinkedList() {
        head = tail = null;
        iter = null;
        countNodes = 0;
    }

    public void reset() {
        iter = head;
    }

    public Object next() {
        Object r = iter.getData();
        iter = iter.getLink();
        return r;
    }

    public boolean hasNext() {
        return iter != null;
    }

    public void addAtEndNode(Object c) {
        if (tail == null) {
            head = tail = new ObjectNode(c, null);
        } else {
            tail.addNodeAfter(c);
            tail = tail.getLink();
        }
        ++countNodes;
    }

    public int countNodes() {
        return countNodes;
    }

    public Object getObjectAt(int i) {
        return ObjectNode.listPosition(head, i).getData();
    }

    public Object getLast() {
        return tail.getData();
    }

    public String toString() {
        if (head == null) {
            return "";
        }
        return head.toString();
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();
        for (long i = 0; i < 26; i++) {
            list.addAtEndNode(new BigInteger("" + i));
        }
        list.reset();
        while (list.hasNext()) {
            System.out.println(list.next());
        }
    }
}
