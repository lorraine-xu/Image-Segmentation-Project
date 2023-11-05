import java.awt.Color;
import java.util.*;

/** Segments an input Image into similar sections
 * 
 * Time spent: 7 hours
 * 
 * @author Lorraine Xu
 * @author Belle Ange Itetere
 */
public class ImageSegmenter {
    
    /**
	 * Transforms an input image into a set of different colored sections
	 * 
	 * @param rgbArray Color[][] containing an image's data
	 * @param granularity Parameter to determine the size and sensitivity of each
	 *                    section
	 * @return Color[][] of the same dimensions with new RGB values representing
	 *         the sections
	 */
    public static Color[][] segment(Color[][] rgbArray, double granularity) {
        Pixel[][] myPixels = new Pixel[rgbArray.length][rgbArray[0].length]; 
        
        // Transforms Color[][] into to Pixel[][]
        for (int r = 0; r < myPixels.length; r++){
            for (int c = 0; c < myPixels[0].length; c++){
                myPixels[r][c] = new Pixel(r, c, rgbArray[r][c]);
            }
        }

        // Initializes the Disjointed Set Forest that stores and sorts all the Pixels
        DisjointSetForest dsf = new DisjointSetForest(myPixels);

        // Finds and sorts all Edges between adjacent pixels
        SortedSet<Edge> myEdges = createEdges(myPixels);

        // Iterates over all possible edges and determines whether or not two pixels
		// belong to the same segment
        for (Edge currentEdge: myEdges){
            Pixel pixelM = currentEdge.getFirstPixel();
            DisjointSetForest.Node nodeM = dsf.nodes[pixelM.getRow()][pixelM.getCol()];
            Pixel pixelN = currentEdge.getSecondPixel();
            DisjointSetForest.Node nodeN = dsf.nodes[pixelN.getRow()][pixelN.getCol()];

            if (dsf.find(nodeM).pixel != dsf.find(nodeN).pixel){
                DisjointSetForest.Node segmentN = dsf.find(nodeN);
                DisjointSetForest.Node segmentM = dsf.find(nodeM);
                int sizeN = segmentN.size;
                int sizeM = segmentM.size;
                double idN = segmentN.iDistance;
                double idM = segmentM.iDistance;

                if (currentEdge.getWeight() < Math.min(idN + (granularity / sizeN), 
                idM + (granularity / sizeM))){
                    dsf.union(nodeM, nodeN);
                }
            }
        }

        // Generates a map that assigns each segment a specific Color
		Map<DisjointSetForest.Node, Color> colorMap = colorSegments(dsf);

		// Overwrites the original Color[][] with the new values found in the Color Map
		for (int row = 0; row < myPixels.length; row++) {
			for (int col = 0; col < myPixels[0].length; col++) {
				rgbArray[row][col] = colorMap.get(dsf.find(dsf.nodes[row][col]));
			}
		}

        return rgbArray; 
    }

    /**
	 * Creates a Map for each segment in the image, using its representative as
	 * keyand and assigns it a randomly generated Color.
	 * 
	 * @param forest - Disjointed Set Forest containing all segmented Pixels
	 * @return - Map assigning each segment a Color
	 */
	private static Map<DisjointSetForest.Node, Color> colorSegments(DisjointSetForest dsf) {
		ColorPicker colorGenerator = new ColorPicker();
		Map<DisjointSetForest.Node, Color> colorMap = new HashMap<DisjointSetForest.Node, Color>();
		List<DisjointSetForest.Node> segments = new ArrayList<DisjointSetForest.Node>();

		// Adds all unique segments to an ArrayList
		for (int row = 0; row < dsf.nodes.length; row++) {
			for (int col = 0; col < dsf.nodes[0].length; col++) {
				DisjointSetForest.Node current = dsf.find(dsf.nodes[row][col]);
				if (!segments.contains(current)) {
					segments.add(current);
				}
			}
		}

		// Iterates over the ArrayList and assigns each unique segment a Color
		for (int j = 0; j < segments.size(); j++) {
			Color randomColor = colorGenerator.nextColor();
			colorMap.put(segments.get(j), randomColor);
		}

		return colorMap;
	}

    /**
	 * Checks the bottom-left, bottom, bottom-right, and right edges for each pixel
	 * and adds a new Edge into the SortedSet if it's in bounds.
	 * 
	 * @param myPixels Pixel[][] containing an image's data
	 * @return SortedSet containing all the Edges between adjacent Pixels
	 */
    private static SortedSet<Edge> createEdges(Pixel[][] myPixels){
        SortedSet<Edge> myEdges = new TreeSet<Edge>();

        // Checks for all necessary edges so that every edge is accounted for
		for (int row = 0; row < myPixels.length; row++) {
			for (int col = 0; col < myPixels[0].length; col++) {
				Pixel currentPixel = myPixels[row][col];
				if (isInBounds(myPixels.length, myPixels[0].length, row + 1, col)) {
					myEdges.add(new Edge(currentPixel,myPixels[row + 1][col]));
				}
				if (isInBounds(myPixels.length, myPixels[0].length, row, col + 1)) {
					myEdges.add(new Edge(currentPixel, myPixels[row][col + 1]));
				}
				if (isInBounds(myPixels.length, myPixels[0].length, row + 1, col + 1)) {
					myEdges.add(new Edge(currentPixel, myPixels[row + 1][col + 1]));
				}
				if (isInBounds(myPixels.length, myPixels[0].length, row + 1, col - 1)) {
					myEdges.add(new Edge(currentPixel, myPixels[row + 1][col - 1]));
				}
			}
		}

		return myEdges;
	}

	/**
	 * Checks if a Pixel is within the boundaries of the 2D Array
	 * 
	 * @param height Height of the image
	 * @param width Width of the image
	 * @param row y-axis position of the Pixel
	 * @param col x-axis position of the Pixel
	 * @return true if the Pixel is in bounds, false otherwise
	 */
	private static boolean isInBounds(int height, int width, int row, int col) {
		return (row >= 0 && row < height && col >= 0 && col < width);
	}
}
