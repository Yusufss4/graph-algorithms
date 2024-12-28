## README

### Overview

This repository contains a **single Java file** (`CompactMain.java`) that implements two classic graph algorithms:

1. **Bellman-Ford** – Finds the shortest paths from a single source to all other vertices in a **directed** graph, allowing for **negative edge weights**.
2. **Prim’s** – Constructs a **Minimum Spanning Tree (MST)** of an **undirected** graph starting from a specified source vertex.

The code supports **both** algorithms, choosing which one to run based on the first line of input (“Bellman-ford” or “Prim’s”).

This project is written for the Boğaziçi University Master of Science in Software Engineering SWE 510.01 Data Structures and Algorithms course to showcase the usage of Bellman-Ford and Prim's algorithms on graphs.

---

## Table of Contents

- [README](#readme)
  - [Overview](#overview)
- [Table of Contents](#table-of-contents)
- [How to Compile and Run](#how-to-compile-and-run)
- [Input Format](#input-format)
  - [Example: Bellman-Ford](#example-bellman-ford)
  - [Example: Prim’s](#example-prims)
- [Classes and Code Structure](#classes-and-code-structure)
  - [1. `Edge` Class](#1-edge-class)
  - [2. `Graph` Class](#2-graph-class)
  - [3. `PrimAlgorithm` Class](#3-primalgorithm-class)
  - [4. `BellmanFordAlgorithm` Class](#4-bellmanfordalgorithm-class)
  - [5. `CompactMain` Class](#5-compactmain-class)
- [Algorithm Descriptions](#algorithm-descriptions)
  - [Bellman-Ford Algorithm](#bellman-ford-algorithm)
  - [Prim’s Algorithm](#prims-algorithm)
- [Contact / Further Enhancements](#contact--further-enhancements)

---

## How to Compile and Run

1. **Clone** or **download** this repository.
2. **Navigate** to the folder containing `CompactMain.java`.
3. **Compile**:
   ```bash
   javac CompactMain.java
   ```
4. **Run**:
   ```bash
   java CompactMain
   ```
5. **Provide input** in the **formats described** below (manually, or redirect from a file).
   - The first line should be either **`Bellman-ford`** or **`Prim’s`**.
   - Then specify the number of edges, lines describing edges, and finally the source vertex line.

---

## Input Format

The program reads from standard input (e.g., console or a file). The **first line** is the algorithm name (`Bellman-ford` or `Prim’s`).

1. `Bellman-ford` expects **directed** edges separated by the symbol `→`.
2. `Prim’s` expects **undirected** edges separated by the symbol `-`.

Follow this with a line like `"12 edges"` to specify how many edges there are.

Then, provide **that many** lines of edges in the format:

```
A → B: 4
```

or

```
A - B: 4
```

Finally, end with the source vertex line, e.g.:

```
source: A
```

### Example: Bellman-Ford

```
Bellman-ford
12 edges
A → B: 4
A → C: 2
B → C: 3
B → D: 2
B → E: 3
C → B: -1
C → F: 4
D → F: -3
D → G: 1
E → G: -2
F → H: 2
G → F: -2
source: A
```

### Example: Prim’s

```
Prim’s
11 edges
A - B: 3
A - C: 1
A - D: 5
B - C: 6
B - E: 7
C - D: 2
C - E: 4
D - F: 8
E - F: 9
E - G: 2
F - G: 6
source: A
```

---

## Classes and Code Structure

Below is a **high-level overview** of the classes in `CompactMain.java`.

### 1. `Edge` Class

A lightweight container for a directed edge:

```java
class Edge {
    int source;
    int destination;
    int weight;
    ...
}
```

- **`source`**: The integer index of the source vertex
- **`destination`**: The integer index of the destination vertex
- **`weight`**: The edge weight (can be negative for Bellman-Ford)

### 2. `Graph` Class

Responsible for:

- **Mapping** vertex labels (e.g. `"A"`) to an integer ID (`0, 1, 2, ...`).
- Holding:
  - An **adjacency list** (`adjacencyList`) for Prim’s (undirected).
  - A **list of directed edges** (`directedEdges`) for Bellman-Ford.

Key methods:

- `getOrCreateVertexId(label)`: Assigns an ID to a new label or retrieves an existing one.
- `addDirectedEdge(u, v, w)`: Adds a directed edge (for Bellman-Ford).
- `addUndirectedEdge(u, v, w)`: Adds an undirected edge (for Prim’s).
- `buildReverseMap()`: Builds a reverse lookup from numeric ID back to label for printing results.

### 3. `PrimAlgorithm` Class

Implements **Prim’s MST** logic with static methods:

- **`runPrim(graph, sourceId)`**: Entry point.
  1. Initializes arrays:
     - `inMST[]` to track which vertices are already in MST.
     - `parent[]` to record MST edges.
     - `key[]` for the minimum edge weight that connects each vertex to the MST.
  2. Builds MST using a **min-heap** (`PriorityQueue<int[]>`):
     - Continually extract the vertex with the smallest key.
     - Update neighbors that could be connected with cheaper edges.
  3. Computes MST total weight and prints results.

### 4. `BellmanFordAlgorithm` Class

Implements **Bellman-Ford** with static methods:

- **`runBellmanFord(graph, sourceId)`**: Entry point.
  1. Initializes `distance[]` with `Integer.MAX_VALUE`, except `distance[sourceId] = 0`.
  2. **Relaxes** all edges up to `(V - 1)` times in `relaxEdges()`.
  3. Checks for **negative-weight cycles** in `containsNegativeWeightCycle()`.
  4. Prints distances if there is no negative cycle.

### 5. `CompactMain` Class

Contains the `main(...)` method. Handles:

1. **Reading** user input with a `Scanner`.
2. Determining whether to run **Bellman-Ford** or **Prim’s**.
3. Parsing edge lines and building the `Graph` object.
4. Reading the **source** vertex.
5. **Calling** the appropriate algorithm’s `run...()` method.

---

## Algorithm Descriptions

### Bellman-Ford Algorithm

**Purpose**: Finds the shortest path from a single source to all other vertices in a **directed** graph, which **may contain negative edge weights** (but no negative cycles reachable from the source).

1. **Initialization**:
   - `distance[source] = 0`; all other `distance[v] = ∞`.
2. **Edge Relaxation** (repeated `V - 1` times):
   - For each directed edge `(u, v, w)`, if `distance[u] + w < distance[v]`, update `distance[v] = distance[u] + w`.
   - This progressively improves estimates of the shortest paths.
3. **Negative Cycle Detection**:
   - Perform one more pass. If any `distance[v]` can still be improved, a **negative cycle** exists.

**Complexity**: O(V \* E), where V is the number of vertices, E is the number of edges.

### Prim’s Algorithm

**Purpose**: Finds a **Minimum Spanning Tree** (MST) in a weighted **undirected** graph.

1. **Initialization**:
   - Pick a source vertex (e.g., `A`).
   - Maintain `key[v]`, which is the **minimum weight** edge connecting `v` to the MST.
2. **MST Construction**:
   - Use a **min-heap** to choose the **next** vertex with the **smallest** `key[v]` value.
   - Mark it as included in MST (`inMST[v] = true`).
   - Update `key[w]` of the neighbors `w` if the new edge to w is cheaper.
3. **Repeat** until all vertices are in MST or the min-heap is empty.

**Complexity**:

- Using a binary min-heap (priority queue), O(E log V).

---

## Contact / Further Enhancements

- **Contact**: If you encounter issues or have questions, please open an issue or reach out to the code’s author.
- **Potential enhancements**:
  - Parse multiple test cases in a single run.
  - Add **Dijkstra** or **Kruskal** algorithms.
  - Incorporate more robust error handling and input validation.

Enjoy exploring **Bellman-Ford** and **Prim’s** with this sample Java implementation!

