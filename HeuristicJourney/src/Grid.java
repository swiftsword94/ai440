import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Random;
import java.io.File;
//needed for exporting file
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Grid {
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
	
	
	ArrayList<ArrayList<Node>> Grid = new ArrayList<ArrayList<Node>>();
	Node cell = new Node();
	
	
	public static Node setNeighbors(ArrayList<ArrayList<Node>> grid, Node cell){
		
		int x = cell.x;
		int y = cell.y;
		
		
		//cell top left
		if ( ! ( (x-1 == -1) && (y-1 == -1) ) ){
			//cell.neighbors.add(grid.get(x-1).get(y-1));
			//System.out.println(x + y);
			
		}
		
		/*
		//cell top
		if ( ! ( (!(x-1 == -1)) && (y-1 == -1))){
			cell.neighbors.add(grid.get(x).get(y-1));
		}
		

		ScrollPane spane = new ScrollPane();
		spane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		spane.setContent(gridPane);
		
		Scene scene = new Scene(spane, 800, 600);
    	primaryStage.setScene(scene);
    	primaryStage.show();

		
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
				setNeighbors(grid, cell);
				//System.out.print('*');
				
			}
			//System.out.println(row);
		}
		
		
		//create 31x31 50% hard cells
		/*	
		Random rand = new Random();
				
			int hardCells = 0;
			while (hardCells<8){
				
				
		int randomRow = rand.nextInt(89);
		int randomCol = rand.nextInt(129);
				
				//if gridPane.getChild = !hardcell
					for (int hRow = 0; hRow<31; hRow++){
						for (int hCol = 0; hCol<31; hCol++){
							Button button = new Button("2");
							
						}		
					}
					
					hardCells++;
					continue;
					
				//}
					
					/*
					else{
						continue;
					}
					*/
				
					
				
				
				
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
    	
    	
    	
    	
    	
    	ArrayList<ArrayList<Node>> grid = createGrid();
    	
    	ArrayList<Node> test = new ArrayList<Node>();
    
    	
    	//System.out.print(grid.get(0).get(0).type);
    	
    	Node n = new Node();

    	
    	test = search.astar(grid.get(1).get(1), grid.get(100).get(100));
   
    
    	
    	//System.out.println(test.get(0).type);
    	
    	/*
    	for (int i = 0; i<test.size(); i++){
    		System.out.print(test.get(i));
    	}
    	
    	*/
    	//System.out.print(search.astar(grid.get(1).get(1), grid.get(100).get(100)));
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
