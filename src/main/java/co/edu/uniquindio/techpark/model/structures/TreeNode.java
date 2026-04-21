package co.edu.uniquindio.techpark.model.structures;

public class TreeNode<T extends Comparable<T>> {
    private T data;
    private TreeNode<T> left;
    private TreeNode<T> right;

    public TreeNode(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    public TreeNode<T> getLeft() {
        return left;
    }
    public void setLeft(TreeNode<T> left) {
        this.left = left;
    }

    public TreeNode<T> getRight() {
        return right;
    }
    public void setRight(TreeNode<T> right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "data=" + data +
                ", left=" + (left != null ? left.getData() : "null") +
                ", right=" + (right != null ? right.getData() : "null") +
                "}";
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public int countChildren() {
        int count = 0;
        if (left != null) count++;
        if (right != null) count++;
        return count;
    }
}