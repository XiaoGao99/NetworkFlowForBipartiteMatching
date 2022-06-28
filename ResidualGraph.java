import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * A class to represent residual graph
 * The residual graph is represented by a list of nodes, an adjacency matrix
 * that represent connections, soruce node, sink node, and the bipartite
 * matching hashmap.
 * Functionalities include:
 * 1. Generate LevelGraph using BFS
 * 2. Augment any path and update the matching using hashmap
 * 3. Print the output matching
 * 
 * @author Xiao Gao
 * @date May 26 2022
 */
public class ResidualGraph {
  // number of nodes in graph
  private int size;
  // a string array to represent all nodes
  private String[] nodes;
  // an adjacency matrix to represent connections
  private int[][] connections;
  // the index of source node
  private int source;
  // the index of sink node
  private int sink;
  // the bipartite matching
  private HashMap<Integer, Integer> matching;

  /**
   * Constructor
   * 
   * @param size : Number of nodes
   * @pre: None
   * @post: The ResidualGrpah class is initialized
   * 
   */
  public ResidualGraph(int size) {
    // input size + source + sink
    this.size = size + 2;
    this.connections = new int[this.size][this.size];
    this.nodes = new String[this.size];
    // first node is source
    this.source = 0;
    // last node is sink
    this.sink = this.size - 1;
    this.nodes[source] = "source";
    this.nodes[sink] = "sink";
    this.matching = new HashMap<>();
  }

  /**
   * Getter for source
   * 
   * @pre: The object is initialized
   * @post: Get the value of source
   * @return: The value of source
   */
  public int getSource() {
    return this.source;
  }

  /**
   * Getter for sink
   * 
   * @pre: The object is initialized
   * @post: Get the value of sink
   * @return: The value of sink
   */
  public int getSink() {
    return this.sink;
  }

  /**
   * Add a node to the residual graph
   * 
   * @pre: The object is initialized
   * @post: A node is added
   * @param node  : Value of the node
   * @param index : The index of this node
   */
  public void addNode(String node, int index) {
    nodes[index] = node;
  }

  /**
   * Add an edge to the residual graph by updating the adjacency matrix
   * 
   * @param from : Index of the starting vertex
   * @param to   : Index of the ending vertex
   */
  public void addEdge(int from, int to) {
    connections[from][to] = 1;
  }

  /**
   * Link source and sink to the graph after edges are added
   * 
   * @pre: Edges are added to the residual graph
   * @post: The left half of edges are connected with source;
   *        the right half of edges are connected with sink.
   */
  public void connectSourceAndSink() {
    // Link source to the left half
    for (int i = 1; i < size / 2; i++) {
      connections[source][i] = 1;
    }
    // Link sink to the right half
    for (int i = size / 2; i < size - 1; i++) {
      connections[i][sink] = 1;
    }
  }

  /**
   * Reverse the connections in a path
   * 
   * @param path : A path from source to sink. Represented by a list of nodes
   *             (only index needed)
   * @pre: The graph contains the input path
   * @post: The connection in the path is reversed
   */
  public void reverseConnectionInPath(List<Integer> path) {
    // Reversing the edge by removing curr and creating opposite
    for (int i = 0; i < path.size() - 1; i++) {
      int from = path.get(i);
      int to = path.get(i + 1);
      // Deleting the curr connection
      connections[from][to] = 0;
      // Add the reverse connection
      connections[to][from] = 1;
    }
  }

  /**
   * Construct level graph from the residual graph by BFS.
   * 
   * @pre: The residual graph is created already.
   * @post: Find the level graph or return null if unable to find a path.
   * @return the level graph if a path is found;
   *         otherwise return ull
   */
  public LevelGraph createLevelGraph() {
    // The adjacency matrix for the level graph
    int[][] levelGraphConnections = new int[size][size];

    // Store the visited status, initilized to unseen
    boolean[] visited = new boolean[size];
    Arrays.fill(visited, false);
    // set source as visited
    visited[source] = true;

    // Initialized a queue for BFS
    LinkedList<Integer> queue = new LinkedList<Integer>();
    // Add the source
    queue.add(source);

    while (queue.size() != 0) {
      // Polling from the queue oto get the 1st element
      int curr = queue.poll();
      if (curr == sink)
        continue;
      for (int i = 1; i < size; i++) {
        if (connections[curr][i] == 1 && (!visited[i])) {
          // enqueue
          queue.add(i);
          // add to the level graph connection
          levelGraphConnections[curr][i] = 1;
          // Set to visited
          visited[i] = true;
        }
      }
    }
    // if the sink is visited, that means we find a path from source to sink,
    // thus we can return the level graph; otherwise return null.
    return visited[sink] ? new LevelGraph(size, levelGraphConnections) : null;
  }

  /**
   * Augments the flow in the path.
   * Add or update the matching using the input path.
   * 
   * @pre: The path is valid.
   * @post: The bipartite matching is updated.
   * @param path : A list of nodes from source to sink
   */
  public void augmentPath(List<Integer> path) {
    for (int i = 1; i < path.size() - 1; i += 2) {
      int from = path.get(i);
      int to = path.get(i + 1);

      // add or update the hashmap to represent the bipartite matching
      this.matching.put(from, to);
    }
  }

  /**
   * A utility function to print the output
   * 
   * @pre: The ResidualGraph object is initalized
   * @post: Output matching in the correct format
   */
  public void printMatching() {
    for (Integer key : matching.keySet()) {
      Integer value = matching.get(key);
      System.out.println(nodes[key] + " / " + nodes[value]);
    }
    System.out.println(this.matching.size() + " total matches");
  }
}
