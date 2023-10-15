package group1.util.lists;

import java.util.ArrayList;
import java.util.function.Function;

public class BinarySearchNode<T extends Comparable> implements ListAddable<T>
{
    private T value;
    private BinarySearchNode<T> left;
    private BinarySearchNode<T> right;
    
    public BinarySearchNode(T value) {
        this.value = value;
        left = right = null;
    }
    
    public BinarySearchNode() {
        value = null;
        left = right = null;
    }
    
    //1.2 insert data
    public void insert(T data){
        if(value == null){
            value = data;
        }
        if(data.compareTo(value) < 0){
            if(left == null){
                left = new BinarySearchNode<>(data);
            }
            else{
                left.insert(data);
            }
        }
        else if(data.compareTo(value) > 0){
            if(right == null){
                right = new BinarySearchNode<>(data);
            }
            else{
                right.insert(data);
            }
        }
    }
    
    //1.3 inorder tarverse
    public ArrayList<T> inorder(){
        ArrayList<T> element = new ArrayList<>();
        if(left != null){
            element.addAll(left.inorder());
        }
        element.add(value);
        if(right != null){
            element.addAll(right.inorder());
        }
        return element;
    }
    
    //calulate height (not necessary)
    private int height(BinarySearchNode n){
        if(n == null){
            return 0;
        }
        int lheight = height(n.left);
        int rheight = height(n.right);
        
        return Math.max(rheight, rheight) + 1;
    }
    
    public int height(){
        return height(this);
    }
    
    //1.4 breadth first traverse
    public ArrayList<T> levelOrderTraversal() {
        ArrayList<T> result = new ArrayList<>();
        ArrayList<BinarySearchNode> currentLevel = new ArrayList<>();
        ArrayList<BinarySearchNode> nextLevel = new ArrayList<>();

        currentLevel.add(this);

        while (!currentLevel.isEmpty()) {
            for (BinarySearchNode node : currentLevel) {
                result.add((T)node.value);
                if (node.left != null) {
                    nextLevel.add(node.left);
                }
                if (node.right != null) {
                    nextLevel.add(node.right);
                }
            }

            currentLevel = new ArrayList<>(nextLevel);
            nextLevel.clear();
        }

        return result;
    }
    // count number of node
    private int count(BinarySearchNode n){
        if(n == null){
            return 0;
        }
        int leftcount = count(n.left);
        int rightcount = count(n.right);
        
        return leftcount + rightcount + 1;
    }
    
    public int count(){
        return count(this);
    }
    
    // search
    public BinarySearchNode<T> search(T data){
        BinarySearchNode<T> current = this;
        while(current.value != data && current != null){
            if(data.compareTo(current.value) < 0){
                current = current.left;
            }
            else{
                current = current.right;
            }
        }
        return current;
    }
    
    //1.8 simply balance
    public BinarySearchNode<T> balance(ArrayList<T> sortedlist, int start, int end){
        if(start > end){
            return null;
        }
        int mid = (start + end)/2;
        BinarySearchNode<T> n = new BinarySearchNode<>(sortedlist.get(mid));
        
        n.left = balance(sortedlist, start, mid - 1);
        n.right = balance(sortedlist, mid + 1, end);
        
        return n;
    }
    
    //delete
    public void Delete(T data){
        BinarySearchNode<T> parent = null;
        BinarySearchNode<T> current = this;

        while (current != null && current.value != data) {
            parent = current;

            if (data.compareTo(current.value) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        
        if (current == null){
            return;
        }
        
        if(current.right == null && current.left == null){
            if(parent != null){
                if(parent.left == current){
                    parent.left = null;
                }
                else{
                    parent.right = null;
                }
            }
            else{
                current = null;
            }
        }
        else if(current.right == null || current.left == null){
            if(current.left != null){
                current.value = current.left.value;
                current.left = null;
            }
            else{
                current.value = current.right.value;
                current.right = null;
            }
        }
        else{
            BinarySearchNode<T> successor = findInOrderSuccessor(current.right);
            current.value = successor.value;
            current.right = null;
        }
    }
    
    private BinarySearchNode<T> findInOrderSuccessor(BinarySearchNode<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    
    public <U extends Comparable> void add(T value, Function<T, U> selector)
    {
        throw new UnsupportedOperationException();
    }
    @Override
    public void add(T value)
    {
        add(value, (v) -> v);
    }
    
    public <U extends Comparable> BinarySearchNode<T> find(T value, Function<T, U> selector)
    {
        throw new UnsupportedOperationException();
    }
    public BinarySearchNode<T> find(T value) 
    {
        return find(value, (v) -> v);
    }
    
    public <U extends Comparable> void delete(T value, Function<T, U> selector)
    {
        throw new UnsupportedOperationException();
    }
    public void delete(T value) 
    {
        find(value, (v) -> v);
    }
}
