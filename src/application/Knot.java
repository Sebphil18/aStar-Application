package application;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Knot {
	
	//53656261737469616e2053
	
	int x;
	int y;
	int state = 0;
	int fValue;
	int distance;
	int cost;
	
	Knot successor;
	ArrayList<Knot> neighbors = new ArrayList<Knot>();
	
	public Knot(int x, int y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public void changeState(int state, GraphicsContext gc, int x, int y, int knotWidth) {
		
		//0 = hidden
		//1 = seen
		//2 = discovered
		//3 = obstacle
		//4 = mountain
		//5 = highway
		//6 = start
		//7 = objective
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(state == 0) {
					gc.setStroke(Color.WHITE);
				}else if(state == 1) {
					gc.setStroke(Color.ORANGE);
				}else if(state == 2) {
					gc.setStroke(Color.DARKRED);
				}else if(state == 3) {
					gc.setStroke(Color.BLACK);
				}else if(state == 4) {
					gc.setStroke(Color.CADETBLUE);
				}else if(state == 5) {
					gc.setStroke(Color.BLUEVIOLET);
				}else if(state == 6) {
					gc.setStroke(Color.GREEN);
				}else if(state == 7) {
					gc.setStroke(Color.RED);
				}
				
				gc.strokeOval(x, y, knotWidth, knotWidth);
				
			}
		});
		
		this.state = state;
		
	}

	public int getfValue() {
		return fValue;
	}

	public void setfValue(int fValue) {
		this.fValue = fValue;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Knot getSuccessor() {
		return successor;
	}

	public void setSuccessor(Knot successor) {
		this.successor = successor;
	}

	public ArrayList<Knot> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<Knot> neighbors) {
		this.neighbors = neighbors;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
