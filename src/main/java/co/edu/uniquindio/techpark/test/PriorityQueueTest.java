package co.edu.uniquindio.techpark.test;

import co.edu.uniquindio.techpark.model.structures.PriorityQueue;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PriorityQueue - Custom structure tests")
public class PriorityQueueTest {

    private PriorityQueue<String> queue;

    @BeforeEach
    void setup() {
        queue = new PriorityQueue<>();
    }

    // ----------------------------------------------------------------
    // empty state
    // ----------------------------------------------------------------
    @Test
    @DisplayName("New queue should be empty with size 0")
    void testNewQueueIsEmpty() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.getSize());
    }

    @Test
    @DisplayName("peek() returns null on empty queue")
    void testPeekOnEmptyQueue() {
        assertNull(queue.peek());
    }

    @Test
    @DisplayName("dequeue() returns null on empty queue")
    void testDequeueOnEmptyQueue() {
        assertNull(queue.dequeue());
    }

    // ----------------------------------------------------------------
    // enqueue / dequeue basic
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Single enqueue then dequeue returns that element")
    void testSingleEnqueueDequeue() {
        queue.enqueue("Visitor-A", 2);

        assertFalse(queue.isEmpty());
        assertEquals(1, queue.getSize());
        assertEquals("Visitor-A", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("peek() returns head without removing it")
    void testPeekDoesNotRemove() {
        queue.enqueue("Alice", 1);

        assertEquals("Alice", queue.peek());
        assertEquals(1, queue.getSize()); // still in queue
        assertEquals("Alice", queue.dequeue());
    }

    // ----------------------------------------------------------------
    // priority ordering
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Fast-Pass (priority 1) is dequeued before General (priority 2)")
    void testFastPassBeforeGeneral() {
        queue.enqueue("General-Bob", 2);
        queue.enqueue("FastPass-Alice", 1);
        queue.enqueue("General-Clara", 2);

        assertEquals("FastPass-Alice", queue.dequeue());
    }

    @Test
    @DisplayName("All Fast-Pass visitors exit before any General visitor")
    void testAllFastPassBeforeAllGeneral() {
        queue.enqueue("General-1", 2);
        queue.enqueue("FastPass-1", 1);
        queue.enqueue("General-2", 2);
        queue.enqueue("FastPass-2", 1);

        assertEquals("FastPass-1", queue.dequeue());
        assertEquals("FastPass-2", queue.dequeue());
        assertEquals("General-1", queue.dequeue());
        assertEquals("General-2", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("Lower numeric priority value = higher service priority")
    void testLowerNumberIsHigherPriority() {
        queue.enqueue("Low-Priority", 3);
        queue.enqueue("Medium-Priority", 2);
        queue.enqueue("High-Priority", 1);

        assertEquals("High-Priority", queue.dequeue());
        assertEquals("Medium-Priority", queue.dequeue());
        assertEquals("Low-Priority", queue.dequeue());
    }

    // ----------------------------------------------------------------
    // FIFO within same priority
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Same-priority elements follow FIFO order")
    void testFIFOWithinSamePriority() {
        queue.enqueue("First-General", 2);
        queue.enqueue("Second-General", 2);
        queue.enqueue("Third-General", 2);

        assertEquals("First-General", queue.dequeue());
        assertEquals("Second-General", queue.dequeue());
        assertEquals("Third-General", queue.dequeue());
    }

    @Test
    @DisplayName("Fast-Pass FIFO is preserved independently of General FIFO")
    void testFIFOPreservedPerPriorityLevel() {
        queue.enqueue("FP-First", 1);
        queue.enqueue("Gen-First", 2);
        queue.enqueue("FP-Second", 1);
        queue.enqueue("Gen-Second", 2);

        assertEquals("FP-First", queue.dequeue());
        assertEquals("FP-Second", queue.dequeue());
        assertEquals("Gen-First", queue.dequeue());
        assertEquals("Gen-Second", queue.dequeue());
    }

    // ----------------------------------------------------------------
    // size tracking
    // ----------------------------------------------------------------
    @Test
    @DisplayName("getSize() reflects each enqueue and dequeue")
    void testSizeTracking() {
        assertEquals(0, queue.getSize());
        queue.enqueue("A", 1); assertEquals(1, queue.getSize());
        queue.enqueue("B", 2); assertEquals(2, queue.getSize());
        queue.enqueue("C", 1); assertEquals(3, queue.getSize());
        queue.dequeue(); assertEquals(2, queue.getSize());
        queue.dequeue(); assertEquals(1, queue.getSize());
        queue.dequeue(); assertEquals(0, queue.getSize());
    }

    // ----------------------------------------------------------------
    // getPosition
    // ----------------------------------------------------------------
    @Test
    @DisplayName("getPosition() returns 0 for head element")
    void testGetPositionHead() {
        queue.enqueue("Alice", 1);
        queue.enqueue("Bob", 2);
        assertEquals(0, queue.getPosition("Alice"));
    }

    @Test
    @DisplayName("getPosition() returns correct index for mid-queue elements")
    void testGetPositionMid() {
        queue.enqueue("A", 2);
        queue.enqueue("B", 2);
        queue.enqueue("C", 2);
        assertEquals(0, queue.getPosition("A"));
        assertEquals(1, queue.getPosition("B"));
        assertEquals(2, queue.getPosition("C"));
    }

    @Test
    @DisplayName("getPosition() returns -1 for element not in queue")
    void testGetPositionNotFound() {
        queue.enqueue("Alice", 1);
        assertEquals(-1, queue.getPosition("Bob"));
    }

    // ----------------------------------------------------------------
    // clear
    // ----------------------------------------------------------------
    @Test
    @DisplayName("clear() empties the queue completely")
    void testClear() {
        queue.enqueue("A", 1); queue.enqueue("B", 2); queue.enqueue("C", 1);
        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.getSize());
        assertNull(queue.peek());
    }

    // ----------------------------------------------------------------
    // full simulation: park attraction queue
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Simulates a park attraction queue with mixed ticket types")
    void testParkQueueSimulation() {
        // 3 general visitors arrive first
        queue.enqueue("General-Carlos", 2);
        queue.enqueue("General-Maria", 2);
        queue.enqueue("General-Luis", 2);

        // 2 fast-pass arrive later
        queue.enqueue("FastPass-Alice", 1);
        queue.enqueue("FastPass-Dylan", 1);

        // Fast-pass goes first regardless of arrival order
        assertEquals("FastPass-Alice", queue.dequeue());
        assertEquals("FastPass-Dylan", queue.dequeue());

        // General visitors in arrival order
        assertEquals("General-Carlos", queue.dequeue());
        assertEquals("General-Maria", queue.dequeue());
        assertEquals("General-Luis", queue.dequeue());

        assertTrue(queue.isEmpty());
    }
}