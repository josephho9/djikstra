// === CS400 File Header Information ===
// Name: Joseph Ho
// Email: jho29@wisc.edu
// Group and Team: <your group name: two letters, and team color>
// Group TA: <name of your group's ta>
// Lecturer: Gary Dahl
// Notes to Grader: <optional extra notes>

import java.util.PriorityQueue;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * This class extends the BaseGraph data structure with additional methods for computing the total
 * cost and list of node data along the shortest path connecting a provided starting to ending
 * nodes. This class makes use of Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number> extends BaseGraph<NodeType, EdgeType>
    implements GraphADT<NodeType, EdgeType> {

  /**
   * While searching for the shortest path between two nodes, a SearchNode contains data about one
   * specific path between the start node and another node in the graph. The final node in this path
   * is stored in its node field. The total cost of this path is stored in its cost field. And the
   * predecessor SearchNode within this path is referenced by the predecessor field (this field is
   * null within the SearchNode containing the starting node in its node field).
   *
   * SearchNodes are Comparable and are sorted by cost so that the lowest cost SearchNode has the
   * highest priority within a java.util.PriorityQueue.
   */
  protected class SearchNode implements Comparable<SearchNode> {
    public Node node;
    public double cost;
    public SearchNode predecessor;

    public SearchNode(Node node, double cost, SearchNode predecessor) {
      this.node = node;
      this.cost = cost;
      this.predecessor = predecessor;
    }

    public int compareTo(SearchNode other) {
      if (cost > other.cost)
        return +1;
      if (cost < other.cost)
        return -1;
      return 0;
    }
  }

  /**
   * Constructor that sets the map that the graph uses.
   */
  public DijkstraGraph() {
    super(new PlaceholderMap<>());
  }

  /**
   * This helper method creates a network of SearchNodes while computing the shortest path between
   * the provided start and end locations. The SearchNode that is returned by this method is
   * represents the end of the shortest path that is found: it's cost is the cost of that shortest
   * path, and the nodes linked together through predecessor references represent all of the nodes
   * along that shortest path (ordered from end to start).
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return SearchNode for the final end node within the shortest path
   * @throws NoSuchElementException when no path from start to end is found or when either start or
   *                                end data do not correspond to a graph node
   */
  protected SearchNode computeShortestPath(NodeType start, NodeType end) {
    // implement in step 5.3
    MapADT<Node, Node> visited = new PlaceholderMap();
    PriorityQueue<SearchNode> pq = new PriorityQueue<>();
    Node s = this.nodes.get(start); //Instantiate start as a Node
    Node e = this.nodes.get(end);//Instantiate start as a Node
    pq.add(new SearchNode(s, 0.0, null)); //add the start node to the unsettled priority queue
    while (!pq.isEmpty()) {
      SearchNode current = pq.poll(); //take the lowest cost edge out of the priority queue

      if (current.node.equals(e)) { //reached the end
        return current;
      }

      if (!visited.containsKey(current.node)) { //only search if we have not visited that node yet
        for (Edge edge : current.node.edgesLeaving) { //check all edges in the current node
          SearchNode x =
              new SearchNode(edge.successor, current.cost + edge.data.doubleValue(), current);
          pq.add(x); //add it to the priority queue
        }
        visited.put(current.node, current.node); //mark the node as visited
      }
    }


    throw new NoSuchElementException("No path exists from start to end");



  }

  /**
   * Returns the list of data values from nodes along the shortest path from the node with the
   * provided start value through the node with the provided end value. This list of data values
   * starts with the start value, ends with the end value, and contains intermediary values in the
   * order they are encountered while traversing this shorteset path. This method uses Dijkstra's
   * shortest path algorithm to find this solution.
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return list of data item from node along this shortest path
   */
  public List<NodeType> shortestPathData(NodeType start, NodeType end) {
    List<NodeType> path = new ArrayList<>();
    try {
      // Get the SearchNode for the end node in the shortest path
      SearchNode endNode = computeShortestPath(start, end);

      // Traverse backwards from the end node to the start node
      for (SearchNode current = endNode; current != null; current = current.predecessor) {
        path.add((NodeType) current.node.data); 
      }

      // The path is collected in reverse order, from end to start, so it needs to be reversed
      Collections.reverse(path);

      return path;
      
    } catch (NoSuchElementException e) {
      // Handle the case where no path exists or start/end nodes are not in the graph
      System.out.println(e.getMessage());
      return path; // returns an empty path if no path is found or an exception is thrown
    }
  }

  /**
   * Returns the cost of the path (sum over edge weights) of the shortest path freom the node
   * containing the start data to the node containing the end data. This method uses Dijkstra's
   * shortest path algorithm to find this solution.
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return the cost of the shortest path between these nodes
   */
  public double shortestPathCost(NodeType start, NodeType end) {
    SearchNode s = computeShortestPath(start, end);

    return s.cost;
  }

  // TODO: implement 3+ tests in step 4.1
  @Test
  public static void testExample() {
    DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
    graph.insertNode("A");
    graph.insertNode("B");
    graph.insertNode("C");
    graph.insertNode("D");
    graph.insertNode("E");
    graph.insertEdge("A", "C", 1);
    graph.insertEdge("A", "D", 4);
    graph.insertEdge("A", "B", 15);
    graph.insertEdge("C", "E", 10);
    graph.insertEdge("B", "E", 1);
    graph.insertEdge("D", "B", 2);
    graph.insertEdge("D", "E", 10);

    double cost = graph.shortestPathCost("A", "E");
    assertEquals(7, cost);
    List<String> x = graph.shortestPathData("A", "E");
    List<String> expected = new ArrayList<>() {
      {
        add("A");
        add("D");
        add("B");
        add("E");
      }
    };
    assertEquals(expected, x);
  }

  @Test
  public static void testExampleDifferentStartEnd() {
    DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
    graph.insertNode("A");
    graph.insertNode("B");
    graph.insertNode("C");
    graph.insertNode("D");
    graph.insertNode("E");
    graph.insertEdge("A", "C", 1);
    graph.insertEdge("A", "D", 4);
    graph.insertEdge("A", "B", 15);
    graph.insertEdge("C", "E", 10);
    graph.insertEdge("B", "E", 1);
    graph.insertEdge("D", "B", 2);
    graph.insertEdge("D", "E", 10);

    double cost = graph.shortestPathCost("D", "E");
    assertEquals(3, cost);
    List<String> x = graph.shortestPathData("D", "E");

    List<String> expected = new ArrayList<>() {
      {
        add("D");
        add("B");
        add("E");
      }
    };
    assertEquals(x, expected);
  }
  
  // TODO: implement 3+ tests in step 4.1
  @Test
  public static void lectureExample1() {
      // Simple example from lecture
      DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();

      // 5 nodes
      graph.insertNode("A");
      graph.insertNode("B");
      graph.insertNode("C");
      graph.insertNode("D");
      graph.insertNode("E");
      // 14 edges to make an undirected graph
      graph.insertEdge("A", "B", 15);
      graph.insertEdge("A", "C", 1);
      graph.insertEdge("A", "D", 4);
      graph.insertEdge("B", "A", 15);
      graph.insertEdge("B", "D", 2);
      graph.insertEdge("B", "E", 1);
      graph.insertEdge("C", "A", 1);
      graph.insertEdge("C", "E", 10);
      graph.insertEdge("D", "A", 4);
      graph.insertEdge("D", "B", 2);
      graph.insertEdge("D", "E", 10);
      graph.insertEdge("E", "B", 1);
      graph.insertEdge("E", "C", 10);
      graph.insertEdge("E", "D", 10);

      // Check the cost and the sequence
      Assertions.assertEquals(7, graph.shortestPathCost("A", "E"));
      Assertions.assertEquals("[A, D, B, E]", graph.computeShortestPath("A", "E"));
  } 

  @Test
  public static void testNoDirect() {
    DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
    graph.insertNode("A");
    graph.insertNode("B");
    graph.insertNode("C");
    graph.insertNode("D");
    graph.insertNode("E");

    graph.insertEdge("A", "B", 1);
    graph.insertEdge("B", "C", 4);
    graph.insertEdge("C", "D", 15);

    assertThrows(NoSuchElementException.class, () -> graph.computeShortestPath("A", "E"),
        "Expected NoSuchElementException when no path exists from A to E.");
  }


  

  public static void main(String[] args) {
    testExample();
    testExampleDifferentStartEnd();
    testNoDirect();
  }
}
