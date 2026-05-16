package co.edu.uniquindio.techpark.test;

import co.edu.uniquindio.techpark.model.structures.BSTree;
import co.edu.uniquindio.techpark.model.structures.LinkedList;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BSTree - Binary Search Tree tests")
public class BSTreeTest {

    private BSTree<Integer> tree;

    @BeforeEach
    void setup() {
        tree = new BSTree<>();
    }

    // ----------------------------------------------------------------
    // empty state
    // ----------------------------------------------------------------
    @Test
    @DisplayName("New tree should be empty")
    void testNewTreeIsEmpty() {
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.getSize());
    }

    @Test
    @DisplayName("search() returns null on empty tree")
    void testSearchOnEmptyTree() {
        assertNull(tree.search(42));
    }

    @Test
    @DisplayName("min() and max() return null on empty tree")
    void testMinMaxOnEmptyTree() {
        assertNull(tree.min());
        assertNull(tree.max());
    }

    // ----------------------------------------------------------------
    // insert / search
    // ----------------------------------------------------------------
    @Test
    @DisplayName("insert() and search() single element")
    void testInsertAndSearchSingle() {
        tree.insert(10);

        assertFalse(tree.isEmpty());
        assertEquals(1, tree.getSize());
        assertNotNull(tree.search(10));
        assertEquals(10, tree.search(10).getData());
    }

    @Test
    @DisplayName("search() returns null for missing element")
    void testSearchMissing() {
        tree.insert(5); tree.insert(3); tree.insert(7);
        assertNull(tree.search(99));
    }

    @Test
    @DisplayName("insert() duplicate does not increase size")
    void testInsertDuplicate() {
        tree.insert(5); tree.insert(5);
        assertEquals(1, tree.getSize());
    }

    @Test
    @DisplayName("insert() multiple elements are all searchable")
    void testInsertMultiple() {
        int[] values = {10, 5, 15, 3, 7, 12, 18};
        for (int v : values) tree.insert(v);

        assertEquals(values.length, tree.getSize());
        for (int v : values) {
            assertNotNull(tree.search(v), "Should find: " + v);
            assertEquals(v, tree.search(v).getData());
        }
    }

    // ----------------------------------------------------------------
    // inorder traversal (must return sorted elements)
    // ----------------------------------------------------------------
    @Test
    @DisplayName("inorder() returns elements in ascending order")
    void testInorderSorted() {
        tree.insert(5); tree.insert(3); tree.insert(8);
        tree.insert(1); tree.insert(4); tree.insert(7); tree.insert(9);

        LinkedList<Integer> result = tree.inorder();
        int n = result.getSize();
        assertEquals(7, n);

        // verify ascending order
        for (int i = 0; i < n - 1; i++) {
            assertTrue(result.get(i) <= result.get(i + 1),
                    "Element at " + i + " (" + result.get(i) + ") must be <= element at " + (i+1) + " (" + result.get(i+1) + ")");
        }
    }

    @Test
    @DisplayName("inorder() on single element returns list of one")
    void testInorderSingleElement() {
        tree.insert(42);
        LinkedList<Integer> result = tree.inorder();
        assertEquals(1, result.getSize());
        assertEquals(42, result.get(0));
    }

    @Test
    @DisplayName("inorder() on empty tree returns empty list")
    void testInorderEmpty() {
        LinkedList<Integer> result = tree.inorder();
        assertEquals(0, result.getSize());
    }

    // ----------------------------------------------------------------
    // min / max
    // ----------------------------------------------------------------
    @Test
    @DisplayName("min() returns the smallest element")
    void testMin() {
        tree.insert(10); tree.insert(3); tree.insert(7); tree.insert(1); tree.insert(15);
        assertEquals(1, tree.min());
    }

    @Test
    @DisplayName("max() returns the largest element")
    void testMax() {
        tree.insert(10); tree.insert(3); tree.insert(7); tree.insert(1); tree.insert(15);
        assertEquals(15, tree.max());
    }

    @Test
    @DisplayName("min() equals max() for single-element tree")
    void testMinEqualsMaxSingleElement() {
        tree.insert(42);
        assertEquals(42, tree.min());
        assertEquals(42, tree.max());
    }

    // ----------------------------------------------------------------
    // delete
    // ----------------------------------------------------------------
    @Test
    @DisplayName("delete() removes a leaf node")
    void testDeleteLeaf() {
        tree.insert(10); tree.insert(5); tree.insert(15);
        tree.delete(5);

        assertEquals(2, tree.getSize());
        assertNull(tree.search(5));
        assertNotNull(tree.search(10));
        assertNotNull(tree.search(15));
    }

    @Test
    @DisplayName("delete() removes a node with one child")
    void testDeleteNodeWithOneChild() {
        tree.insert(10); tree.insert(5); tree.insert(3);
        tree.delete(5); // has only left child (3)

        assertEquals(2, tree.getSize());
        assertNull(tree.search(5));
        assertNotNull(tree.search(3));  // child promoted
        assertNotNull(tree.search(10));
    }

    @Test
    @DisplayName("delete() removes a node with two children")
    void testDeleteNodeWithTwoChildren() {
        tree.insert(10); tree.insert(5); tree.insert(15);
        tree.insert(3);  tree.insert(7); tree.insert(12); tree.insert(18);
        tree.delete(10); // root with two children

        assertEquals(6, tree.getSize());
        assertNull(tree.search(10));

        // inorder must still be sorted
        LinkedList<Integer> sorted = tree.inorder();
        for (int i = 0; i < sorted.getSize() - 1; i++)
            assertTrue(sorted.get(i) < sorted.get(i + 1));
    }

    @Test
    @DisplayName("delete() on missing element does not throw or change size")
    void testDeleteMissingElement() {
        tree.insert(1); tree.insert(2); tree.insert(3);
        assertDoesNotThrow(() -> tree.delete(99));
        assertEquals(3, tree.getSize());
    }

    @Test
    @DisplayName("delete() all elements results in empty tree")
    void testDeleteAllElements() {
        int[] values = {5, 3, 8, 1, 4};
        for (int v : values) tree.insert(v);
        for (int v : values) tree.delete(v);

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.getSize());
    }

    // ----------------------------------------------------------------
    // height
    // ----------------------------------------------------------------
    @Test
    @DisplayName("getHeight() returns 0 for empty tree")
    void testHeightEmpty() {
        assertEquals(0, tree.getHeight());
    }

    @Test
    @DisplayName("getHeight() returns 1 for single-node tree")
    void testHeightSingleNode() {
        tree.insert(10);
        assertEquals(1, tree.getHeight());
    }

    @Test
    @DisplayName("getHeight() returns correct height for balanced tree")
    void testHeightBalanced() {
        //        10
        //       /  \
        //      5    15
        //     / \   / \
        //    3   7 12  18
        tree.insert(10); tree.insert(5); tree.insert(15);
        tree.insert(3);  tree.insert(7); tree.insert(12); tree.insert(18);
        assertEquals(3, tree.getHeight());
    }

    @Test
    @DisplayName("getHeight() returns correct height for skewed tree")
    void testHeightSkewed() {
        // right-skewed: 1 -> 2 -> 3 -> 4 -> 5
        for (int i = 1; i <= 5; i++) tree.insert(i);
        assertEquals(5, tree.getHeight());
    }

    // ----------------------------------------------------------------
    // string tree (simulates attraction name catalog)
    // ----------------------------------------------------------------
    @Test
    @DisplayName("BSTree<String> works correctly for attraction name catalog")
    void testStringTree() {
        BSTree<String> catalog = new BSTree<>();
        catalog.insert("Wave Pool");
        catalog.insert("Extreme Roller Coaster");
        catalog.insert("VR Escape Room");
        catalog.insert("Haunted Mine");
        catalog.insert("Lazy River");

        assertEquals(5, catalog.getSize());

        // inorder gives alphabetical order
        LinkedList<String> sorted = catalog.inorder();
        assertEquals("Extreme Roller Coaster", sorted.get(0));
        assertEquals("Haunted Mine", sorted.get(1));
        assertEquals("Lazy River", sorted.get(2));
        assertEquals("VR Escape Room", sorted.get(3));
        assertEquals("Wave Pool", sorted.get(4));

        // search works
        assertNotNull(catalog.search("Lazy River"));
        assertNull(catalog.search("Nonexistent Ride"));
    }
}