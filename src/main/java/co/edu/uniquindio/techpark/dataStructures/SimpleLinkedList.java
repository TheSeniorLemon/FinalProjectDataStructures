package co.edu.uniquindio.techpark.dataStructures;

public class SimpleLinkedList<T> {
    Node<T> list;
    int size;

    public SimpleLinkedList() {
        list = null;
        size = 0;
    }

    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        node.setNext(list);
        list = node;
        size++;
    }

    public void addLast(T value) {
        if (isEmpty()) { addFirst(value); return; }
        Node<T> node = new Node<>(value);
        Node<T> aux = list;
        while (aux.getNext() != null) aux = aux.getNext();
        aux.setNext(node);
        size++;
    }

    public void addIndex(int index, T value) {
        if (index < 0 || index > size) return;
        if (index == 0) { addFirst(value); return; }
        if (index == size) { addLast(value); return; }
        Node<T> node = new Node<>(value);
        Node<T> aux = list;
        for (int i = 0; i < index - 1; i++) aux = aux.getNext();
        node.setNext(aux.getNext());
        aux.setNext(node);
        size++;
    }

    public void removeFirst() {
        if (isEmpty()) return;
        list = list.getNext();
        size--;
    }

    public void removeLast() {
        if (isEmpty()) return;
        if (size == 1) { list = null; size--; return; }
        Node<T> aux = list;
        while (aux.getNext().getNext() != null) aux = aux.getNext();
        aux.setNext(null);
        size--;
    }

    public void removeIndex(int index) {
        if (index < 0 || index > (size - 1)) return;
        if (index == 0) { removeFirst(); return; }
        if (index == (size - 1)) { removeLast(); return; }
        Node<T> aux = list;
        for (int i = 0; i < index - 1; i++) aux = aux.getNext();
        aux.setNext(aux.getNext().getNext());
        size--;
    }

    public T getFirstValue() {
        if (isEmpty()) return null;
        return list.getValue();
    }

    public T getLastValue() {
        if (isEmpty()) return null;
        Node<T> aux = list;
        while (aux.getNext() != null) aux = aux.getNext();
        return aux.getValue();
    }

    public T getIndexValue(int index) {
        if (index < 0 || index > (size - 1)) return null;
        Node<T> aux = list;
        for (int i = 0; i < index; i++) aux = aux.getNext();
        return aux.getValue();
    }

    public SimpleLinkedList<T> reverse() {
        if (isEmpty()) return null;
        if (size == 1) return this;
        SimpleLinkedList<T> inverted = new SimpleLinkedList<>();
        Node<T> actual = list;
        while (actual != null) {
            inverted.addFirst(actual.getValue());
            actual = actual.getNext();
        }
        return inverted;
    }

    public int indexOf(T value) {
        if (isEmpty()) return -1;
        Node<T> aux = list;
        int index = 0;
        while (aux != null) {
            if (aux.getValue().equals(value)) return index;
            aux = aux.getNext();
            index++;
        }
        return -1;
    }

    public boolean contains(T value) {
        return indexOf(value) != -1;
    }

    public void print() {
        if (isEmpty()) { System.out.println("The list is empty"); return; }
        System.out.print("First => <<");
        Node<T> aux = list;
        while (aux != null) {
            System.out.print("[ " + aux.getValue() + " ]");
            if (aux.getNext() != null) System.out.print("->");
            aux = aux.getNext();
        }
        System.out.println(">> <= Last");
    }

    public boolean isEmpty() { return (list == null && size == 0); }
    public int size() { return size; }
    public Node<T> list() { return list; }
}