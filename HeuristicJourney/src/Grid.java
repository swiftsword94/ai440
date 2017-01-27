import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Random;

public class Grid extends Application {
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