package group1.util.lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LinkedList<T extends Comparable> implements ListAddable<T>
{
    private LinkedNode<T> head;
    private LinkedNode<T> tail;

    // Add Object
    public void add(T t) 
    {
        LinkedNode<T> node = new LinkedNode<>(t, null);
        
        if (isEmpty()) 
        {
            head = node;
            tail = head;
        } 
        else 
        {
            node.setPrev(tail);
            tail.setNext(node);
            tail = node;
        }
    }
    
    public void addToHead(T t)
    {
        if (isEmpty()) add(t);
        else
        {
            LinkedNode<T> node = new LinkedNode<>(t, null);
            node.setNext(head);
            head.setPrev(node);
            head = node;
        }
    }

    public void addAfter(int k, T t) 
    {
        if (!(k < count())) 
        {
            // Invalid index
            throw new IndexOutOfBoundsException(String.format("Index %d was beyond the range of the list [0, %d].", k, count() - 1));
        }

        LinkedNode kNode = get(k);
        LinkedNode newNode = new LinkedNode(t, null);

        if (kNode.getNext() != null) newNode.setNext(kNode.getNext());
        kNode.setNext(newNode);
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

    public void delete(Function<T, Boolean> comparator)
    {
        if (isEmpty()) return;
        
        LinkedNode<T> node = head;
        while (node != null)
        {
            if (comparator.apply(node.getValue()))
            {
                if (node == head)
                {
                    head = null;
                    return;
                }
                else
                {
                    LinkedNode prev = node.getPrev();
                    LinkedNode next = node.getNext();
                    if (prev != null) prev.setNext(next);
                    if (next != null) next.setPrev(prev);
                }
            }
            node = node.getNext();
        }
    }
    public void deleteObject(T t)
    {
        delete((v) -> v.equals(t));
    }
    public void delete(T t)
    {
        delete((v) -> v.compareTo(t) == 0);
    }
    
    // Delete by index
    public void delete(int k) 
    {
        if (!(k < count()))
        {
            // Invalid index
            throw new IndexOutOfBoundsException(String.format("Index %d was beyond the range of the list [0, %d].", k, count() - 1));
        }
        
        int current = 0;
        LinkedNode node = head;
        while (current != k) 
        {
            node = node.getNext();
            current++;
        }
        
        if (node == head)
        {
            head = null;
        }
        else
        {
            LinkedNode prev = node.getPrev();
            LinkedNode next = node.getNext();
            if (prev != null) prev.setNext(next);
            if (next != null) next.setPrev(prev);
        }
    }

    // Alternative implementation
//    public void delete(int k) {
//        delete(get(k).getValue());
//    }

    // Search Object
    public List<LinkedNode<T>> find(Function<T, Boolean> evaluator) 
    {
        ArrayList<LinkedNode<T>> found = new ArrayList<>();
        
        if (isEmpty()) 
            return found;
        
        LinkedNode<T> node = head;
        while (node != null) 
        {
            if (evaluator.apply(node.getValue())) 
            {
                found.add(node);
            }
            node = node.getNext();
        }
        return found;
    }
    public List<T> findValues(Function<T, Boolean> evaluator) 
    {
        return find(evaluator).stream().map(x -> x.getValue()).collect(Collectors.toList());
    }
    public List<LinkedNode<T>> find(T t)
    {
        return find((v) -> v.compareTo(t) == 0);
    }
    
    public int findIndexOf(Function<T, Boolean> evaluator) 
    {
        if (isEmpty()) return -1;
        
        LinkedNode<T> node = head;
        int index = 0;
        while (node != null) 
        {
            if (evaluator.apply(node.getValue())) 
            {
                break;
            }
            node = node.getNext();
            index++;
        }
        
        return index;
    }
    public int findIndexOf(T t)
    {
        return findIndexOf((v) -> v.compareTo(t) == 0);
    }

    // Display Objects
    public void display() 
    {
        if(isEmpty()) System.out.println("List is empty");
        LinkedNode node = head;
        while(node != null) 
        {
            System.out.println(node.getValue().toString());
            node = node.getNext();
        }

    }

    // Traverse
    public ArrayList<T> traverse() 
    {
        ArrayList list = new ArrayList();
        LinkedNode node = head;
        while(node != null) 
        {
            list.add(node.getValue());
            node = node.getNext();
        }
        return list;
    }
    
    public void clear()
    {
        head = null;
    }

    // Check if empty
    public boolean isEmpty() 
    {
        return head == null;
    }

    public LinkedNode<T> get(int i) 
    {
        if(i > count()) return null;
        LinkedNode node = head;
        while(i != 0) 
        {
            node = node.getNext();
            i--;
        }
        return node;
    }

    // Get size of list
    public int count()
    {
        if(isEmpty()) return 0;
        LinkedNode node = head;
        int length = 0;
        while (node != null) 
        {
            length++;
            node = node.getNext();
        }
        return length;
    }
    
    public void sort(Comparator<T> comparator)
    {
        if (isEmpty() || count() == 1)
        {
            // Already sorted or nothing to sort
            return;
        }

        boolean swapped;
        LinkedNode<T> lastNode = null;

        do
        {
            swapped = false;
            LinkedNode<T> current = head;
            LinkedNode<T> next;

            while (current.getNext() != lastNode)
            {
                next = current.getNext();

                if (comparator.compare(current.getValue(), next.getValue()) > 0)
                {
                    // Swap the nodes
                    T temp = current.getValue();
                    current.setValue(next.getValue());
                    next.setValue(temp);
                    swapped = true;
                }

                current = next;
            }
            lastNode = current;
        } 
        while (swapped);
    }
    public void sort()
    {
        sort((a, b) -> a.compareTo(b));
    }
}
