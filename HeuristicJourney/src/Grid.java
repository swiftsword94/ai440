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
import java.util.List;
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
	

	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Grid");
		
		double cellSize = 5;
		int row = 120;
		int col = 160;
		Canvas grid = new Canvas(cellSize*col, cellSize*row);
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
		
		Grid world = new Grid();
		world.createGrid(120, 160);
		ArrayList<Node> test = new ArrayList<Node>();
		
		drawBoard(gContext, world.Grid, cellSize, row, col);
		Button redraw = new Button("RD");
		redraw.setOnAction((e)->{ world.createGrid(120,160);world.drawBoard(gContext, world.Grid, cellSize, row, col);});
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
		grid.setFill(Color.WHITE);
		grid.fill();
    	makeCellBorders(grid, cellSize+1);
    	drawCells(grid, graph, cellSize);
    	drawHighways(grid, graph, cellSize);
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
	
	/**
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
		
		for(int y = 0; y < graph.size(); y++)
		{
			for(int x = 0; x < graph.get(y).size(); x++)
			{
			
				drawCell(grid, graph.get(y).get(x), cellSize);
			}
		}
	}
	public void drawHighways(GraphicsContext grid, ArrayList<ArrayList<Node>> graph, double cellSize)
	{
		Node ptr = null, neighbor = null;
		grid.setStroke(Color.CHOCOLATE);
		for(int y = 0; y < graph.size(); y++)
		{
			for(int x = 0; x < graph.get(y).size(); x++)//for every node in the graph
			{
				ptr = graph.get(y).get(x);
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
	public static void addHardCellBlock(ArrayList<ArrayList<Node>> grid, int xCoord, int yCoord, int size)
	{
		
		Random rand = new Random();
		if(size%2==0)
		{
			size++;
		}
		
		for(int x = xCoord; x < grid.get(yCoord).size()&& (x < xCoord+(size/2+1)); x++)//right
		{
			for(int y = yCoord; y >= 0 && (y > yCoord-(size/2+1)); y--)//righttop+middle
			{
				grid.get(y).get(x).type = (rand.nextBoolean())?'1':'2'; 
			}
			for(int y = yCoord+1; y < grid.size() && (y < yCoord+(size/2+1)); y++)//rightbottom
			{
				grid.get(y).get(x).type = (rand.nextBoolean())?'1':'2'; 
			}
		}
		for(int x = xCoord-1; x >= 0 && (x > xCoord-(size/2+1)); x--)//left
		{
			for(int y = yCoord; y >= 0 && (y > yCoord-(size/2+1)); y--)//lefttop+middle
			{
				grid.get(y).get(x).type = (rand.nextBoolean())?'1':'2'; 
			}
			for(int y = yCoord+1; y < grid.size() && (y < yCoord+(size/2+1)); y++)//leftbottom
			{
				grid.get(y).get(x).type = (rand.nextBoolean())?'1':'2'; 
			}
		}
	}
	
	public static ArrayList<Node> addHighway(ArrayList<ArrayList<Node>> graph, int xCoord, int yCoord, int length)
	{
		int x = xCoord, y = yCoord;
		char direction;
		double dirchoice;
		boolean isEnd = false;
		Random random = new Random();
		ArrayList<Node> highway = new ArrayList<Node>();
		
		//pick direction from start
		if(x == 0)
		{
			if(y == 0)
			{
				direction = (random.nextBoolean())?'r':'d';
			}
			else if(y == graph.size()-1)
			{
				direction = (random.nextBoolean())?'r':'u';
			}
			else
			{
				direction = 'r';
			}
		}
		else if(x == graph.get(y).size()-1)
		{
			if(y == 0)
			{
				direction = (random.nextBoolean())?'l':'d';
			}
			else if(y == graph.size()-1)
			{
				direction = (random.nextBoolean())?'l':'u';
			}
			else
			{
				direction = 'l';
			}
		}
		else
		{
			if(y == 0)
			{
				direction = 'd';
			}
			else if(y == graph.size()-1)
			{
				direction = 'u';
			}
			else
			{
				return null;
			}
		}
		//continue forward
		while(!isEnd)
		{
			switch(direction)//draws lines given a direction
			{
			case 'u':
				for(int i = 0; i < length; i++, y--)
				{
					if(y == 0 && i > 0 && highway.get(highway.size()-1).equals(graph.get(y+1).get(x)))
					{
						isEnd = true;
						break;
					}
					if(graph.get(y).get(x).type != 'a' && graph.get(y).get(x).type != 'b')
					{
						highway.add(graph.get(y).get(x));
					}
					else
					{
						return null;
					}
				}
				break;
			case 'd':
				for(int i = 0; i < length; i++, y++)
				{
					if(y == graph.size()-1 && i > 0 && highway.get(highway.size()-1).equals(graph.get(y-1).get(x)))
					{
						isEnd = true;
						break;
					}
					if(graph.get(y).get(x).type != 'a' && graph.get(y).get(x).type != 'b')
					{
						highway.add(graph.get(y).get(x));
					}
					else
					{
						return null;
					}
				}
				break;
			case 'l':
				for(int i = 0; i < length; i++, x--)
				{
					if(x == 0 && i > 0 && highway.get(highway.size()-1).equals(graph.get(y).get(x+1)))
					{
						isEnd = true;
						break;
					}
					if(graph.get(y).get(x).type != 'a' && graph.get(y).get(x).type != 'b')
					{
						highway.add(graph.get(y).get(x));
					}
					else
					{
						return null;
					}
				}
				break;
			case 'r':
				for(int i = 0; i < length; i++, x++)
				{
					if(x == graph.get(y).size()-1 && i > 0 && highway.get(highway.size()-1).equals(graph.get(y).get(x-1)))
					{
						isEnd = true;
						break;
					}
					if(graph.get(y).get(x).type != 'a' && graph.get(y).get(x).type != 'b')
					{
						highway.add(graph.get(y).get(x));
					}
					else
					{
						return null;
					}
				}
				break;
			default:
				return null;
			}
			//chooses which direction to go
			dirchoice = random.nextDouble();  
			if(dirchoice < .6)
			{
				//go the same direction
			}
			else if(dirchoice < .8)//go counterclockwise
			{
				switch(direction)
				{
				case 'u':direction = 'l';
				case 'd':direction = 'r';
				case 'l':direction = 'd';
				case 'r':direction = 'u';
				default:return null;
				}
			}
			else//go clockwise
			{
				switch(direction)
				{
				case 'u':direction = 'r';
				case 'd':direction = 'l';
				case 'l':direction = 'u';
				case 'r':direction = 'd';
				default:return null;
				}
			}
		}
		
		//checking highway validity
		if(highway.size()<100)
		{
			return null;
		}
		
		return highway;
	}
	/**
	 * Randomly fills a board with highways 
	 * @param graph An ArrayList of ArayList of Nodes represented in the form of a Cartesian plane.
	 * @param nHighways The number of highways to fill the board with.
	 */
	public static void fillHighways(ArrayList<ArrayList<Node>> graph, int nHighways)
	{
		ArrayList<Node> highway = new ArrayList<Node>();
		int x = 0, y = 0;
		Random rand = new Random();
		/*
		 * I need to pick a number that resides on the outside boundaries of the walls randomly
		 * I could simply get all of the edge nodes and choose randomly
		 * I could try to pick a side and then pick a number  
		 */
		//pick a side
		double side;
		
		for(int i = 0; i < nHighways;)
		{
			side = rand.nextDouble();  
			if(side < .25)//top
			{
				y = 0;
				x = (int)Math.round(rand.nextDouble()*(graph.get(y).size()-1));
			}
			else if(side < .50)//bottom
			{
				y = graph.size()-1;
				x = (int)Math.round(rand.nextDouble()*(graph.get(y).size()-1));
			}
			else if(side < .75)//left
			{
				y = (int)Math.round(rand.nextDouble()*(graph.size()-1));
				x = 0;
			}
			else//right
			{
				y = (int)Math.round(rand.nextDouble()*(graph.size()-1));
				x = graph.get(y).size()-1;
			}
			
			highway = addHighway(graph, x, y, 20);
			if(highway != null)
			{
				i++;
				for(Node e : highway)
				{
					e.type = (e.type  =='1')? 'a' : 'b';
				}
				System.out.println(i);
			}
		}
	}
	//create grid
	public ArrayList<ArrayList<Node>> createGrid(int height, int width)
	{
		//creates all unblocked cells
		for (int row=0; row<height; row++)
		{
			this.Grid.add(new ArrayList<Node>());		
			for (int col=0; col<width; col++)
			{
				this.Grid.get(row).add(new Node('1', col, row));
			}
			//System.out.println(row);
		}
		//set neighbors
		for (int row=0; row<height; row++)
		{		
			for (int col=0; col<width; col++)
			{
				setNeighbors(this.Grid, this.Grid.get(row).get(col));
			}
			
		}
		
		for(int i = 0;i < 8;i++)//fills the map with hard cells
		{
			addHardCellBlock(this.Grid, (int)Math.round(Math.random()*(width-1)),(int)Math.round(Math.random()*height-1), 31);
		}
		
		//DO HIGHWAYS
		fillHighways(this.Grid, 4);
		return this.Grid;
	}


    public static void main(String[] args)
    {
    	
    	//test = search.astar(world.Grid.get(1).get(1), world.Grid.get(100).get(100));
    	
    	Application.launch(args);
        
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
