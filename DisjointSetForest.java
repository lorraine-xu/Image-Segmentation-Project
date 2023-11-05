import java.util.*;

/** Creates and manages DisjointedSetForest and stores it in a 2D Array
 * 
 * Time spent: 7 hours
 * 
 * @author Lorraine Xu
 * @author Belle Ange Itetere
 */
public class DisjointSetForest {
    public Node[][] nodes;

    /**
	 * Wrapper class for a Pixel that stores a parent pointer as well as rank,
	 * weight and segment size.
	 */
    public class Node {
        public Pixel pixel;

        public Node parent;
        public int rank;
        public int size;
        public double iDistance;

        /**
		 * Initializes a Node based on an input Pixel.
		 * 
		 * @param pixel Pixel to be contained in a Node
		 */
        public Node(Pixel pixel){
            this.pixel = pixel;

            parent = null;
            this.rank = 0;
            this.size = 1;
            this.iDistance = 0.0;
        }
    }

    /**
	 * Initializes the Disjointed Set Forest by assigning each node 
     * into its pixel's equivalent position in the 2D Array.
	 * 
	 * @param pixels Pixel[][] containing the image data
	 */
    public DisjointSetForest(Pixel[][] pixels){
        nodes = new Node[pixels.length][pixels[0].length];
        
        for (int i = 0; i < pixels.length; i++){
            for (int j = 0; j < pixels[0].length; j++){
                nodes[i][j] = new Node(pixels[i][j]);
            }
        }
    }

    /**
	 * Recursively searches through a node's parents until it finds the 'root' and
	 * returns it.
	 * 
	 * @param currentNode Node to be found within the DisjointedSetForest
	 * @return Representative or top Node in a section of Nodes
	 */
    public Node find(Node currentNode){
        if (currentNode.parent == null) {
			return currentNode;
		} else {
			// Updates parent recursively to assure O(1) runtime for future find calls
			currentNode.parent = find(currentNode.parent);
			return currentNode.parent;
		}
    }

    /**
	 * Creates a connection between two Nodes, connecting the Node with lower rank
	 * to the Node with higher rank.
	 * 
	 * @param node1  Node to be joined with another
	 * @param node2 Node to be joined with another
	 */
    public void union(Node node1, Node node2){
        Node firstRoot = find(node1);
		Node secondRoot = find(node2);
		Edge edge = new Edge(node1.pixel, node2.pixel);

		// If both initial Nodes have equal rank, the new 'top' Node's rank will
		// increase by 1. Else, the ranks will remain the same
		if (firstRoot.rank >= secondRoot.rank) {
			if (firstRoot.rank == secondRoot.rank)
				firstRoot.rank++;

			secondRoot.parent = firstRoot; // Updates parent pointer
			firstRoot.size += secondRoot.size; // Updates size
			secondRoot.size = firstRoot.size;
			firstRoot.iDistance = edge.getWeight(); // Updates internal distance
		}
		// If the second Node has a higher rank, the process is symmetrical
		else {
			firstRoot.parent = secondRoot;
			secondRoot.size += firstRoot.size;
			firstRoot.size = secondRoot.size;
			secondRoot.iDistance = edge.getWeight();
		}
    }
}
