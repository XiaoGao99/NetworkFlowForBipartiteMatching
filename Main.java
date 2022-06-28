import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Driver class for Program 3.
 * Applies Ford-Fulkerson algorithm to solve bipartite matching problem.
 * 
 * Functionalities include:
 * 1. Dinic’s algorithm is implemented
 * 2. Read in file
 * 
 * @author Xiao Gao
 * @date May 27 2022
 */
public class Main {

  /**
   * Main method.
   * Generate a residual graph by calling getResidualGraph(),
   * then pass the graph to run() method to output the result.
   * 
   */
  public static void main(String[] args) {
    ResidualGraph residualGraph = getResidualGraph();
    run(residualGraph);
  }

  /**
   * A utility function to read in file.
   * 
   * @pre : 1. File name must be exact 'program3data.txt'
   *      2. File must follow this format: "The first line will hold the
   *      number of nodes n, which will be even.
   *      The next n/2 lines will store the names of items to be matched in
   *      the left set.
   *      Then, n/2 further lines will store the names of items to be
   *      matching in the right set.
   *      Next, the number of edges will be stored and following that will
   *      be the edges (one per line) indexing the nodes using 1-based
   *      indexing." (cited from UWB CSS449 Program3 Description)
   * @post : The file is prcessed, and a residual graph is created using the
   *       file data.
   */
  private static ResidualGraph getResidualGraph() {
    Scanner reader = null;
    // open the file
    try {
      File file = new File("program3data.txt");
      reader = new Scanner(file);
    } catch (FileNotFoundException e) {
      System.out.println("File out found!");
      e.printStackTrace();
      System.exit(0);
    }

    // use trim() to avoid the extra whitespace typo
    int size = Integer.parseInt(reader.nextLine().trim());
    ResidualGraph residualGraph = new ResidualGraph(size);
    for (int i = 0; i < size; i++) {
      residualGraph.addNode(reader.nextLine().trim(), i + 1);
    }

    // skip one line
    size = Integer.parseInt(reader.nextLine().trim());
    for (int i = 0; i < size; i++) {
      // add extra security if typo exists in the file
      if (!reader.hasNextInt())
        break;
      int from = reader.nextInt();
      int to = reader.nextInt();
      residualGraph.addEdge(from, to);
    }
    // close scanner
    reader.close();
    residualGraph.connectSourceAndSink();
    return residualGraph;
  }

  /**
   * Runs the Dinitz algorithm on the residual graph in phases. In each phase,
   * level graph is created using the residual graph containing paths from
   * source to sink.
   * The algorithm terminates when no existing path from source to sink in the
   * residual graph.
   *
   * @param residualGraph : The residual graph generated by the input file.
   * @pre : The residual graph is already constructed
   * @post : The result is printed
   */
  private static void run(ResidualGraph residualGraph) {

    // Iterating over different phases until there exists a path
    // from source to sink
    while (true) {
      // Construct level graph by running BFS on the residual graph
      LevelGraph levelGraph = residualGraph.createLevelGraph();

      // no path exists, break and print matching
      if (levelGraph == null) {
        break;
      }

      // Initialize location to source node and path to an empty list.
      int currentLocation = residualGraph.getSource();
      LinkedList<Integer> path = new LinkedList<>();

      // Recursively traversing on current level graph until
      // there exists a path from source to sink.
      traverse(currentLocation, path, residualGraph, levelGraph);
    }

    // Printing bipartite matching result
    residualGraph.printMatching();

  }

  /**
   * Recursively to find path from source to sink in the level graph.
   * Once a path a found, augment the flow and update level & residual graph.
   * If the current node is not sink, advance along the level graph if there
   * exists a node ahead.
   * Retreats when stuck by deleting nodes from the level graph
   *
   * @param currentNode   : Current node in the recursive traversal
   * @param currentPath   : A linkedlist to represent current path
   * @param residualGraph : The current state of the residual graph
   * @param levelGraph    : The current state of the level graph
   * @pre : Both the residual graph and the level graph are constructed.
   * @post : The flow is augmented and matching is added/updated
   */
  private static void traverse(int currentNode,
      LinkedList<Integer> currentPath,
      ResidualGraph residualGraph,
      LevelGraph levelGraph) {

    // Assign source and sink
    int source = residualGraph.getSource();
    int sink = residualGraph.getSink();

    // Push current node to path
    currentPath.add(currentNode);

    // if current location is sink
    if (currentNode == sink) {
      // Augment flow with path and update the matching
      residualGraph.augmentPath(currentPath);

      // Update residual graph by reversing the connections
      residualGraph.reverseConnectionInPath(currentPath);

      // Delete edges from level graph in the path
      levelGraph.deleteEdgesOnAPath(currentPath);

      // Set location to source and clear the path
      traverse(source, new LinkedList<>(), residualGraph, levelGraph);
    } else { // current location is not sink

      // Get neighbors from the current node
      ArrayList<Integer> neighbors = levelGraph.getNeighbors(currentNode);

      // If stuck on source, break
      if (currentNode == source && neighbors.isEmpty()) {
        return;
        // Retreating, when the traversal is stuck at a node
      } else if (neighbors.isEmpty()) {
        // Delete current node and incoming edges
        levelGraph.deleteNode(currentNode);

        // Delete the last node
        currentPath.removeLast();

        // backtrack
        int previousNode = currentPath.removeLast();
        traverse(previousNode, currentPath, residualGraph, levelGraph);
      } else { // Advance along the first neighbor in level graph
        traverse(neighbors.get(0), currentPath,
            residualGraph, levelGraph);
      }
    }
  }
}