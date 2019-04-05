package application;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class CustomFunctions {
	
	//53656261737469616e2053
	
	public static String debugInfo() {
		
		String debugInfo = "["+
				 		   new Exception().getStackTrace()[1].getClassName() + " (" +
				 		   new Exception().getStackTrace()[1].getMethodName() + ") {" +
				 		   new Exception().getStackTrace()[1].getLineNumber() + "}] "
				 		   ;
		
		return debugInfo;
		
	}
	
	public static void getNeighbors(Knot[][] knots, Knot knot, ArrayList<Knot> closedlist, boolean diagonal) {
		
		ArrayList<Knot> neightbors = new ArrayList<Knot>();
		
		int x = (knot.getX());
		int y = (knot.getY());
		
		for (int nX = Math.max(0, x - 1); nX <= Math.min(x + 1, knots.length - 1); ++nX){
		    for (int nY = Math.max(0, y - 1); nY <= Math.min(y + 1, knots[0].length - 1); ++nY) {
		    	
		        if (!(nX==x && nY==y))  {
		        	
		        	if(knots[nX][nY] != null) {
		        		
		        		if(!(knots[nX][nY].getState() == 3||closedlist.contains(knots[nX][nY]))){
		        			
		        			int rX = x-nX;
		        			int rY = y-nY;
		        			
		        			if(Math.abs(rX) == Math.abs(rY) && !diagonal) continue;
		        			
		        			neightbors.add(knots[nX][nY]);
		        			
		        		}else {
		        		
		        		}	
		        	}
		        }
		    }
		}
		
		knot.setNeighbors(neightbors);
		
	}

	public static void setDistanceToObjective(Knot[][] knots, Knot objective) {
		for( int cX=0 ; cX < knots.length ; cX++) {
		
			for( int cY=0 ; cY < knots[0].length ; cY++) {
			
				Knot knot = knots[cX][cY];
			
				int allX = objective.getX()-knot.getX();
				int allY = objective.getY()-knot.getY();
			
				int distance = allX+allY;
			
				knots[cX][cY].setDistance(distance);
			
			}
		}
	}

	public static int getLowestResult(ArrayList<Knot> knots) {
	
		Knot lowestKnot = null;
	
		int id=-1;
	
		for(int i=0;i<knots.size();i++) {
		
			if(lowestKnot == null) {
			
				lowestKnot = knots.get(i);
			
				id = i;
			
			}else if(lowestKnot.getfValue()>knots.get(i).getfValue()) {
			
				lowestKnot = knots.get(i);
			
				id = i;
			
			}
		
		}
	
		return id;
	
	}
	
	public static void showErrorMessage(String errorMsg, Label error) {
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				error.setText(errorMsg);
				
				Timeline timeline = new Timeline();
				timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(error.opacityProperty(), 1)));
				timeline.getKeyFrames().add(new KeyFrame(Duration.millis(4000), new KeyValue(error.opacityProperty(), 0)));
				
				timeline.play();
			}
		});
		
	}

}
