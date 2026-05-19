package co.edu.uniquindio.techpark.model.structures;

public class Graph<T extends Comparable<T>> {
    private LinkedList<GraphNode<T>> nodes;
    private int numNodes;
    private boolean isDirected;

    public Graph() {
        this.nodes = new LinkedList<>();
        this.numNodes = 0;
        this.isDirected = false;
    }

    public LinkedList<GraphNode<T>> getNodes() {
        return nodes;
    }
    public void setNodes(LinkedList<GraphNode<T>> nodes) {
        this.nodes = nodes;
    }

    public int getNumNodes() {
        return numNodes;
    }
    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public boolean isDirected() {
        return isDirected;
    }
    public void setDirected(boolean directed) {
        isDirected = directed;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", numNodes=" + numNodes +
                ", isDirected=" + isDirected +
                '}';
    }

    public Graph(boolean isDirected) {
        this.nodes = new LinkedList<>();
        this.numNodes = 0;
        this.isDirected = isDirected;
    }

    // ----------------------------------------------------------------
    // node management
    // ----------------------------------------------------------------

    public void addNode(T data) {
        if (data == null) return;
        if (getNode(data) != null) {
            System.out.println("The node already exists in the graph.");
            return;
        }
        nodes.add(new GraphNode<>(data));
        numNodes++;
        System.out.println("Node added: " + data);
    }

    public boolean removeNode(T data) {
        GraphNode<T> target = getNode(data);
        if (target == null) {
            System.out.println("Node not found.");
            return false;
        }
        int n = nodes.getSize();
        for (int i = 0; i < n; i++) {
            GraphNode<T> node = nodes.get(i);
            if (node != null && !node.equals(target)) {
                node.removeEdge(target);
            }
        }
        boolean result = nodes.remove(target);
        if (result) {
            numNodes--;
            System.out.println("Node removed: " + data);
        }
        return result;
    }

    public GraphNode<T> getNode(T data) {
        if (data == null) return null;
        int n = nodes.getSize();
        for (int i = 0; i < n; i++) {
            GraphNode<T> node = nodes.get(i);
            if (node == null) continue;
            T nodeData = node.getData();
            if (nodeData == null) continue;
            if (nodeData == data) return node;
            if (nodeData.compareTo(data) == 0) return node;
        }
        return null;
    }

    public boolean existsNode(T data) {
        return getNode(data) != null;
    }

    // ----------------------------------------------------------------
    // edge management
    // ----------------------------------------------------------------

    public boolean addEdge(T source, T destination, double weight) {
        GraphNode<T> sourceNode  = getNode(source);
        GraphNode<T> destNode = getNode(destination);
        if (sourceNode == null || destNode == null) {
            System.out.println("One or both nodes do not exist in the graph.");
            return false;
        }
        if (weight <= 0) {
            System.out.println("The edge weight must be greater than zero.");
            return false;
        }
        String description = source + " -> " + destination;
        sourceNode.addEdge(new GraphEdge<>(destNode, weight, description));
        if (!isDirected) {
            String reverseDescription = destination + " -> " + source;
            destNode.addEdge(new GraphEdge<>(sourceNode, weight, reverseDescription));
        }
        return true;
    }

    public boolean removeEdge(T source, T destination) {
        GraphNode<T> sourceNode  = getNode(source);
        GraphNode<T> destNode = getNode(destination);
        if (sourceNode == null || destNode == null) return false;
        boolean result = sourceNode.removeEdge(destNode);
        if (!isDirected) {
            destNode.removeEdge(sourceNode);
        }
        return result;
    }

    // ----------------------------------------------------------------
    // dijkstra
    // ----------------------------------------------------------------

    public LinkedList<T> dijkstra(T source, T destination) {
        if (source == null || destination == null) return null;
        GraphNode<T> sourceNode  = getNode(source);
        GraphNode<T> destNode = getNode(destination);
        if (sourceNode == null || destNode == null) return null;

        resetNodes();
        sourceNode.setDistance(0.0);

        LinkedList<GraphNode<T>> pending = new LinkedList<>();
        pending.add(sourceNode);

        while (!pending.isEmpty()) {
            GraphNode<T> current = extractMin(pending);
            if (current == null) break;
            current.setVisited(true);

            if (current.getData().compareTo(destination) == 0) break;

            LinkedList<GraphEdge<T>> edges = current.getEdges();
            int ne = edges.getSize();
            for (int i = 0; i < ne; i++) {
                GraphEdge<T> edge = edges.get(i);
                if (edge == null) continue;
                GraphNode<T> neighbor = edge.getDestination();
                if (neighbor.isVisited()) continue;
                double newDistance = current.getDistance() + edge.getWeight();
                if (newDistance < neighbor.getDistance()) {
                    neighbor.setDistance(newDistance);
                    neighbor.setPrevious(current);
                    if (!pending.contains(neighbor)) {
                        pending.add(neighbor);
                    }
                }
            }
        }
        return reconstructPath(destNode);
    }

    private GraphNode<T> extractMin(LinkedList<GraphNode<T>> list) {
        if (list.isEmpty()) return null;
        GraphNode<T> min = list.get(0);
        int n = list.getSize();
        for (int i = 1; i < n; i++) {
            GraphNode<T> node = list.get(i);
            if (node != null && node.getDistance() < min.getDistance()) {
                min = node;
            }
        }
        list.remove(min);
        return min;
    }

    private LinkedList<T> reconstructPath(GraphNode<T> destination) {
        LinkedList<T> path = new LinkedList<>();
        if (destination.getDistance() == Double.MAX_VALUE) {
            System.out.println("No path exists to the destination.");
            return path;
        }
        GraphNode<T> current = destination;
        LinkedList<T> reversed = new LinkedList<>();
        while (current != null) {
            reversed.add(current.getData());
            current = current.getPrevious();
        }
        int n = reversed.getSize();
        for (int i = n - 1; i >= 0; i--) {
            path.add(reversed.get(i));
        }
        return path;
    }

    // ----------------------------------------------------------------
    // bfs
    // ----------------------------------------------------------------

    public LinkedList<T> bfs(T source) {
        GraphNode<T> sourceNode = getNode(source);
        if (sourceNode == null) return null;

        resetNodes();
        LinkedList<T> result = new LinkedList<>();
        LinkedList<GraphNode<T>> queue = new LinkedList<>();

        sourceNode.setVisited(true);
        queue.add(sourceNode);

        while (!queue.isEmpty()) {
            GraphNode<T> current = queue.get(0);
            queue.remove(current);
            result.add(current.getData());

            LinkedList<GraphEdge<T>> edges = current.getEdges();
            int ne = edges.getSize();
            for (int i = 0; i < ne; i++) {
                GraphEdge<T> edge = edges.get(i);
                if (edge == null) continue;
                GraphNode<T> neighbor = edge.getDestination();
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    queue.add(neighbor);
                }
            }
        }
        return result;
    }

    // ----------------------------------------------------------------
    // cluster detection
    // ----------------------------------------------------------------

    public LinkedList<GraphNode<T>> detectClusters() {
        resetNodes();
        LinkedList<GraphNode<T>> clusters = new LinkedList<>();
        int n = nodes.getSize();

        for (int i = 0; i < n; i++) {
            GraphNode<T> node = nodes.get(i);
            if (node != null && !node.isVisited()) {
                bfsCluster(node);
                clusters.add(node);
            }
        }
        System.out.println("Clusters detected: " + clusters.getSize());
        return clusters;
    }

    private void bfsCluster(GraphNode<T> start) {
        LinkedList<GraphNode<T>> queue = new LinkedList<>();
        start.setVisited(true);
        queue.add(start);

        while (!queue.isEmpty()) {
            GraphNode<T> current = queue.get(0);
            queue.remove(current);

            LinkedList<GraphEdge<T>> edges = current.getEdges();
            int ne = edges.getSize();
            for (int i = 0; i < ne; i++) {
                GraphEdge<T> edge = edges.get(i);
                if (edge == null) continue;
                GraphNode<T> neighbor = edge.getDestination();
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    queue.add(neighbor);
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // utilities
    // ----------------------------------------------------------------

    private void resetNodes() {
        int n = nodes.getSize();
        for (int i = 0; i < n; i++) {
            GraphNode<T> node = nodes.get(i);
            if (node != null) node.reset();
        }
    }

    public void printGraph() {
        int n = nodes.getSize();
        System.out.println("=== TECH-PARK UQ GRAPH ===");
        for (int i = 0; i < n; i++) {
            GraphNode<T> node = nodes.get(i);
            if (node == null) continue;
            StringBuilder sb = new StringBuilder();
            sb.append(node.getData()).append(" -> ");
            LinkedList<GraphEdge<T>> edges = node.getEdges();
            int ne = edges.getSize();
            for (int j = 0; j < ne; j++) {
                GraphEdge<T> edge = edges.get(j);
                if (edge != null) {
                    sb.append(edge.getDestination().getData())
                            .append("(").append(edge.getWeight()).append("m)");
                    if (j < ne - 1) sb.append(", ");
                }
            }
            System.out.println(sb.toString());
        }
        System.out.println("==========================");
    }
}