import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Random;
import java.util.ArrayList
import java.util.PriorityQueue

public class Grid extends Application {
	public static class search
	{
		public search()
		{
			
		}
		//will use euclidian distance
		private float getHeuristic(Node start, Node end)
		{
			return (start.y-end.y)/(start.x-end.x);
		}
		/*
			This Method uses A* to traverse the grid from the start to end. 
			@param grid An arraylist of Cells that will be traversed.
			@param start The start of the traversal.
			@param end The end Cell needed to traverse to.
			@see Cell
		*/
		public static ArrayList<Node> astar(ArrayList<ArrayList<Node>> grid, Node start, Node end)
		{
			Node ptr = start;
			PriorityQueue<Node> fringe = new PriorityQueue<Node>(8);//up to 8 neighbors in the middle of a block
			while(true)
			{
				//fill
			}
		}
	}
		@Override
	    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Grid");
      
        GridPane gridPane = new GridPane();

        
        //creates grid
        //0 indicates blocked cell
        //1 indicates regular unblocked cell
        //2 indicates hard to traverse cell
        //a indicates regular unblocked cell with highway
        //b indicates hard to traverse cell with a highway
        
        //randomize function for unique cell
        String[] cellTypes = {"0","1", "2", "a", "b"};
        Random rand = new Random();
        
        
        for (int i=0; i<120; i++){
        	for (int j=0; j<160;j++){
        		int  random = rand.nextInt(5);
        		//50 is the maximum and the 1 is our minimum
        		Button button = new Button(cellTypes[random]);
        		gridPane.add(button, i, j, 1, 1);
        	}
        }
        

        Scene scene = new Scene(gridPane, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
