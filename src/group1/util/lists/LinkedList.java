package group1.util.lists;

import sun.awt.image.ImageWatched;

public class LinkedList<T extends Comparable>
{
    private LinkedNode<T> head;
    private LinkedNode<T> tail;

    // Add Object
    public void add(T t) {
        LinkedNode node = new LinkedNode(t, null);
        if(isEmpty()) {
            head = node;
            tail = head;
        } else {
            node.setPrev(tail);
            tail.setNext(node);
            tail = node;
        }
    }


    // Delete Object
//    public void delete(T t) {
//        if(isEmpty()) return;
//        LinkedNode node = head;
//        while(node.getValue() != t) {
//            node = node.getNext();
//            if(node == null) return;
//            // Node not found
//        }
//
//        LinkedNode prev = node.getPrev();
//        LinkedNode next = node.getNext();
//        if(prev != null) prev.setNext(next);
//        if(next != null) next.setPrev(prev);
//        node = null;
//
//    }

    // Delete by index
    public void delete(int k) {
        if(!(k < length())) {
            // Invalid index
            return;
        }
        int current = 0;
        LinkedNode node = head;
        while(current != k) {
            node = node.getNext();
            current++;
        }
        LinkedNode prev = node.getPrev();
        LinkedNode next = node.getNext();
        if(prev != null) prev.setNext(next);
        if(next != null) next.setPrev(prev);
    }

    // Alternative implementation
//    public void delete(int k) {
//        delete(get(k).getValue());
//    }

    // Search Object
    public LinkedNode<T> search(T t) {
        if(isEmpty()) return null;
        LinkedNode node = head;
        while(node != null) {
            if(node.getValue().compareTo(t) == 0) {
                return node;
            }
            node = node.getNext();
        }
        return null;
    }

    public void addAfter(int k, T t) {
        LinkedNode kNode = get(k);
        LinkedNode newNode = new LinkedNode(t, null);
        if(!(k < length())) {
            // Invalid index
            return;
        }
        if(kNode.getNext() != null) newNode.setNext(kNode.getNext());
        kNode.setNext(newNode);
    }


    // Display Objects
    public void display() {
        if(isEmpty()) System.out.println("List is empty");
        LinkedNode node = head;
        while(node != null) {
            System.out.println(node.getValue().toString());
            node = node.getNext();
        }

    }


    // Check if empty
    public boolean isEmpty() {
        return head == null;
    }

    public LinkedNode get(int i) {
        if(i > length()) return null;
        LinkedNode node = head;
        while(i != 0) {
            node = node.getNext();
            i--;
        }
        return node;
    }

    // Get size of list
    public int length() {
        if(isEmpty()) return 0;
        LinkedNode node = head;
        int length = 0;
        while (node != null) {
            length++;
            node = node.getNext();
        }
        return length;
    }
}
