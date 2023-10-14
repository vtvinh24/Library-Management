package group1.util.lists;

import java.util.ArrayList;

public class BinarySearchNode<T extends Comparable<T>>
{
    private T value;
    private BinarySearchNode<T> left;
    private BinarySearchNode<T> right;

    public BinarySearchNode(T value) {
        this.value = value;
        left = right = null;
    }
    
    public BinarySearchNode() {
        left = right = null;
    }
    
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
    
    public ArrayList<T> breadth(){
        ArrayList<T> element = new ArrayList<>();
        
    }
}
