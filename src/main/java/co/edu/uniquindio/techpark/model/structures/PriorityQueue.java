package co.edu.uniquindio.techpark.model.structures;

public class PriorityQueue<T> {
    private QueueNode<T> head;
    private int size;

    public PriorityQueue() {
        this.head = null;
        this.size = 0;
    }

    public QueueNode<T> getHead() {
        return head;
    }
    public void setHead(QueueNode<T> head) {
        this.head = head;
    }

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PriorityQueue[");
        QueueNode<T> current = head;
        while (current != null) {
            sb.append("(").append(current.getData())
                    .append(", p=").append(current.getPriority()).append(")");
            if (current.getNext() != null) sb.append(" -> ");
            current = current.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    // ----------------------------------------------------------------
    // main operations
    // ----------------------------------------------------------------

    public void enqueue(T data, int priority) {
        QueueNode<T> newNode = new QueueNode<>(data, priority);

        if (head == null || priority < head.getPriority()) {
            newNode.setNext(head);
            head = newNode;
        } else {
            QueueNode<T> current = head;
            while (current.getNext() != null
                    && current.getNext().getPriority() <= priority) {
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            current.setNext(newNode);
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            System.out.println("The queue is empty.");
            return null;
        }
        T data = head.getData();
        head = head.getNext();
        size--;
        return data;
    }

    public T peek() {
        if (isEmpty()) return null;
        return head.getData();
    }

    // ----------------------------------------------------------------
    // queries
    // ----------------------------------------------------------------

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    public int getPosition(T data) {
        QueueNode<T> current = head;
        int position = 0;
        while (current != null) {
            if (current.getData().equals(data)) return position;
            current = current.getNext();
            position++;
        }
        return -1;
    }

    public boolean contains(T data) {
        return getPosition(data) != -1;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    public boolean remove(T element) {
        if (element == null || head == null) return false;

        // remove head
        if (head.getData().equals(element)) {
            head = head.getNext();
            size--;
            return true;
        }

        // remove mid / tail
        QueueNode<T> prev = head;
        QueueNode<T> curr = head.getNext();
        while (curr != null) {
            if (curr.getData().equals(element)) {
                prev.setNext(curr.getNext());
                size--;
                return true;
            }
            prev = curr;
            curr = curr.getNext();
        }
        return false;
    }
}