import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;
import java.io.File;
//needed for exporting file
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Grid extends Application{
	ArrayList<ArrayList<Node>> Grid = new ArrayList<ArrayList<Node>>();
	Node cell = new Node();
	public static class search
	{
		
		
		
		public search()
		{
			
		}
		//uses manhattan distance to generate heuristic
		//TODO: change to manhattan
		public static double getHeuristic(Node start, Node end)
		{
			return Math.sqrt(((double)Math.pow(start.y-end.y,2))+Math.pow(start.x-end.x, 2));
		}
		private static boolean isnDiagonal(Node start, Node end)
		{
			return (start.x-end.x==0&&start.y-end.y==0) ? false: true;
		}
		//gets cell to cell traversal cost (could this be inlined?)
		private static double getCost(Node start, Node end)
		{
			switch(start.type)
			{
			case '1':
			{
				switch(end.type)
				{
				case '1':return (isnDiagonal(start, end)) ? 1: Math.sqrt(2);
				case 'a': return (isnDiagonal(start, end)) ? 1: Math.sqrt(2);
				case '2': return (isnDiagonal(start, end)) ? 1.5: (Math.sqrt(8)+Math.sqrt(2))/2;
				case 'b': return (isnDiagonal(start, end)) ? 1.5: (Math.sqrt(8)+Math.sqrt(2))/2;
				default: return 0;
				}
			}
			case '2':
			{
				switch(end.type)
				{
				case '1':return (isnDiagonal(start, end)) ? 1.5: (Math.sqrt(8)+Math.sqrt(2))/2;
				case 'a': return (isnDiagonal(start, end)) ? 1.5: (Math.sqrt(8)+Math.sqrt(2))/2;
				case '2': return (isnDiagonal(start, end)) ? 2: Math.sqrt(8);
				case 'b': return (isnDiagonal(start, end)) ? 2: Math.sqrt(8);
				default: return 0;
				}
			}
			case 'a':
			{
				switch(end.type)
				{
				case '1':return (isnDiagonal(start, end)) ? 1: Math.sqrt(2);
				case 'a': return (isnDiagonal(start, end)) ? .25: Math.sqrt(2);
				case '2': return (isnDiagonal(start, end)) ? 2: (Math.sqrt(8)+Math.sqrt(2))/2;
				case 'b': return (isnDiagonal(start, end)) ? .375: (Math.sqrt(8)+Math.sqrt(2))/2;
				default: return 0;
				}
			}
			case 'b':
			{
				switch(end.type)
				{
				case '1':return (isnDiagonal(start, end)) ? 1.5: (Math.sqrt(8)+Math.sqrt(2))/2;
				case 'a': return (isnDiagonal(start, end)) ? .375: (Math.sqrt(8)+Math.sqrt(2))/2;
				case '2': return (isnDiagonal(start, end)) ? 2: Math.sqrt(8);
				case 'b': return (isnDiagonal(start, end)) ? .5: Math.sqrt(8);
				default: return 0;
				}
			}
			default:
				return 0;//trying to avoid throwing errors (I can make them but for time's sake ill leave it as it is)
			}
		}
		private static boolean updateNode(PriorityQueue<Node> fringe, Node current, Node fptr, Node end)
		{
			if(current.distance + getCost(current, fptr) < fptr.distance)
			{
				fptr.distance = current.distance + getCost(current, fptr);
				fptr.eCost = fptr.distance+search.getHeuristic(fptr, end);
				fptr.parent = current;
				if(fringe.contains(fptr))
				{
					fringe.remove(fptr);
				}
				fringe.add(fptr);
			}
			return false;
		}
		
		
		
		/*
			This Method uses A* to traverse the grid from the start to end. 
			@param grid An arraylist of Cells that will be traversed.
			@param start The start of the traversal.
			@param end The end Cell needed to traverse to.
			@see Cell
		*/
		public static ArrayList<Node> astar(Node start, Node end)
		{
			if(start.equals(end))
			{
				return null;
			}
			
			Node ptr = start, fptr;//current and fringe pointer nodes
			ptr.parent=ptr;
			//TODO: comparator for fringe?
			PriorityQueue<Node> fringe = new PriorityQueue<Node>(8);//up to 8 neighbors around starting node
			ArrayList<Node> visited = new ArrayList<Node>();
			fringe.add(ptr);
			while(!fringe.isEmpty())
			{
				ptr = fringe.poll();
				if(ptr.equals(end))
				{
					ArrayList<Node> res = new ArrayList<Node>();
					for(;ptr!=start; ptr = ptr.parent)//return the path back to start
					{
						res.add(ptr);
					}
					return res;
				}
				visited.add(ptr);
				for(int i = 0; i<ptr.neighbors.size();i++)//getting neighbors from current node
				{
					fptr = ptr.neighbors.get(i);//current neighbor of graph
					if(!visited.contains(fptr))
					{
						if(!fringe.contains(fptr))//add to the fringe and set inf
						{
							fptr.distance = Double.POSITIVE_INFINITY;
							fringe.add(fptr);
						}
						updateNode(fringe, ptr, fptr, end);
					}
				}
			}
			return null;
			//return path
			
		}
	}
	

	public void start(Stage primaryStage) throws Exception{
		primaryStage.setTitle("Grid");
		
		double cellSize = 10;
		int row = 120;
		int col = 160;
		Canvas grid = new Canvas(cellSize*row, cellSize*col);
		GridPane gridPane = new GridPane();
		
		ScrollPane spane = new ScrollPane();
		Scene scene = new Scene(gridPane, 800, 600);
		
		spane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		ArrayList<ArrayList<Node>> graph = Grid;
		GraphicsContext gContext = grid.getGraphicsContext2D();
		//pixel size of cell

		gridPane.add(spane, 0, 0);
		spane.setContent(grid);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		drawBoard(gContext, graph, cellSize, row, col);
		Button redraw = new Button("RD");
		
		gridPane.add(redraw, 1, 0);
		redraw.autosize();
}

	
	public static void setNeighbors(ArrayList<ArrayList<Node>> grid, Node cell){
		
		int x = cell.x;
		int y = cell.y;
		
		//cells on left
		if (x != 0){
			if(y!=0)
			{
				cell.neighbors.add(grid.get(y-1).get(x-1));
			}
			cell.neighbors.add(grid.get(y).get(x-1));
			if(y!=grid.size()-1)
			{
				cell.neighbors.add(grid.get(y+1).get(x-1));
			}
		}
		
		//right
		if (x !=grid.get(y).size()-1){
			if(y!=0)
			{
				cell.neighbors.add(grid.get(y-1).get(x+1));
			}
			cell.neighbors.add(grid.get(y).get(x+1));
			if(y!=grid.size()-1)
			{
				cell.neighbors.add(grid.get(y+1).get(x+1));
			}
		}
		//top
		if(y!=0)
		{
			cell.neighbors.add(grid.get(y-1).get(x));
		}
		
		//bottom
		if(y!=grid.size()-1)
		{
			cell.neighbors.add(grid.get(y+1).get(x));
		}
		
		
		
	}
	public void drawBoard(GraphicsContext grid, ArrayList<ArrayList<Node>> graph, double cellSize, int row, int col)
	{
		grid.fill();
    	makeCellBorders(grid, cellSize+1);
    	drawCells(grid, graph, cellSize);
	}
	
	public void makeCellBorders(GraphicsContext graph, double size)
	{
		double height = graph.getCanvas().getHeight();
		double width = graph.getCanvas().getWidth();
		if (size > height)
		{
			graph.getCanvas().setHeight(size);
		}
		if (size > width)
		{
			graph.getCanvas().setWidth(size);
		}
		graph.setLineWidth(1.0);
		graph.setStroke(Color.BLACK);
		for(double i = 0; i < width; i+=size)//vertical
		{
			graph.moveTo(i, 0);
			graph.strokeLine(i, 0, i, height);
		}
		for(double i = 0; i < height; i+=size)//horizontal
		{
			graph.moveTo(i, 0);
			graph.strokeLine(0, i, width, i);
		}
	}
	
	//
	/*
	 * Draws a single cell on a canvas. This method assumes displayed lines on the grid are 1 pixel wide
	 * @param grid an array
	 */
	public void drawCell(GraphicsContext grid, Node node, double cellSize)
	{
		switch(node.type)
		{
		case '0':
			grid.setFill(Color.BLACK);
			grid.fillRect(node.x*cellSize+node.x, node.y*cellSize+node.y, cellSize, cellSize);
			return;
		case '1':
			grid.setFill(Color.CHARTREUSE);
			grid.fillRect(node.x*cellSize+node.x, node.y*cellSize+node.y, cellSize, cellSize);
			return;
		case '2':
			grid.setFill(Color.DARKGREEN);
			grid.fillRect(node.x*cellSize+node.x, node.y*cellSize+node.y, cellSize, cellSize);
			return;
		case 'a':
			grid.setFill(Color.CHARTREUSE);
			grid.fillRect(node.x*cellSize+node.x, node.y*cellSize+node.y, cellSize, cellSize);
			return;
		case 'b':
			grid.setFill(Color.DARKGREEN);
			grid.fillRect(node.x*cellSize+node.x, node.y*cellSize+node.y, cellSize, cellSize);
			return;
		default:
			return;
		}
	}
	public void drawCells(GraphicsContext grid, ArrayList<ArrayList<Node>> graph, double cellSize)
	{
		for(int x = 0; x < graph.size(); x++)
		{
			for(int y = 0; y < graph.get(x).size(); y++)
			{
				drawCell(grid, graph.get(x).get(y), cellSize);
			}
		}
	}
	public void drawHighways(GraphicsContext grid, ArrayList<ArrayList<Node>> graph, double cellSize)
	{
		Node ptr = null, neighbor = null;
		grid.setStroke(Color.DARKGOLDENROD);
		for(int x = 0; x < graph.size(); x++)
		{
			for(int y = 0; y < graph.get(x).size(); y++)//for every node in the graph
			{
				ptr = graph.get(x).get(y);
				if(ptr.type == 'a'||ptr.type == 'b')//if they are a highway
				{
					for(int i = 0; i<ptr.neighbors.size(); i++)//connect them to their highway neighbors
					{
						neighbor = ptr.neighbors.get(i);
						if(search.isnDiagonal(ptr, neighbor)&&(neighbor.type=='a'||neighbor.type=='b'))
						grid.strokeLine(ptr.x*cellSize+ptr.x, ptr.y*cellSize+ptr.y, neighbor.x*cellSize+neighbor.x, neighbor.y*cellSize+neighbor.y);
					}
				}
			}
		}
	}
	
	
	
	//create grid
	public static ArrayList<ArrayList<Node>> createGrid(){
		
		//1 indicates regular unblocked cell

		ArrayList<ArrayList<Node>> grid = new ArrayList<ArrayList<Node>>();
			
		
		//creates all unblocked cells
		for (int row=0; row<120; row++){
			grid.add(new ArrayList<Node>());		
			for (int col=0; col<160; col++){
				Node cell = new Node('1', col, row);
				grid.get(row).add(cell);
				//populate with neighbors
				//setNeighbors(grid, cell);
				//System.out.print('*');
				
			}
			//System.out.println(row);
		}
		
		//set neighbors
		for (int row=0; row<120; row++){		
			for (int col=0; col<160; col++){
				setNeighbors(grid, grid.get(row).get(col));
			}
			
		}
		
		
		//create 31x31 50% hard cells
		//randomly choose 8 coordinates
		
		
		Random rand = new Random();
		
		int xRand1, xRand2, xRand3, xRand4, xRand5, xRand6, xRand7, xRand8;
		int yRand1, yRand2, yRand3, yRand4, yRand5, yRand6, yRand7, yRand8;
		
		xRand1 = rand.nextInt(grid.get(0).size() - 17) + 17;
		yRand1 = rand.nextInt(grid.size() - 17) + 17;
		System.out.print(xRand1);System.out.println(" , " +yRand1);System.out.println();
		for (int hRow = (yRand1-8); hRow < (yRand1+8) ; hRow++){
			for (int hCol = (xRand1-8); hCol < (xRand1+8); hCol++){
				grid.get(hRow).get(hCol).type = '2';
			}
		}
		
		xRand2 = rand.nextInt(grid.get(0).size() - 17) + 17;
		yRand2 = rand.nextInt(grid.size() - 17) + 17;
		System.out.print(xRand2);System.out.println(" , " +yRand2);System.out.println();
		for (int hRow = (yRand2-8); hRow < (yRand2+8) ; hRow++){
			for (int hCol = (xRand2-8); hCol < (xRand2+8); hCol++){
				grid.get(hRow).get(hCol).type = '2';
			}
		}
		
		xRand3 = rand.nextInt(grid.get(0).size() - 17) + 17;
		yRand3 = rand.nextInt(grid.size() - 17) + 17;
		System.out.print(xRand3);System.out.print(" , " +yRand3);System.out.println();
		for (int hRow = (yRand3-8); hRow < (yRand3+8) ; hRow++){
			for (int hCol = (xRand3-8); hCol < (xRand3+8); hCol++){
				grid.get(hRow).get(hCol).type = '2';
			}
		}
		
		xRand4 = rand.nextInt(grid.get(0).size() - 17) + 17;
		yRand4 = rand.nextInt(grid.size() - 17) + 17;
		System.out.print(xRand4);System.out.print(" , " +yRand4);System.out.println();
		for (int hRow = (yRand4-8); hRow < (yRand4+8) ; hRow++){
			for (int hCol = (xRand4-8); hCol < (xRand4+8); hCol++){
				grid.get(hRow).get(hCol).type = '2';
			}
		}

		xRand5 = rand.nextInt(grid.get(0).size() - 17) + 17;
		yRand5 = rand.nextInt(grid.size() - 17) + 17;
		System.out.print(xRand5);System.out.print(" , " +yRand5);System.out.println();
		for (int hRow = (yRand5-8); hRow < (yRand5+8) ; hRow++){
			for (int hCol = (xRand5-8); hCol < (xRand5+8); hCol++){
				grid.get(hRow).get(hCol).type = '2';
			}
		}
		
		xRand6 = rand.nextInt(grid.get(0).size() - 17) + 17;
		yRand6 = rand.nextInt(grid.size() - 17) + 17;
		System.out.print(xRand6);System.out.print(" , " +yRand6);System.out.println();
		for (int hRow = (yRand6-8); hRow < (yRand6+8) ; hRow++){
			for (int hCol = (xRand6-8); hCol < (xRand6+8); hCol++){
				grid.get(hRow).get(hCol).type = '2';
			}
		}
		
		xRand7 = rand.nextInt(grid.get(0).size() - 17) + 17;
		yRand7 = rand.nextInt(grid.size() - 17) + 17;
		System.out.print(xRand7);System.out.print(" , " +yRand7);System.out.println();
		for (int hRow = (yRand7-8); hRow < (yRand7+8) ; hRow++){
			for (int hCol = (xRand7-8); hCol < (xRand7+8); hCol++){
				grid.get(hRow).get(hCol).type = '2';
			}
		}
		
		xRand8 = rand.nextInt(grid.get(0).size() - 17) + 17;
		yRand8 = rand.nextInt(grid.size() - 17) + 17;
		System.out.print(xRand8);System.out.print(" , " + yRand8);System.out.println();
		for (int hRow = (yRand8-8); hRow < (yRand8+8) ; hRow++){
			for (int hCol = (xRand8-8); hCol < (xRand8+8); hCol++){
				grid.get(hRow).get(hCol).type = '2';
			}
		}
		
				
				
			
					
				
				
				
				//create rivers
				
				//Random rand = new Random();
				//int randomRiverRow = rand.nextInt(120);
				//int randomRiverCol = rand.nextInt(160);
				
				//make array with random cells that are along the edge
				
				
				
				//random function to choose random cell from edge array;
				
				//4 cases for cell depending on its location
				
				//if top, then no traversal top
				
				//if right, then no traversal right
				
				//if bot, then no traversal bot
				
				//if left, then no traversal left
			
				
			
				
				
		return grid;
	}


    public static void main(String[] args){
    	
    	
  
    	Grid world = new Grid();
    	
    	world.Grid = createGrid();
    	
    	ArrayList<Node> test = new ArrayList<Node>();
    	
    	test = search.astar(world.Grid.get(1).get(1), world.Grid.get(100).get(100));
    	
    	launch(args);
    	

    	
    	
        System.out.println("end");
        
        //import
        /*
        String file_name = "testGrid.txt";
        try{
        	ReadFile file = new ReadFile(file_name);
        	String[] aryLines = file.OpenFile();
        	int i;
        	for (int i = 0; i<aryLines.length; i++){
        		System.out.println(aryLines[i]);
        	
        	}
        }
        catch (IOException e){
        	System.out.println("FILE Was not exported");
        	
        }
        */
        
        /*
        File file = new File("gridPaneTest.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) try { writer.close(); } catch (IOException ignore) {}
        }
        System.out.printf("File is located at %s%n", file.getAbsolutePath());
        */
      
    }
}
