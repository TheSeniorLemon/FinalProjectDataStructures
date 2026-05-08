package co.edu.uniquindio.techpark.model.structures;

public class BSTree<T extends Comparable<T>> {
    private TreeNode<T> root;

    public BSTree() {
        this.root = null;
    }

    public TreeNode<T> getRoot() {
        return root;
    }
    public void setRoot(TreeNode<T> root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return "BSTree{size=" + getSize() +
                ", height=" + getHeight() +
                ", min=" + min() +
                ", max=" + max() + "}";
    }

    // ----------------------------------------------------------------
    // insertion
    // ----------------------------------------------------------------

    public void insert(T data) {
        if (data == null) return;
        root = insertRec(root, data);
    }

    private TreeNode<T> insertRec(TreeNode<T> node, T data) {
        if (node == null) return new TreeNode<>(data);
        int cmp = data.compareTo(node.getData());
        if (cmp < 0) {
            node.setLeft(insertRec(node.getLeft(), data));
        } else if (cmp > 0) {
            node.setRight(insertRec(node.getRight(), data));
        } else {
            System.out.println("The element already exists in the tree.");
        }
        return node;
    }

    // ----------------------------------------------------------------
    // search
    // ----------------------------------------------------------------

    public TreeNode<T> search(T data) {
        if (data == null) return null;
        return searchRec(root, data);
    }

    private TreeNode<T> searchRec(TreeNode<T> node, T data) {
        if (node == null) return null;
        int cmp = data.compareTo(node.getData());
        if (cmp == 0) return node;
        if (cmp < 0) return searchRec(node.getLeft(), data);
        return searchRec(node.getRight(), data);
    }

    public boolean contains(T data) {
        return search(data) != null;
    }

    // ----------------------------------------------------------------
    // deletion
    // ----------------------------------------------------------------

    public void delete(T data) {
        if (data == null) return;
        root = deleteRec(root, data);
    }

    private TreeNode<T> deleteRec(TreeNode<T> node, T data) {
        if (node == null) {
            System.out.println("Element not found for deletion.");
            return null;
        }
        int cmp = data.compareTo(node.getData());
        if (cmp < 0) {
            node.setLeft(deleteRec(node.getLeft(), data));
        } else if (cmp > 0) {
            node.setRight(deleteRec(node.getRight(), data));
        } else {
            if (node.isLeaf()) return null;
            if (node.getLeft() == null) return node.getRight();
            if (node.getRight() == null) return node.getLeft();
            T successor = minRec(node.getRight());
            node.setData(successor);
            node.setRight(deleteRec(node.getRight(), successor));
        }
        return node;
    }

    // ----------------------------------------------------------------
    // traversals
    // ----------------------------------------------------------------

    public LinkedList<T> inorder() {
        LinkedList<T> list = new LinkedList<>();
        inorderRec(root, list);
        return list;
    }

    private void inorderRec(TreeNode<T> node, LinkedList<T> list) {
        if (node == null) return;
        inorderRec(node.getLeft(), list);
        list.add(node.getData());
        inorderRec(node.getRight(), list);
    }

    public LinkedList<T> preorder() {
        LinkedList<T> list = new LinkedList<>();
        preorderRec(root, list);
        return list;
    }

    private void preorderRec(TreeNode<T> node, LinkedList<T> list) {
        if (node == null) return;
        list.add(node.getData());
        preorderRec(node.getLeft(), list);
        preorderRec(node.getRight(), list);
    }

    public LinkedList<T> postorder() {
        LinkedList<T> list = new LinkedList<>();
        postorderRec(root, list);
        return list;
    }

    private void postorderRec(TreeNode<T> node, LinkedList<T> list) {
        if (node == null) return;
        postorderRec(node.getLeft(), list);
        postorderRec(node.getRight(), list);
        list.add(node.getData());
    }

    // ----------------------------------------------------------------
    // minimum and maximum
    // ----------------------------------------------------------------

    public T min() {
        if (isEmpty()) return null;
        return minRec(root);
    }

    private T minRec(TreeNode<T> node) {
        if (node.getLeft() == null) return node.getData();
        return minRec(node.getLeft());
    }

    public T max() {
        if (isEmpty()) return null;
        return maxRec(root);
    }

    private T maxRec(TreeNode<T> node) {
        if (node.getRight() == null) return node.getData();
        return maxRec(node.getRight());
    }

    // ----------------------------------------------------------------
    // height and utilities
    // ----------------------------------------------------------------

    public int getHeight() {
        return heightRec(root);
    }

    private int heightRec(TreeNode<T> node) {
        if (node == null) return 0;
        int leftHeight = heightRec(node.getLeft());
        int rightHeight = heightRec(node.getRight());
        return 1 + Math.max(leftHeight, rightHeight);
    }

    public int getSize() {
        return sizeRec(root);
    }

    private int sizeRec(TreeNode<T> node) {
        if (node == null) return 0;
        return 1 + sizeRec(node.getLeft()) + sizeRec(node.getRight());
    }

    public boolean isEmpty() {
        return root == null;
    }
}