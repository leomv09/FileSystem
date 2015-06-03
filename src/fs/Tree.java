package fs;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Tree data structure.
 * 
 * @author José Andrés García Sáenz <jags9415@gmail.com>
 * @param <T> Type of data to store.
 */
public class Tree<T> {

    /**
     * Data stored in this node of the tree.
     */
    private T data;
    
    /**
     * The parent node.
     */
    private Tree<T> parent;
    
    /**
     * The children nodes.
     */
    private List<Tree<T>> children;
    
    /**
     * Flag indicating if this tree allows children nodes.
     */
    private final boolean allowsChildren;
    
    /**
     * Create a new Tree.
     * 
     * @param parent The parent node.
     * @param data The data to store.
     * @param allowsChildren If the tree allows children nodes.
     */
    private Tree(Tree parent, T data, boolean allowsChildren) {
        this.data = data;
        this.parent = parent;
        this.allowsChildren = allowsChildren;
        this.children = new ArrayList<>();
    }
    
    /**
     * Create a new Tree.
     * 
     * @param data The data to store. 
     */
    public Tree(T data) {
        this(null, data, true);
    }
    
    /**
     * Add a child node.
     * 
     * @param data The data to store in the new node.
     * @param allowsChildren If the new node allows children.
     * @throws Exception If this tree do not allows children.
     */
    public void addChild(T data, boolean allowsChildren) throws Exception {
        if (this.allowsChildren) {
            Tree<T> child = new Tree<>(this, data, allowsChildren);
            children.add(child);
        }
        else {
            throw new Exception("This tree do not allow children");
        }
    }
    
    /**
     * Add a child node.
     * 
     * @param data The data to store in the new node.
     * @throws Exception If this tree do not allows children.
     */
    public void addChild(T data) throws Exception {
        addChild(data, true);
    }
 
    /**
     * Remove a child.
     * 
     * @param data The data stored in the child.
     * @return true if the child is removed.
     */
    public boolean removeChild(T data) {
        for (Tree<T> child : children) {
            if (child.getData().equals(data)) {
                return children.remove(child);
            }
        }
        return false;
    }
    
    /**
     * Remove all children.
     */
    public void clear() {
        children.clear();
    }
    
    /**
     * Get the data stored in this node.
     * 
     * @return The data. 
     */
    public T getData() {
        return data;
    }
 
    /**
     * Set the data stored in this node.
     * 
     * @param data The new data. 
     */
    public void setData(T data) {
        this.data = data;
    }
    
    /**
     * Get the amount of children nodes.
     * 
     * @return The amount of children nodes.
     */
    public int getSize() {
        return children.size();
    }

    /**
     * Get the parent node.
     * 
     * @return The parent node. 
     */
    public Tree<T> getParent() {
        return parent;
    }
    
    /**
     * Get the children nodes.
     * 
     * @return The children nodes. 
     */
    public List<Tree<T>> getChildren() {
        return children;
    }

    /**
     * Get the flag that indicate if this node allows children.
     * 
     * @return The flag that indicate if this node allows children.
     */
    public boolean allowsChildren() {
        return allowsChildren;
    }
    
    /**
     * Check if this node is a leaf node.
     * A leaf node has no children.
     * 
     * @return If this node is a leaf node.
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

}
