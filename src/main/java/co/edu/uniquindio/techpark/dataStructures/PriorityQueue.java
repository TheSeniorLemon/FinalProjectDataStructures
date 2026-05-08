package co.edu.uniquindio.techpark.dataStructures;

public class PriorityQueue<T> {
    private NodePQ<T> head;
    private int size;

    public PriorityQueue() {
        head = null;
        size = 0;
    }

    private static class NodePQ<T> {
        T value;
        int priority;
        NodePQ<T> next;

        NodePQ(T value, int priority) {
            this.value = value;
            this.priority = priority;
            this.next = null;
        }
    }


    public void enqueue(T value, int priority) {
        NodePQ<T> newNode = new NodePQ<>(value, priority);

        // Si la cola está vacía o el nuevo tiene mayor prioridad que el primero
        if (head == null || priority < head.priority) {
            newNode.next = head;
            head = newNode;
            size++;
            return;
        }

        // Busca la posición correcta
        NodePQ<T> aux = head;
        while (aux.next != null && aux.next.priority <= priority) {
            aux = aux.next;
        }
        newNode.next = aux.next;
        aux.next = newNode;
        size++;
    }

    public T dequeue() {
        if (isEmpty()) return null;
        T value = head.value;
        head = head.next;
        size--;
        return value;
    }

    public T peek() {
        if (isEmpty()) return null;
        return head.value;
    }

    public boolean isEmpty() {
        return head == null && size == 0;
    }

    public int size() {
        return size;
    }

    public void print() {
        if (isEmpty()) { System.out.println("The queue is empty"); return; }
        System.out.print("First => ");
        NodePQ<T> aux = head;
        while (aux != null) {
            System.out.print("[ " + aux.value + "(p:" + aux.priority + ") ]");
            if (aux.next != null) System.out.print(" -> ");
            aux = aux.next;
        }
        System.out.println(" <= Last");
    }
}