import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.Random;
import java.util.Scanner;
import java.util.function.ToDoubleBiFunction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//needed for exporting file
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
		
		/**
		 * 
		 * @param node the current Node in question
		 * @return true if the node is traversable
		 */
		public static boolean isTraversable(Node node)
		{
			return (node.type == '1'|| node.type == '2'|| node.type == 'a'|| node.type == 'b')?true:false;
		}
		
		//uses Manhattan distance to generate heuristic
		//TODO: change to Manhattan
		public static double euclidDist(Node start, Node end)
		{
			return Math.sqrt(((double)Math.pow(start.y-end.y,2))+Math.pow(start.x-end.x, 2))/4;
		}
		/**
		 * gets a distance based on a start and end node, and returns the minimum possible distance
		 * @param start
		 * @param end
		 * @return
		 */
		public static double manhattanDist(Node start, Node end)
		{
			int xdist = Math.abs(start.x-end.x);
			int ydist = Math.abs(start.y-end.y);
			
			return (xdist+ydist)/4 + ((.25-8) * Math.min(xdist, ydist));
		}
		private static boolean isnDiagonal(Node start, Node end)
		{
			return ((start.x==end.x&&!(start.y==end.y))||((!(start.x==end.x)&&start.y==end.y))) ? true: false;
		}
		public static <T> boolean isInBounds(ArrayList<ArrayList<T>> grid, int x, int y)
		{
			return (y < grid.size()&& x < grid.get(y).size())? true : false;
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
		/**
		 * Updates the Node fptr with new distance, eCost, and parent values depending on the given heuristic  
		 * @param fringe
		 * @param current the current node
		 * @param fptr a neighbor of a node being searched
		 * @param end The goal node of a search
		 * @param heuristic A function which calculates an estimated distance from fptr to end and returns a double 
		 * @return
		 */
		private static boolean updateNode(PriorityQueue<Node> fringe, Node current, Node fptr, Node end, ToDoubleBiFunction<Node, Node> heuristic)
		{
			if(current.distance + getCost(current, fptr) < fptr.distance)
			{
				double estimatedDistance = 0;
				fptr.distance = current.distance + getCost(current, fptr);
				if(heuristic!=null)
				{
					estimatedDistance = heuristic.applyAsDouble(fptr, end);
				}
				fptr.eCost = fptr.distance+estimatedDistance;
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
			@param grid An ArrayList of Cells that will be traversed.
			@param start The start of the traversal.
			@param end The end Cell needed to traverse to.
			@see Cell
		*/
		public static ArrayList<Node> astar(Node start, Node end, ToDoubleBiFunction<Node, Node> heuristic )
		{
			if(start.equals(end))
			{
				return null;
			}
			if(!isTraversable(start))
			{
				return null;
			}
			Node ptr = start, fptr;//current and fringe pointer nodes
			ptr.parent=ptr;
			//TODO: comparator for fringe?
			PriorityQueue<Node> fringe = new PriorityQueue<Node>(8);//up to 8 neighbors around starting node
			ArrayList<Node> visited = new ArrayList<Node>();
			fringe.add(ptr);
			int num = 1;
			while(!fringe.isEmpty())
			{
				ptr = fringe.poll();
				num++;
				if(ptr.equals(end))
				{
					ArrayList<Node> res = new ArrayList<Node>();
					for(;ptr!=start; ptr = ptr.parent)//return the path back to start
					{
						res.add(ptr);
					}
					res.add(ptr);
					System.out.println(num);
					return res;
				}
				visited.add(ptr);
				for(int i = 0; i<ptr.neighbors.size();i++)//getting neighbors from current node
				{
					fptr = ptr.neighbors.get(i);//current neighbor of graph
					if(isTraversable(fptr)&&!visited.contains(fptr))
					{
						if(!fringe.contains(fptr))//add to the fringe and set to infinity
						{
							//May require an if valid node check
							fptr.distance = Double.POSITIVE_INFINITY;
							fringe.add(fptr);
						}
						updateNode(fringe, ptr, fptr, end, (Node a, Node b)->heuristic.applyAsDouble(a, b));
					}
				}
			}
			System.out.println(num);
			return null;
			//return path
			
		}
	}
	

	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Grid");
		
		double cellSize = 12;
		int row = 120;
		int col = 160;
		Canvas grid = new Canvas(cellSize*col+col, cellSize*row+row);
		GridPane gridPane = new GridPane();
		
		ScrollPane spane = new ScrollPane();
		Scene scene = new Scene(gridPane, 800, 600);
		GraphicsContext gContext = grid.getGraphicsContext2D();
		
		spane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		//pixel size of cell

		gridPane.add(spane, 0, 0);
		spane.setContent(grid);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		gridPane.setHgap(5);
		gridPane.setPadding(new Insets(10,10,10,10));
		
		Grid world = new Grid();
		world.createGrid(120, 160);
		
		//text fields
		TextField xStart = new TextField("xStart"),
				yStart = new TextField("yStart"),
				xEnd = new TextField("xEnd"),
				yEnd = new TextField("yEnd");
		
		//Buttons
		Button randomize = new Button("Randomize"),
				//reset = new Button("Reset"),
				euclideanSearchPath = new Button("Euclidean Search"),
				manhattanSearchPath = new Button("Manhattan Search"),
				importFile = new Button("Import Map"),
				exportFile = new Button("Export Map");
		
		//Button actions 
		randomize.setOnAction((e)->
		{
			world.createGrid(120, 160);
			world.drawBoard(gContext, world.Grid, cellSize, row, col);
			drawPath(gContext,
					search.astar(world.Grid.get(0).get(0),
							world.Grid.get(119).get(159),
							(Node a, Node b)->{return search.euclidDist(a, b);}),
					cellSize);
		});
		euclideanSearchPath.setOnAction((e)->
		{
			int xS = 0, yS = 0, xE = 0, yE = 0;
			world.drawBoard(gContext, world.Grid, cellSize, row, col);
			xS = Integer.parseInt(xStart.getText());
			yS = Integer.parseInt(yStart.getText());
			xE = Integer.parseInt(xEnd.getText());
			yE = Integer.parseInt(yEnd.getText());
			
			if(search.isInBounds(world.Grid, xS, yS)&&search.isInBounds(world.Grid, xE, yE))
			{
				drawPath(gContext,
						search.astar(world.Grid.get(yS).get(xS),
								world.Grid.get(yE).get(xE),
								(Node a, Node b)->{return search.euclidDist(a, b);}),
						cellSize);
			}
			
		});
		manhattanSearchPath.setOnAction((e)->
		{
			int xS = 0, yS = 0, xE = 0, yE = 0;
			world.drawBoard(gContext, world.Grid, cellSize, row, col);
			xS = Integer.parseInt(xStart.getText());
			yS = Integer.parseInt(yStart.getText());
			xE = Integer.parseInt(xEnd.getText());
			yE = Integer.parseInt(yEnd.getText());
			
			if(search.isInBounds(world.Grid, xS, yS)&&search.isInBounds(world.Grid, xE, yE))
			{
				drawPath(gContext,
						search.astar(world.Grid.get(yS).get(xS),
								world.Grid.get(yE).get(xE),
								(Node a, Node b)->{return search.manhattanDist(a, b);}),
						cellSize);
			}
			
		}
		);
		importFile.setOnAction(e ->
		{
			FileChooser fc = new FileChooser();
			fc.setTitle("Import File");
			File file = fc.showOpenDialog(primaryStage);
			if(file!=null)
			{
				world.createGrid(file);
				world.drawBoard(gContext, world.Grid, cellSize, row, col);
				drawPath(gContext,
						search.astar(world.Grid.get(0).get(0),
								world.Grid.get(119).get(159),
								(Node a, Node b)->{return search.euclidDist(a, b);}),
						cellSize);
			}
		}
		);
		exportFile.setOnAction(e ->
		{
			FileChooser fc = new FileChooser();
			fc.setTitle("Import File");
			FileWriter file;
			try {
				file = new FileWriter(fc.showSaveDialog(primaryStage));
				
				if(file != null)
				{
					for(int y = 0; y<world.Grid.size(); y++)
					{
						for(int x = 0; x<world.Grid.get(y).size(); x++)
						{
							file.write(world.Grid.get(y).get(x).type);
						}
						file.write('\n');
						file.flush();
					}
				}
				file.close();
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			
		}
		);
		//stacking layouts
		VBox panel = new VBox(randomize, importFile, exportFile, euclideanSearchPath, manhattanSearchPath, xStart, yStart, xEnd, yEnd);
		gridPane.add(panel, 1, 0);
		panel.setMinSize(100, 100);
		ArrayList<Node> path = search.astar(world.Grid.get(0).get(0),world.Grid.get(119).get(159), (Node a, Node b)->{return search.euclidDist(a, b);});
		drawBoard(gContext, world.Grid, cellSize, row, col);
		drawPath(gContext, path, cellSize);
		
		System.out.println("Memory Usage: " + (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000 + " MB");
	}

	
	public void setNeighbors(ArrayList<ArrayList<Node>> grid, Node cell){
		
		int x = cell.x;
		int y = cell.y;
		
		//cells on left
		if (x != 0)
		{
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
		if (x !=grid.get(y).size()-1)
		{
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
	
	public void drawPath(GraphicsContext grid, ArrayList<Node> path, double cellSize)
	{
		Node ptr = null, neighbor = null;
		grid.setStroke(Color.BLUEVIOLET);
		grid.setLineWidth(3);
		if(path==null)
		{
			return ;
		}
		for(int i = path.size()-1; i > 0; i--)
		{
			ptr = path.get(i);
			neighbor = path.get(i-1);
			grid.strokeLine(ptr.x*cellSize+cellSize/2+ptr.x, ptr.y*cellSize+cellSize/2+ptr.y, neighbor.x*cellSize+cellSize/2+neighbor.x, neighbor.y*cellSize+cellSize/2+neighbor.y);
		}
	}
	public void drawHighways(GraphicsContext grid, ArrayList<ArrayList<Node>> graph, double cellSize)
	{
		Node ptr = null, neighbor = null;
		grid.setStroke(Color.MAROON);
		grid.setLineWidth(7);
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
						grid.strokeLine(ptr.x*cellSize+cellSize/2 + ptr.x, ptr.y*cellSize+cellSize/2+ptr.y, neighbor.x*cellSize+cellSize/2+neighbor.x, neighbor.y*cellSize+cellSize/2+neighbor.y);
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
					if(y <= 0)//&& highway.get(highway.size()-1).equals(graph.get(1).get(x)))
					{
						isEnd = true;
						break;
					}
					if(graph.get(y).get(x).type != 'a' && graph.get(y).get(x).type != 'b' && !highway.contains(graph.get(y).get(x)))
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
					if(y >= graph.size()-1)//&& highway.get(highway.size()-1).equals(graph.get(graph.size()-2).get(x)))
					{
						isEnd = true;
						break;
					}
					if(graph.get(y).get(x).type != 'a' && graph.get(y).get(x).type != 'b' && !highway.contains(graph.get(y).get(x)))
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
					if(x <= 0)//&& highway.get(highway.size()-1).equals(graph.get(y).get(1)))
					{
						isEnd = true;
						break;
					}
					if(graph.get(y).get(x).type != 'a' && graph.get(y).get(x).type != 'b' && !highway.contains(graph.get(y).get(x)))
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
					if(x >= graph.get(y).size()-1)//&& highway.get(highway.size()-1).equals(graph.get(y).get(graph.get(y).size()-2)))
					{
						isEnd = true;
						break;
					}
					if(graph.get(y).get(x).type != 'a' && graph.get(y).get(x).type != 'b' && !highway.contains(graph.get(y).get(x)))
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
				case 'u':
					direction = 'l';
					break;
				case 'd':
					direction = 'r';
					break;
				case 'l':
					direction = 'd';
					break;
				case 'r':
					direction = 'u';
					break;
				default:
					return null;
				}
			}
			else//go clockwise
			{
				switch(direction)
				{
				case 'u':
					direction = 'r';
					break;
				case 'd':
					direction = 'l';
					break;
				case 'l':
					direction = 'u';
					break;
				case 'r':
					direction = 'd';
					break;
				default:return null;
				}
			}
		}
		
		//checking highway validity
		if(isEnd && highway.size()<100)
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
					e.type = (e.type  =='1' || e.type  =='a' )? 'a' : 'b';
				}
			}
		}
	}
	public boolean addBlockedCell(ArrayList<ArrayList<Node>> graph, int x, int y)
	{
		if(graph.get(y).get(x).type == 'a'|| graph.get(y).get(x).type == 'b')
		{
			return false;
		}
		else
		{
			graph.get(y).get(x).type = '0';
			return true;
		}
	}
	public void fillBlockedCells(ArrayList<ArrayList<Node>> graph, int cellnum)
	{
		Random rand = new Random();
		int x = 0, y = 0;
		for(int i = 0;i<cellnum;)
		{
			y = (int) Math.round(rand.nextDouble()*(graph.size()-1));
			x = (int) Math.round(rand.nextDouble()*(graph.get(y).size()-1));
			if(addBlockedCell(graph, x, y))
			{
				
				i++;
			}
		}
	}
	//create grid
	public ArrayList<ArrayList<Node>> createGrid(int height, int width)
	{
		//creates all unblocked cells
		this.Grid = new ArrayList<ArrayList<Node>>();
		for (int row=0; row<height; row++)
		{
			this.Grid.add(new ArrayList<Node>());		
			for (int col=0; col<width; col++)
			{
				this.Grid.get(row).add(new Node('1', col, row));
			}
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
			addHardCellBlock(this.Grid, (int)Math.round(Math.random()*(width-1)),(int)Math.round(Math.random()*(height-1)), 31);
		}
		
		//DO HIGHWAYS
		fillHighways(this.Grid, 4);
		//DO Blocked Cells
		fillBlockedCells(this.Grid, (int)Math.round(this.Grid.size()*this.Grid.get(0).size()*.2));
		return this.Grid;
	}
	public ArrayList<ArrayList<Node>> createGrid(File file)
	{
		//creates all unblocked cells
		this.Grid = new ArrayList<ArrayList<Node>>();
		Scanner scan;
		try
		{
			scan = new Scanner(file);
			String tmp;
			for (int row=0; scan.hasNext(); row++)
			{
				this.Grid.add(new ArrayList<Node>());
				tmp = scan.nextLine();
				for (int col=0; col < tmp.length(); col++)
				{
					this.Grid.get(row).add(new Node(tmp.charAt(col), col, row));
				}
			}
			scan.close();
			//set neighbors
			for (int row=0; row<this.Grid.size(); row++)
			{		
				for (int col=0; col<this.Grid.get(row).size(); col++)
				{
					setNeighbors(this.Grid, this.Grid.get(row).get(col));
				}
			}
			return this.Grid;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}

    public static void main(String[] args)
    {
    	
    	
    	
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
