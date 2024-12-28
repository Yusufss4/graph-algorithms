import java.util.*;

class Edge {
    int source;
    int destination;
    int weight;

    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
}

class Graph {
    Map<String, Integer> vertexMap = new HashMap<>(); // label -> vertex index eg A->0
    ArrayList<ArrayList<int[]>> adjacencyList = new ArrayList<>(); // for Prim’s
    ArrayList<Edge> directedEdges = new ArrayList<>(); // for Bellman-Ford
    int numberOfVertices = 0;

    public int getOrCreateVertexId(String label) {
        // Map a vertex label to an integer index if not seen before
        if (!vertexMap.containsKey(label)) {
            vertexMap.put(label, numberOfVertices);
            adjacencyList.add(new ArrayList<>()); // expand adjacency list for Prim’s
            numberOfVertices++;
        }
        return vertexMap.get(label);
    }

    public void addDirectedEdge(int u, int v, int w) {
        // For Bellman-Ford
        directedEdges.add(new Edge(u, v, w));
    }

    public void addUndirectedEdge(int u, int v, int w) {
        // For Prim’s
        adjacencyList.get(u).add(new int[] { v, w });
        adjacencyList.get(v).add(new int[] { u, w });
    }

    /*
     * Build a reverse map that maps vertex index to its label, such as from index 0
     * to 'A'
     */
    public Map<Integer, String> buildReverseMap() {
        Map<Integer, String> reverseMap = new HashMap<>();
        for (String label : this.vertexMap.keySet()) {
            reverseMap.put(this.vertexMap.get(label), label);
        }
        return reverseMap;
    }
}

class PrimAlgorithm {
    private static Graph graph;
    private static boolean[] inMST;
    private static int[] parent; // parent of each vertex in MST
    private static int[] key; // weights of edges connecting each vertex
    private static PriorityQueue<int[]> priorityQueue;
    private static int numberOfVertices;

    public static void runPrim(Graph graph, int sourceId) {
        PrimAlgorithm.graph = graph;
        PrimAlgorithm.numberOfVertices = graph.numberOfVertices;
        PrimAlgorithm.inMST = new boolean[numberOfVertices];
        PrimAlgorithm.parent = new int[numberOfVertices];
        PrimAlgorithm.key = new int[numberOfVertices]; // min cost to connect each node
        Arrays.fill(parent, -1);
        Arrays.fill(key, Integer.MAX_VALUE);

        key[sourceId] = 0; // start from source

        // Priority queue storing (weight, index of vertex) pairs.
        // Queue is sorted based on weight which is the first element of the pair.
        PrimAlgorithm.priorityQueue = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        priorityQueue.offer(new int[] { 0, sourceId }); // Add source vertex to the queue

        buildMST();

        int totalWeight = computeTotalWeight();
        printResults(totalWeight, sourceId);
    }

    private static void buildMST() {
        while (!priorityQueue.isEmpty()) {
            int[] top = priorityQueue.poll();
            int currentVertex = top[1];

            if (inMST[currentVertex]) {
                continue;
            }
            inMST[currentVertex] = true;

            // Traverse adjacency list
            for (int[] edge : graph.adjacencyList.get(currentVertex)) {
                int neighbor = edge[0];
                int weight = edge[1];
                if (!inMST[neighbor] && weight < key[neighbor]) {
                    key[neighbor] = weight;
                    parent[neighbor] = currentVertex;
                    priorityQueue.offer(new int[] { key[neighbor], neighbor });
                }
            }
        }
    }

    private static int computeTotalWeight() {
        int total = 0;
        for (int w : key) {
            if (w != Integer.MAX_VALUE) {
                total += w;
            }
        }
        return total;
    }

    private static void printResults(int totalWeight, int sourceId) {
        System.out.println("\nResult\nPrim’s MST total weight = " + totalWeight);

        Map<Integer, String> reverseMap = graph.buildReverseMap();
        System.out.println("MST edges (parent -> child):");
        for (int i = 0; i < numberOfVertices; i++) {
            if (i != sourceId && parent[i] != -1) {
                System.out.println(reverseMap.get(parent[i]) + " - "
                        + reverseMap.get(i) + " (weight " + key[i] + ")");
            }
        }
    }
}

class BellmanFordAlgorithm {
    private static Graph graph;
    private static int[] distance;
    private static int numberOfVertices;

    public static void runBellmanFord(Graph graph, int sourceId) {
        BellmanFordAlgorithm.graph = graph;
        BellmanFordAlgorithm.numberOfVertices = graph.numberOfVertices;
        BellmanFordAlgorithm.distance = new int[numberOfVertices];
        Arrays.fill(distance, Integer.MAX_VALUE); // Initialize to infinity

        distance[sourceId] = 0; // Source has distance 0

        relaxEdges();
        if (containsNegativeWeightCycle()) {
            System.out.println("Graph contains a negative-weight cycle!");
            return;
        }

        printResults();
    }

    private static void relaxEdges() {
        for (int i = 0; i < numberOfVertices - 1; i++) { // (V-1) iterations
            boolean updated = false;
            for (Edge edge : graph.directedEdges) { // Relax all edges
                if (canRelax(edge)) {
                    distance[edge.destination] = distance[edge.source] + edge.weight;
                    updated = true;
                }
            }
            if (!updated)
                break; // No updates, so no need to continue
        }
    }

    private static boolean canRelax(Edge e) {
        return (distance[e.source] != Integer.MAX_VALUE // If the source is reachable
                && distance[e.source] + e.weight < distance[e.destination]);
    }

    private static boolean containsNegativeWeightCycle() {
        for (Edge e : graph.directedEdges) {
            if (canRelax(e)) {
                return true;
            }
        }
        return false;
    }

    private static void printResults() {
        Map<Integer, String> reverseMap = graph.buildReverseMap();
        System.out.println("\nResult:\n");
        for (int i = 0; i < numberOfVertices; i++) {
            String label = reverseMap.get(i);
            String distStr = (distance[i] == Integer.MAX_VALUE) ? "∞" : String.valueOf(distance[i]);
            System.out.println(label + " = " + distStr);
        }
    }
}

public class CompactMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String algorithmName = scanner.nextLine().trim();

        String edgeInfo = scanner.nextLine().trim();
        int edgeCount = Integer.parseInt(edgeInfo.split(" ")[0]);

        Graph graph = new Graph();

        // Read edges
        for (int i = 0; i < edgeCount; i++) {
            String line = scanner.nextLine().trim();

            // Weight part: ":5"
            String[] parts = line.split(":");
            int weight = Integer.parseInt(parts[1].trim());
            String edgePart = parts[0].trim();

            // Identify the separator: "→" for Bellman-Ford, "-" for Prim’s
            boolean isBellman = algorithmName.equalsIgnoreCase("Bellman-ford");
            String separator = isBellman ? "→" : "-";

            // Split vertices
            String[] vertices = edgePart.split(separator);
            String first = vertices[0].trim();
            String second = vertices[1].trim();

            int u = graph.getOrCreateVertexId(first);
            int v = graph.getOrCreateVertexId(second);

            if (isBellman) {
                // Directed edge for Bellman-Ford
                graph.addDirectedEdge(u, v, weight);
            } else {
                // Undirected edge for Prim’s
                graph.addUndirectedEdge(u, v, weight);
            }
        }

        // Source line: "source: A"
        String sourceLine = scanner.nextLine().trim();
        String sourceLabel = sourceLine.split(":")[1].trim();
        int sourceId = graph.getOrCreateVertexId(sourceLabel);

        // Run algorithm
        if (algorithmName.equalsIgnoreCase("Bellman-ford")) {
            BellmanFordAlgorithm.runBellmanFord(graph, sourceId);
        } else if (algorithmName.equalsIgnoreCase("Prim’s")) {
            PrimAlgorithm.runPrim(graph, sourceId);
        } else {
            System.out.println("Unknown algorithm: " + algorithmName);
        }

        scanner.close();
    }
}
