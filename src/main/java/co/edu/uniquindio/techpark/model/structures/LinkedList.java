package co.edu.uniquindio.techpark.model.structures;

public class LinkedList<T> {
    private ListNode<T> head;
    private ListNode<T> tail;
    private int size;

    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public ListNode<T> getHead() {
        return head;
    }
    public void setHead(ListNode<T> head) {
        this.head = head;
    }

    public ListNode<T> getTail() {
        return tail;
    }
    public void setTail(ListNode<T> tail) {
        this.tail = tail;
    }

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "LinkedList[]";
        StringBuilder sb = new StringBuilder("LinkedList[");
        ListNode<T> current = head;
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) sb.append(" -> ");
            current = current.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    // ----------------------------------------------------------------
    // insertion
    // ----------------------------------------------------------------

    public void add(T data) {
        ListNode<T> newNode = new ListNode<>(data);
        if (head == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
        size++;
    }

    public void addFirst(T data) {
        ListNode<T> newNode = new ListNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
        }
        size++;
    }

    public void addAtIndex(T data, int index) {
        if (index < 0 || index > size) {
            System.out.println("Index out of range.");
            return;
        }
        if (index == 0) {
            addFirst(data);
            return;
        }
        if (index == size) {
            add(data);
            return;
        }
        ListNode<T> newNode = new ListNode<>(data);
        ListNode<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.getNext();
        }
        newNode.setNext(current.getNext());
        current.setNext(newNode);
        size++;
    }

    // ----------------------------------------------------------------
    // deletion
    // ----------------------------------------------------------------

    public boolean remove(T data) {
        if (head == null) return false;
        if (equalsData(head.getData(), data)) {
            head = head.getNext();
            if (head == null) tail = null;
            size--;
            return true;
        }
        ListNode<T> current = head;
        while (current.getNext() != null) {
            if (equalsData(current.getNext().getData(), data)) {
                if (current.getNext().equals(tail)) {
                    tail = current;
                }
                current.setNext(current.getNext().getNext());
                size--;
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public T removeFirst() {
        if (head == null) return null;
        T data = head.getData();
        head = head.getNext();
        if (head == null) tail = null;
        size--;
        return data;
    }

    public T removeLast() {
        if (head == null) return null;
        if (head == tail) {
            T data = head.getData();
            head = null;
            tail = null;
            size--;
            return data;
        }
        ListNode<T> current = head;
        while (current.getNext() != tail) {
            current = current.getNext();
        }
        T data = tail.getData();
        current.setNext(null);
        tail = current;
        size--;
        return data;
    }

    public T removeAtIndex(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Index out of range.");
            return null;
        }
        if (index == 0) return removeFirst();
        if (index == size - 1) return removeLast();
        ListNode<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.getNext();
        }
        T data = current.getNext().getData();
        current.setNext(current.getNext().getNext());
        size--;
        return data;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    // ----------------------------------------------------------------
    // access and search
    // ----------------------------------------------------------------

    public T get(int index) {
        if (index < 0 || index >= size) return null;
        ListNode<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getData();
    }

    public T getFirst() {
        if (head == null) return null;
        return head.getData();
    }

    public T getLast() {
        if (tail == null) return null;
        return tail.getData();
    }

    public boolean contains(T data) {
        ListNode<T> current = head;
        while (current != null) {
            if (equalsData(current.getData(), data)) return true;
            current = current.getNext();
        }
        return false;
    }

    public int indexOf(T data) {
        ListNode<T> current = head;
        int index = 0;
        while (current != null) {
            if (equalsData(current.getData(), data)) return index;
            current = current.getNext();
            index++;
        }
        return -1;
    }

    // ----------------------------------------------------------------
    // state
    // ----------------------------------------------------------------

    public boolean isEmpty() {
        return head == null;
    }

    // ----------------------------------------------------------------
    // utilities
    // ----------------------------------------------------------------

    private boolean equalsData(T a, T b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    public void reverse() {
        if (size <= 1) return;
        ListNode<T> previous = null;
        ListNode<T> current = head;
        tail = head;
        while (current != null) {
            ListNode<T> next = current.getNext();
            current.setNext(previous);
            previous = current;
            current = next;
        }
        head = previous;
    }

    public LinkedList<T> copy() {
        LinkedList<T> copy = new LinkedList<>();
        ListNode<T> current = head;
        while (current != null) {
            copy.add(current.getData());
            current = current.getNext();
        }
        return copy;
    }
}