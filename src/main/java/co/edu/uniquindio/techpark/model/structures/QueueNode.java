package co.edu.uniquindio.techpark.model.structures;

public class QueueNode<T> {
    private T data;
    private int priority;
    private QueueNode<T> next;

    public QueueNode(T data, int priority) {
        this.data = data;
        this.priority = priority;
        this.next = null;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public QueueNode<T> getNext() {
        return next;
    }
    public void setNext(QueueNode<T> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "QueueNode{" +
                "data=" + data +
                ", priority=" + priority +
                ", next=" + next +
                '}';
    }
}