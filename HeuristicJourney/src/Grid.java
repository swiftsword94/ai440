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

import java.io.File;//needed for exporting file
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Grid extends Application {
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
					for(;ptr!=start; ptr = ptr.parent)
					{
						res.add(ptr);
					}
					return res;
				}
				visited.add(ptr);
				for(int i = 0; i<fringe.size();i++)//getting neighbors from current node
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
		
		ArrayList<ArrayList<Node>> graph = new ArrayList<ArrayList<Node>>();
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
        //0 indicates blocked cell
        //1 indicates regular unblocked cell
        //2 indicates hard to traverse cell
        //a indicates regular unblocked cell with highway
        //b indicates hard to traverse cell with a highway
        
        //randomize function for unique cell
        String[] cellTypes = {"0","1", "2", "a", "b"};
        Random rand = new Random();
        
        
        //create grid with unblocked cells
        //create 31x31 hard to traverse cells
        //create river function for cells
        //create blocked cells
        //export map function to text
        

        
        public void exportMap(GridPane grid){
        	
        }


    public static void main(String[] args){
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
