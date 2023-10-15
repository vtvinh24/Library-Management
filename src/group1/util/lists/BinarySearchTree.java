package group1.util.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BinarySearchTree<T extends Comparable> implements ListAddable<T>
{
    private BinarySearchNode<T> root;
    
    public void clear()
    {
        root = null;
    }
    
    //1.2 insert data
    
    private void add(BinarySearchNode<T> node, T data)
    {
        if (data.compareTo(node.getValue()) < 0)
        {
            if (node.getLeft() == null)
            {
                node.setLeft(new BinarySearchNode<>(data));
            } 
            else
            {
                add(node.getLeft(), data);
            }
        } 
        else
        {
            if (node.getRight() == null)
            {
                node.setRight(new BinarySearchNode<>(data));
            } 
            else
            {
                add(node.getRight(), data);
            }
        }
    }
    @Override
    public void add(T data)
    {
        if (root == null)
        {
            root = new BinarySearchNode<>(data);
        }
        else 
        {
            add(root, data);
        }
    }

    //1.3 inorder tarverse
    private ArrayList<T> inorder(BinarySearchNode<T> node)
    {
        ArrayList<T> element = new ArrayList<>();
        if (node.getLeft() != null)
        {
            element.addAll(inorder(node.getLeft()));
        }
        element.add(node.getValue());
        if (node.getRight() != null)
        {
            element.addAll(inorder(node.getRight()));
        }
        return element;
    }
    public ArrayList<T> inorder()
    {
        if (root == null)
        {
            return new ArrayList<>();
        }
        else
        {
            return inorder(root);
        }
    }

    //calulate height (not necessary)
    private int height(BinarySearchNode<T> node)
    {
        if (node == null)
        {
            return 0;
        }
        int lheight = height(node.getLeft());
        int rheight = height(node.getRight());

        return Math.max(lheight, rheight) + 1;
    }
    public int height()
    {
        if (root == null)
        {
            return 0;
        }
        else
        {
            return height(root);
        }
    }

    //1.4 breadth first traverse
    private ArrayList<T> levelOrder(BinarySearchNode<T> node)
    {
        ArrayList<T> result = new ArrayList<>();
        ArrayList<BinarySearchNode<T>> currentLevel = new ArrayList<>();
        ArrayList<BinarySearchNode<T>> nextLevel = new ArrayList<>();

        currentLevel.add(node);

        while (!currentLevel.isEmpty())
        {
            for (BinarySearchNode<T> n : currentLevel)
            {
                result.add((T) n.getValue());
                if (n.getValue() != null)
                {
                    nextLevel.add(n.getLeft());
                }
                if (n.getRight() != null)
                {
                    nextLevel.add(n.getRight());
                }
            }

            currentLevel = new ArrayList<>(nextLevel);
            nextLevel.clear();
        }

        return result;
    }
    public ArrayList<T> levelOrder()
    {
        if (root != null)
        {
            return new ArrayList<>();
        }
        else 
        {
            return levelOrder(root);
        }
    }

    // count number of node
    private int count(BinarySearchNode node)
    {
        if (node == null)
        {
            return 0;
        }
        int leftcount = count(node.getLeft());
        int rightcount = count(node.getRight());

        return leftcount + rightcount + 1;
    }
    public int count()
    {
        if (root == null)
        {
            return 0;
        }
        else 
        {
            return count(root);
        }
    }
    
    public BinarySearchNode<T> find(Function<T, Integer> comparator)
    {
        BinarySearchNode<T> current = root;
        
        while (current != null && comparator.apply(current.getValue()) != 0)
        {
            if (comparator.apply(current.getValue()) < 0)
            {
                current = current.getLeft();
            }
            else
            {
                current = current.getRight();
            }
        }
        return current;
    }
    public BinarySearchNode<T> find(T data)
    {
        return find((v) -> data.compareTo(v));
    }

    //1.8 simply balance
    private BinarySearchNode<T> balance(ArrayList<T> sortedlist, int start, int end)
    {
        if (start > end)
        {
            return null;
        }
        int mid = (start + end) / 2;
        BinarySearchNode<T> n = new BinarySearchNode<>(sortedlist.get(mid));

        n.setLeft(balance(sortedlist, start, mid - 1));
        n.setRight(balance(sortedlist, mid + 1, end));

        return n;
    }
    public void balance()
    {
        this.root = balance(inorder(), 0, count() - 1);
    }

    private BinarySearchNode<T> findInOrderSuccessor(BinarySearchNode<T> node)
    {
        while (node.getLeft() != null)
        {
            node = node.getLeft();
        }
        return node;
    }      
    //delete
    public <U extends Comparable> void delete(Function<T, Integer> comparator)
    {
        BinarySearchNode<T> parent = null;
        BinarySearchNode<T> current = root;

        while (current != null && comparator.apply(current.getValue()) != 0)
        {
            parent = current;

            if (comparator.apply(current.getValue()) < 0)
            {
                current = current.getLeft();
            } 
            else
            {
                current = current.getRight();
            }
        }

        if (current == null)
        {
            return;
        }

        if (current.getRight() == null && current.getLeft() == null)
        {
            if (parent != null)
            {
                if (parent.getLeft() == current)
                {
                    parent.setLeft(null);
                } 
                else
                {
                    parent.setRight(null);
                }
            } 
            else
            {
                current = null;
            }
        } 
        else if (current.getRight() == null || current.getLeft() == null)
        {
            if (current.getLeft() != null)
            {
                current.setValue(current.getLeft().getValue());
                current.setLeft(null);
            } 
            else
            {
                current.setValue(current.getRight().getValue());
                current.setRight(null);
            }
        } 
        else
        {
            BinarySearchNode<T> successor = findInOrderSuccessor(current.getRight());
            if (successor == null)
            {
                root = null;
            }
            else
            {
                current.setValue(successor.getValue());
                current.setRight(null); 
            }
        }
    }  
    public void delete(T data)
    {
        delete((v) -> data.compareTo(v));
    }
}
