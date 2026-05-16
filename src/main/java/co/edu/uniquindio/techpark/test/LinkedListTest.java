package co.edu.uniquindio.techpark.test;

import co.edu.uniquindio.techpark.model.structures.LinkedList;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LinkedList - Custom structure tests")
public class LinkedListTest {

    private LinkedList<String> list;

    @BeforeEach
    void setup() {
        list = new LinkedList<>();
    }

    // ----------------------------------------------------------------
    // isEmpty / getSize on empty list
    // ----------------------------------------------------------------
    @Test
    @DisplayName("New list should be empty with size 0")
    void testNewListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.getSize());
    }

    // ----------------------------------------------------------------
    // add / get
    // ----------------------------------------------------------------
    @Test
    @DisplayName("add() appends elements in order")
    void testAddAppendsInOrder() {
        list.add("Alpha");
        list.add("Beta");
        list.add("Gamma");

        assertEquals(3, list.getSize());
        assertEquals("Alpha", list.get(0));
        assertEquals("Beta", list.get(1));
        assertEquals("Gamma", list.get(2));
    }

    @Test
    @DisplayName("get() returns null for out-of-range index")
    void testGetOutOfRange() {
        list.add("Only");
        assertNull(list.get(-1));
        assertNull(list.get(1));
        assertNull(list.get(99));
    }

    // ----------------------------------------------------------------
    // addFirst
    // ----------------------------------------------------------------
    @Test
    @DisplayName("addFirst() inserts at position 0")
    void testAddFirstInsertsAtHead() {
        list.add("B");
        list.add("C");
        list.addFirst("A");

        assertEquals(3, list.getSize());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    @DisplayName("addFirst() on empty list works correctly")
    void testAddFirstOnEmptyList() {
        list.addFirst("Solo");
        assertEquals(1, list.getSize());
        assertEquals("Solo", list.get(0));
    }

    // ----------------------------------------------------------------
    // remove
    // ----------------------------------------------------------------
    @Test
    @DisplayName("remove() deletes first occurrence and returns true")
    void testRemoveExistingElement() {
        list.add("X"); list.add("Y"); list.add("Z");
        boolean removed = list.remove("Y");

        assertTrue(removed);
        assertEquals(2, list.getSize());
        assertEquals("X", list.get(0));
        assertEquals("Z", list.get(1));
    }

    @Test
    @DisplayName("remove() returns false when element is not found")
    void testRemoveNonExistingElement() {
        list.add("A"); list.add("B");
        assertFalse(list.remove("C"));
        assertEquals(2, list.getSize());
    }

    @Test
    @DisplayName("remove() on the head node updates head correctly")
    void testRemoveHead() {
        list.add("Head"); list.add("Tail");
        list.remove("Head");

        assertEquals(1, list.getSize());
        assertEquals("Tail", list.get(0));
    }

    @Test
    @DisplayName("remove() on the only element leaves list empty")
    void testRemoveOnlyElement() {
        list.add("Alone");
        list.remove("Alone");

        assertTrue(list.isEmpty());
        assertEquals(0, list.getSize());
    }

    // ----------------------------------------------------------------
    // contains
    // ----------------------------------------------------------------
    @Test
    @DisplayName("contains() returns true for existing element")
    void testContainsExisting() {
        list.add("Park"); list.add("Ride");
        assertTrue(list.contains("Ride"));
    }

    @Test
    @DisplayName("contains() returns false for missing element")
    void testContainsMissing() {
        list.add("Park");
        assertFalse(list.contains("Queue"));
    }

    @Test
    @DisplayName("contains() returns false on empty list")
    void testContainsOnEmptyList() {
        assertFalse(list.contains("Anything"));
    }

    // ----------------------------------------------------------------
    // getFirst / getLast
    // ----------------------------------------------------------------
    @Test
    @DisplayName("getFirst() returns head element")
    void testGetFirst() {
        list.add("First"); list.add("Second"); list.add("Third");
        assertEquals("First", list.getFirst());
    }

    @Test
    @DisplayName("getLast() returns tail element")
    void testGetLast() {
        list.add("First"); list.add("Second"); list.add("Third");
        assertEquals("Third", list.getLast());
    }

    @Test
    @DisplayName("getFirst() returns null on empty list")
    void testGetFirstEmpty() {
        assertNull(list.getFirst());
    }

    // ----------------------------------------------------------------
    // removeFirst / removeLast
    // ----------------------------------------------------------------
    @Test
    @DisplayName("removeFirst() removes and returns head")
    void testRemoveFirst() {
        list.add("A"); list.add("B"); list.add("C");
        String removed = list.removeFirst();

        assertEquals("A", removed);
        assertEquals(2, list.getSize());
        assertEquals("B", list.get(0));
    }

    @Test
    @DisplayName("removeLast() removes and returns tail")
    void testRemoveLast() {
        list.add("A"); list.add("B"); list.add("C");
        String removed = list.removeLast();

        assertEquals("C", removed);
        assertEquals(2, list.getSize());
        assertEquals("B", list.get(1));
    }

    // ----------------------------------------------------------------
    // sequential add / remove cycle
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Adding and removing multiple elements maintains correct size")
    void testAddRemoveCycle() {
        for (int i = 0; i < 10; i++) list.add("Item-" + i);
        assertEquals(10, list.getSize());

        for (int i = 0; i < 5; i++) list.remove("Item-" + i);
        assertEquals(5, list.getSize());
        assertFalse(list.contains("Item-0"));
        assertTrue(list.contains("Item-5"));
    }
}