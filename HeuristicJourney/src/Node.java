import java.util.ArrayList;

public class Node implements Comparable<Node>
{
	public ArrayList<Node> neighbors = null;
	public char type = 0;
	public Node parent = null;
	public int x = 0, y = 0, distance = 0; //x and y are coordinates, distance is used for pathfinding
	public Node()
	{
	}
	@Override
	public int compareTo(Node o) {
		// TODO Auto-generated method stub
		return this.distance-o.distance;
	}
}
