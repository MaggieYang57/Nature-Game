
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.*;

public class AStarPathingStrategy implements PathingStrategy{



    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
    	
    	List<Node> openList = new LinkedList<>();
        List<Node> closedList = new LinkedList<>();
        List<Point> path = new LinkedList<>();

        // currentNode begins at starting point
        Node currentNode = new Node(null, 0, start.distanceSquared(end), start);
        openList.add(currentNode);

        // stop when target is in reach
        while (!withinReach.test(currentNode.getPosition(), end)) {

            // get all positions nearby that are WITHIN BOUNDS and are not occupied by something
            List<Point> adjacentPositions = potentialNeighbors.apply(currentNode.getPosition())
                    .filter(canPassThrough)
                    .collect(Collectors.toList());

            List<Node> children = new LinkedList<>();

            // Turn adjacent points into nodes so long as they are not in
            // the closedList (nodes that are already explored)
            for (Point pos : adjacentPositions) {
                Node newChildNode = new Node(currentNode, currentNode.getG() + 1, pos.distanceSquared(end), pos);
                boolean inClosedList = false;
                for (Node closedNode : closedList) {
                    if (closedNode.equals(newChildNode)) {
                        inClosedList = true;
                    }
                }
                if (!inClosedList) {
                    children.add(newChildNode);
                }
            }

            // Add child node to openList if it's not in already.
            for (Node child : children) {
                boolean inOpenList = false;
                for (Node openNode : openList) {
                    if (openNode.equals(child)) {
                        inOpenList = true;
                    }
                }
                if (!inOpenList) {
                    openList.add(child);
                }
                // Now, if a a node with the same position exists in openList but the childNode
                // is a more efficient node, replace the node with the better one
                for (Node openNode : openList) {
                    if (openNode.getG() > child.getG() && openNode.getPosition().equals(child.getPosition())) {
                        openNode.setG(child.getG());
                        openNode.setH(child.getH());
                        openNode.setF(child.getG() + child.getH());
                        openNode.setParent(child.getParent());
                    }
                }
            }
            // add currentNode to closedList (fully explored) and pop currentNode from openList
            closedList.add(currentNode);
            openList.remove(currentNode);

            // If openList is not empty, reorganize it such that the node with the smallest F is at index 0
            if (!openList.isEmpty()) {
                openList = openList.stream()
                        //f = g + h, where g is distance between current node and start node and
                        //h is the distance from current node to end node
                        //Comparator.comparingDouble((point) -> point.distanceSquared(start) + point.distanceSquared(end))
                        .sorted(Comparator.comparing(Node::getF))
                        .collect(Collectors.toList());

                currentNode = openList.get(0);
            }
            // if it's empty,
            else {
                return path;
            }
        }
        // we are adjacent to the target. Build path (always insert at 0 to avoid reversing)
        // from trail of node parents
        while (currentNode.getParent() != null) {
            path.add(0, currentNode.getPosition());
            currentNode = currentNode.getParent();
        }

        // truncate the list so that there's only one point (the next point)
        path = path.stream()
                .limit(1)
                .collect(Collectors.toList());
        return path;
    
    }
}
