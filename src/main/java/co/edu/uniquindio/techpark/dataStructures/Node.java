package co.edu.uniquindio.techpark.dataStructures;


public class Node<T>{
    T value;
    Node<T> next;

    public Node(T value){
        this.value=value;
        next =null;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}