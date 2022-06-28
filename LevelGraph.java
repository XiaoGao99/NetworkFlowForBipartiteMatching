import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent a level graph.
 * A level graph contains nodes from the residual graph via BFS in
 * each phase of the Dinitz algorithm.
 * Getters and Setters are not provided since will not be used
 * Use a list of nodes to represent data, and an adjacency matrix to
 * represent connections between node.
 * Functionalities include: 
 * 1. Get neighbors from the current node.
 * 2. Delete edges on the path.
 * 3. Delete the node
 * 
 * @author Xiao Gao
 * @date May 26 2022
 */

public class LevelGraph {

  private int size; // number of nodes
  private int[][] connections; // adjacency matrix to represent the Level graph

  /**
   * Constructor
   * 
   * @param size        : Number of nodes
   * @param connections : The adjacency matrix to represent connections
   * @pre : None
   * @post : The LevelGraph class is initialized
   */
  public LevelGraph(int size, int[][] connections) {
    this.size = size;
    this.connections = connections;
  }

  /**
   * Get all the neighbors of a given node
   * 
   * @param node : The index of the node we want to get neighbors with.
   * @pre: Node must within the matrix. i.e. not out of bound
   * @post: All the neighbors of the node are found.
   * @return List of all the neighbors of the given node.
   */
  public ArrayList<Integer> getNeighbors(int node) {
    ArrayList<Integer> neighbors = new ArrayList<>();
    for (int i = 0; i < this.size; i++) {
      if (this.connections[node][i] == 1) {
        neighbors.add(i);
      }
    }
    return neighbors;
  }

  /**
   * Deleted edges on the path by updating the adjacency matrix
   * 
   * @param path : A list of nodes representing a path
   * @pre: The level graph must contain the edges in the input path
   * @post: Delete all edges in the input path
   */

  public void deleteEdgesOnAPath(List<Integer> path) {
    for (int i = 0; i < path.size() - 1; i++) {
      int start = path.get(i);
      int end = path.get(i + 1);
      connections[start][end] = 0;
    }
  }

  /**
   * Deletes a node by removing its incoming and outgoing edges
   * 
   * @param node : The index of the node we want to get neighbors with.
   * @pre: Node must within the matrix. i.e. not out of bound
   * @post: The input node is deleted from graph
   */
  public void deleteNode(int node) {
    for (int i = 0; i < this.size; i++) {
      this.connections[node][i] = 0;
      this.connections[i][node] = 0;
    }
  }
}