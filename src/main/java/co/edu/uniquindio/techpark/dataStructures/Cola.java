package co.edu.uniquindio.techpark.dataStructures;

public class Cola <T>{

    Node<T> queue;
    int size;

    public Cola(){
        queue= null;
        size=0;
    }

    public void addLast(T value){
        Node<T> node = new Node<>(value);
        if(isEmpty()){ queue = node; size++; return; }
        Node<T> aux = queue;
        while(aux.getNext() != null){
            aux = aux.getNext();
        }
        aux.setNext(node);
        size++;
    }

    public void removeFirst(){
        if(isEmpty()) return;

        if(size==1){queue=null; size--; return;}

        queue=queue.getNext();
        size--;
    }

    public T peek(){
        if(isEmpty()) return null;
        return queue.getValue();
    }

    public boolean contains(T value){
        if(isEmpty()){return false;}

        Node<T> aux=queue;
        while(aux.getNext()!=null){ if(aux.getValue().equals(value)) return true; aux=aux.getNext();}
        return false;
    }

    public Cola<T> reverse(){
        if(isEmpty())return null;
        if(size==1) { Cola<T> aux = new Cola<T>(); aux.addLast(queue.getValue()); return aux;};

        Cola<T> inverted=new Cola<T>();
        Node<T> actual = queue;

        while(actual != null){
            inverted.addLast(actual.getValue());
            actual=actual.getNext();
        }
    return inverted;
    }

    public void print(){
        if(isEmpty()){ System.out.println("The queue is empty"); return; }

        System.out.print("First => ");
        Node<T> aux = queue;
        while(aux != null){
            System.out.print("[ " + aux.getValue() + " ]");
            if(aux.getNext() != null) System.out.print(" -> ");
            aux = aux.getNext();
        }
        System.out.println(" <= Last");
    }

    public boolean isEmpty(){return (queue==null && size==0);}

    public int size(){return size;}
}
