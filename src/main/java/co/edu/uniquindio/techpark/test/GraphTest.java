package co.edu.uniquindio.techpark.test;

import co.edu.uniquindio.techpark.model.structures.Graph;
import co.edu.uniquindio.techpark.model.structures.GraphNode;
import co.edu.uniquindio.techpark.model.structures.LinkedList;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Graph - Custom structure tests (Dijkstra + BFS)")
public class GraphTest {

    private Graph<Integer> graph;

    @BeforeEach
    void setup() {
        graph = new Graph<>();
    }

    // ----------------------------------------------------------------
    // empty state
    // ----------------------------------------------------------------
    @Test
    @DisplayName("New graph has 0 nodes")
    void testNewGraphIsEmpty() {
        assertEquals(0, graph.getNumNodes());
    }

    // ----------------------------------------------------------------
    // addNode
    // ----------------------------------------------------------------
    @Test
    @DisplayName("addNode() increases node count")
    void testAddNodeIncreasesCount() {
        graph.addNode(1); assertEquals(1, graph.getNumNodes());
        graph.addNode(2); assertEquals(2, graph.getNumNodes());
        graph.addNode(3); assertEquals(3, graph.getNumNodes());
    }

    @Test
    @DisplayName("addNode() duplicate is ignored")
    void testAddNodeDuplicateIgnored() {
        graph.addNode(1); graph.addNode(1);
        assertEquals(1, graph.getNumNodes());
    }

    @Test
    @DisplayName("nodeExists() returns true for added node")
    void testNodeExists() {
        graph.addNode(5);
        assertTrue(graph.existsNode(5));
        assertFalse(graph.existsNode(99));
    }

    // ----------------------------------------------------------------
    // addEdge
    // ----------------------------------------------------------------
    @Test
    @DisplayName("addEdge() creates bidirectional connection (undirected graph)")
    void testAddEdgeUndirected() {
        graph.addNode(1); graph.addNode(2);
        graph.addEdge(1, 2, 10.0);

        // Dijkstra from either direction must find a path
        LinkedList<Integer> path1 = graph.dijkstra(1, 2);
        LinkedList<Integer> path2 = graph.dijkstra(2, 1);

        assertNotNull(path1); assertTrue(path1.getSize() >= 2);
        assertNotNull(path2); assertTrue(path2.getSize() >= 2);
    }

    @Test
    @DisplayName("addEdge() with nonexistent node returns false or is ignored")
    void testAddEdgeNonExistentNode() {
        graph.addNode(1);
        // adding edge to a node that does not exist should not throw
        assertDoesNotThrow(() -> graph.addEdge(1, 99, 5.0));
    }

    // ----------------------------------------------------------------
    // Dijkstra - shortest path
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Dijkstra finds direct path between two adjacent nodes")
    void testDijkstraDirectPath() {
        graph.addNode(1); graph.addNode(2);
        graph.addEdge(1, 2, 5.0);

        LinkedList<Integer> path = graph.dijkstra(1, 2);

        assertNotNull(path);
        assertEquals(2, path.getSize());
        assertEquals(1, (int) path.get(0));
        assertEquals(2, (int) path.get(1));
    }

    @Test
    @DisplayName("Dijkstra finds shortest path avoiding longer alternative")
    void testDijkstraChoosesShorterPath() {
        //  1 --10-- 2
        //  |        |
        //  3 --1--- 4 --1-- 2  (via 3 and 4 is shorter: 2+1+1=4 vs direct 10)
        //
        //  Actual graph:
        //  1 --10-- 2
        //  1 --2--  3
        //  3 --1--  4
        //  4 --1--  2
        //  Shortest 1->2: 1->3->4->2 = 2+1+1 = 4

        graph.addNode(1); graph.addNode(2); graph.addNode(3); graph.addNode(4);
        graph.addEdge(1, 2, 10.0);
        graph.addEdge(1, 3, 2.0);
        graph.addEdge(3, 4, 1.0);
        graph.addEdge(4, 2, 1.0);

        LinkedList<Integer> path = graph.dijkstra(1, 2);

        assertNotNull(path);
        // path must start at 1 and end at 2
        assertEquals(1, (int) path.get(0));
        assertEquals(2, (int) path.get(path.getSize() - 1));
        // optimal path has 4 nodes (1->3->4->2), not the direct 2-node path
        assertEquals(4, path.getSize());
    }

    @Test
    @DisplayName("Dijkstra path contains origin and destination")
    void testDijkstraPathEndsCorrectly() {
        graph.addNode(10); graph.addNode(20); graph.addNode(30);
        graph.addEdge(10, 20, 3.0);
        graph.addEdge(20, 30, 4.0);

        LinkedList<Integer> path = graph.dijkstra(10, 30);

        assertNotNull(path);
        assertEquals(10, (int) path.get(0));
        assertEquals(30, (int) path.get(path.getSize() - 1));
    }

    @Test
    @DisplayName("Dijkstra returns empty or null when no path exists")
    void testDijkstraNoPath() {
        graph.addNode(1); graph.addNode(2);
        graph.addNode(3); // isolated node, no edge to 1 or 2
        graph.addEdge(1, 2, 5.0);

        LinkedList<Integer> path = graph.dijkstra(1, 3);

        // no path should return null or empty list
        assertTrue(path == null || path.getSize() == 0,
                "Expected null or empty path when destination is unreachable");
    }

    @Test
    @DisplayName("Dijkstra from a node to itself returns single-element path or empty")
    void testDijkstraSameOriginDestination() {
        graph.addNode(1); graph.addNode(2);
        graph.addEdge(1, 2, 5.0);

        LinkedList<Integer> path = graph.dijkstra(1, 1);

        // acceptable: single element [1] or empty
        assertTrue(path == null || path.getSize() <= 1);
    }

    // ----------------------------------------------------------------
    // BFS
    // ----------------------------------------------------------------
    @Test
    @DisplayName("BFS visits all reachable nodes from origin")
    void testBFSVisitsAllNodes() {
        //  1 -- 2 -- 4
        //  |
        //  3
        graph.addNode(1); graph.addNode(2); graph.addNode(3); graph.addNode(4);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(2, 4, 1.0);

        LinkedList<Integer> visited = graph.bfs(1);

        assertNotNull(visited);
        assertEquals(4, visited.getSize()); // all 4 nodes reachable from 1
        assertTrue(visited.contains(1));
        assertTrue(visited.contains(2));
        assertTrue(visited.contains(3));
        assertTrue(visited.contains(4));
    }

    @Test
    @DisplayName("BFS first element is the origin node")
    void testBFSStartsAtOrigin() {
        graph.addNode(5); graph.addNode(6); graph.addNode(7);
        graph.addEdge(5, 6, 1.0);
        graph.addEdge(6, 7, 1.0);

        LinkedList<Integer> visited = graph.bfs(5);

        assertNotNull(visited);
        assertEquals(5, (int) visited.get(0));
    }

    @Test
    @DisplayName("BFS does not visit isolated nodes")
    void testBFSDoesNotVisitIsolated() {
        graph.addNode(1); graph.addNode(2); graph.addNode(99); // 99 isolated
        graph.addEdge(1, 2, 1.0);

        LinkedList<Integer> visited = graph.bfs(1);

        assertFalse(visited.contains(99), "Isolated node must not appear in BFS from 1");
    }

    // ----------------------------------------------------------------
    // cluster detection
    // ----------------------------------------------------------------
    @Test
    @DisplayName("detectClusters() returns 1 cluster for fully connected graph")
    void testDetectClustersFullyConnected() {
        graph.addNode(1); graph.addNode(2); graph.addNode(3);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 3, 1.0);

        LinkedList<GraphNode<Integer>> clusters = graph.detectClusters();
        assertEquals(1, clusters.getSize());
    }

    @Test
    @DisplayName("detectClusters() returns 2 clusters for disconnected graph")
    void testDetectClusters2Components() {
        // component A: 1-2
        // component B: 3-4  (no edge connecting to A)
        graph.addNode(1); graph.addNode(2);
        graph.addNode(3); graph.addNode(4);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(3, 4, 1.0);

        LinkedList<GraphNode<Integer>> clusters = graph.detectClusters();
        assertEquals(2, clusters.getSize());
    }

    @Test
    @DisplayName("detectClusters() returns N clusters for N isolated nodes")
    void testDetectClustersAllIsolated() {
        graph.addNode(1); graph.addNode(2); graph.addNode(3);

        LinkedList<GraphNode<Integer>> clusters = graph.detectClusters();
        assertEquals(3, clusters.getSize());
    }

    // ----------------------------------------------------------------
    // full park simulation
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Simulates a 5-attraction mini-park and finds optimal route")
    void testParkMapSimulation() {
        // 5 attractions as integer IDs
        // Layout:
        //  Entry(1) --50m-- Zone-A(2) --80m-- Zone-B(3)
        //                   |                  |
        //                 100m                60m
        //                   |                  |
        //               Zone-C(4) ----40m---- Zone-D(5)
        //
        // Shortest 1->5:
        //   1->2->3->5: 50+80+60 = 190
        //   1->2->4->5: 50+100+40 = 190
        // Both equally short. Path size must be 4.

        for (int i = 1; i <= 5; i++) graph.addNode(i);
        graph.addEdge(1, 2, 50.0);
        graph.addEdge(2, 3, 80.0);
        graph.addEdge(3, 5, 60.0);
        graph.addEdge(2, 4, 100.0);
        graph.addEdge(4, 5, 40.0);

        LinkedList<Integer> route = graph.dijkstra(1, 5);

        assertNotNull(route);
        assertEquals(1, (int) route.get(0));                      // starts at entry
        assertEquals(5, (int) route.get(route.getSize() - 1));    // ends at destination
        assertEquals(4, route.getSize());                          // 4 stops

        // BFS from entry visits all nodes
        LinkedList<Integer> allNodes = graph.bfs(1);
        assertEquals(5, allNodes.getSize());
    }
}