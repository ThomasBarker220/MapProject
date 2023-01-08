import java.security.InvalidAlgorithmParameterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 *
 */
public class GraphProcessor {

    private Map<Point, Double> distance = new HashMap<Point, Double>();
    private Map<Point, HashSet<Point>> adjList = new HashMap<Point, HashSet<Point>>();
    private HashSet<Point> visited = new HashSet<Point>();
    private Map<Point, Point> previous = new HashMap<Point, Point>();
    private List<Point> fullGraph = new ArrayList<Point>();
    
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */

    
    public void initialize(FileInputStream file) throws Exception {
        // TODO: Implement initialize
        // visited = new HashSet<Point>();
        // adjList = new HashMap<Point, HashSet<Point>>();
        // fullGraph = new ArrayList<Point>();

        Scanner read = new Scanner(file);
        Integer numVertices = read.nextInt();
        Integer numEdges = read.nextInt();

        read.nextLine();

        for(int i = 0; i < numVertices; i++) {
            read.next();
            Double latitude = read.nextDouble();
            Double longitude = read.nextDouble();
            Point vertex = new Point(latitude, longitude);
            fullGraph.add(vertex);

            adjList.putIfAbsent(vertex, new HashSet<>());

            read.nextLine();
        }

        for(int i = 0; i < numEdges; i ++) {
            Integer beg = read.nextInt();
            Integer end = read.nextInt();
            adjList.get(fullGraph.get(beg)).add(fullGraph.get(end));
            adjList.get(fullGraph.get(end)).add(fullGraph.get(beg));

            if(read.hasNextLine()) read.nextLine();
        }
        read.close();

    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        // TODO: Implement nearestPoint
        Point closest = fullGraph.get(0);
        double smallestDistance = p.distance(closest);
        for(int i = 0; i < fullGraph.size(); i++) {
            if(p.distance(fullGraph.get(i)) < smallestDistance) {
                closest = fullGraph.get(i);
                smallestDistance = p.distance(closest);
            }
        }
        return closest;
    }


    private void DFS(Point start) {
        Stack<Point> toExplore = new Stack<>();
        Point current = start;
        toExplore.add(current);
        visited.add(current);

        while(!toExplore.isEmpty()) {
            current = toExplore.pop();
            for(Point neighbor : adjList.get(current)) {
                if(!visited.contains(neighbor)) {
                    previous.put(neighbor, current);
                    visited.add(neighbor);
                    toExplore.push(neighbor);
                }
            }
        }
    }

    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        // TODO Implement routeDistance
        double total = 0.0;
        for(int i = 0; i < route.size() - 1; i++) {
            total += route.get(i).distance(route.get(i+1));
        }
        return total;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        visited.clear();
        DFS(p1);
        if(visited.contains(p2)) return true;
        return false;
    }

    private void Dijkstra(Point start, Point end) {
        visited.clear();
        distance.clear();
        Comparator<Point> comp = (a, b) -> distance.get(a).compareTo(distance.get(b));
        PriorityQueue<Point> toExplore = new PriorityQueue<>(comp);
        Point current = start;
        distance.put(current, 0.0);
        toExplore.add(current);

        while(!toExplore.isEmpty()) {
            current = toExplore.remove();
            visited.add(current);
            if(current.equals(end)) {
                return;
            }
            for(Point neighbor : adjList.get(current)) {
                if(visited.contains(neighbor)) {
                    continue;
                }
                Double weight = neighbor.distance(current);
                if(!distance.containsKey(neighbor) || distance.get(neighbor) > distance.get(current) + weight) {
                    distance.put(neighbor, distance.get(current) + weight);
                    previous.put(neighbor, current);
                    toExplore.add(neighbor);
                }

            }
        }
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        if(!connected(start, end)) {
            throw new InvalidAlgorithmParameterException("No path between start and end");
        }
        Dijkstra(start, end);
        Point search = end;
        List<Point> ret = new LinkedList();
        ret.add(search);
        while(!search.equals(start)) {
            if(previous.containsKey(search)) {
                ret.add(0, previous.get(search));
                search = previous.get(search);
            }
        }
        return ret;
    }
    
}
