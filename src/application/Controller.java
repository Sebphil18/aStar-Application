package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Controller implements Initializable{
	
	//53656261737469616e2053
	
	@FXML
    private TextField obstField;

    @FXML
    private TextField mounField;

    @FXML
    private TextField highField;

    @FXML
    private Button generateButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button pathButton;

    @FXML
    private Label stateLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label knotsLabel;

    @FXML
    private Label columnsLabel;

    @FXML
    private Label rowsLabel;

    @FXML
    private CheckBox diagonalCheck;
    
    @FXML
    private TextField widthField;

    @FXML
    private TextField heightField;

    @FXML
    private Button resizeButton;

    @FXML
    private Slider knotWidthSlider;

    @FXML
    private CheckBox seenKnotsCheck;

    @FXML
    private CheckBox disKnotsCheck;

    @FXML
    private CheckBox delayCheck;

    @FXML
    private TextField delayField;

    @FXML
    private Slider widthSlider;

    @FXML
    private Slider heightSlider;

    @FXML
    private Button closeButton;

    @FXML
    private Button miniButton;

    @FXML
    private Label errorLabel;
    
    @FXML
    private Pane canvasPane;
    
    int knotWidth = 1;
    int padding = 1;
    int totalObstacles = 0;
    int delay = 0;
    
    GraphicsContext gc;
    Knot[][] knots;
    Knot objectiveKnot;
    Knot startKnot;
    
    ArrayList<Knot> openlist = new ArrayList<Knot>();
    ArrayList<Knot> closedlist = new ArrayList<Knot>();
	
    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
    
    Canvas canvas = new Canvas(650, 650);
    
	@Override
	public void initialize(URL url, ResourceBundle rec) {
		// TODO Auto-generated method stub
		
		canvasPane.getChildren().add(canvas);
		
		knotWidth = (int) ((int) (canvas.getWidth()+canvas.getHeight())/300);
		padding = knotWidth+knotWidth;
		knots = new Knot[(int) canvas.getWidth()/padding][(int) canvas.getHeight()/padding];
		
		gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);
		
		knotWidthSlider.setValue(knotWidth);
		delayField.setOpacity(1);
		
		System.out.println(CustomFunctions.debugInfo()+"rows: "+knots.length);
		System.out.println(CustomFunctions.debugInfo()+"columns: "+knots[0].length);
		System.out.println(CustomFunctions.debugInfo()+"totalKnots: "+knots.length*knots[0].length);
		System.out.println(CustomFunctions.debugInfo()+"knotWidth: "+knotWidth);
		System.out.println("");
		
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				generateKnots(canvas, knots, padding, knotWidth);
			}
		});
		
		canvas.setOnMouseClicked(e -> {
			
			int x = (int) e.getX()/padding;
			int y = (int) e.getY()/padding;
			
			if(e.getButton().equals(MouseButton.PRIMARY) && knots[x][y].getState() == 3) {
				knots[x][y].changeState(0, gc, x*padding, y*padding, knotWidth);
			}else if(e.getButton().equals(MouseButton.SECONDARY) && knots[x][y].getState() != 3){
				knots[x][y].changeState(3, gc, x*padding, y*padding, knotWidth);
			}
			
		});
		
		canvas.setOnMouseDragged(e -> {
			
			int x = (int) e.getX()/padding;
			int y = (int) e.getY()/padding;
			
			if(x < 0 || x >= knots.length || y < 0 || y >= knots[0].length) return;
			
			if(e.getButton().equals(MouseButton.PRIMARY) && knots[x][y].getState() != 0) {
				knots[x][y].changeState(0, gc, x*padding, y*padding, knotWidth);
			}else if(e.getButton().equals(MouseButton.SECONDARY) && knots[x][y].getState() != 3){
				knots[x][y].changeState(3, gc, x*padding, y*padding, knotWidth);
			}
			
		});
		
		knotsLabel.setText(knots.length*knots[0].length+"");
		columnsLabel.setText(knots[0].length+"");
		rowsLabel.setText(knots.length+"");
		
		errorLabel.setAlignment(Pos.BASELINE_RIGHT);
		errorLabel.setOpacity(0);
		
		stateLabel.setText("initialized");
		System.out.println(CustomFunctions.debugInfo()+"initialized.");
		
	}
	
	@FXML
	public void slideX() {
		canvas.setWidth((int)widthSlider.getValue());
		widthField.setText((int)widthSlider.getValue()+"");
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		reset();
	}
	
	@FXML
	public void slideY() {
		canvas.setHeight((int)heightSlider.getValue());
		heightField.setText((int)heightSlider.getValue()+"");
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		reset();
	}
	
	@FXML
	public void resizeFunction() {
		canvas.setHeight(Double.valueOf(heightField.getText()));
		heightSlider.setValue(Double.valueOf(heightField.getText()));
		canvas.setWidth(Double.valueOf(widthField.getText()));
		widthSlider.setValue(Double.valueOf(widthField.getText()));
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		reset();
	}
	
	@FXML
	public void slideKnotWidth() {
		
		knotWidth = (int) knotWidthSlider.getValue();
		padding = knotWidth+knotWidth;
		
		if(knotWidth > 30) {
			heightSlider.setMin(175);
			widthSlider.setMin(175);
			slideX();
			slideY();
		}else if(knotWidth >25){
			heightSlider.setMin(150);
			widthSlider.setMin(150);
			slideX();
			slideY();
		}else {
			heightSlider.setMin(100);
			widthSlider.setMin(100);
		}
		
		reset();
		
	}
	
	@FXML
	public void generateObstaclesFunction() {
		
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				generateObstacles(knots, obstField, mounField, highField, canvas, padding, knotWidth);
			}
		});
		
		stateLabel.setText("obstacles generated");
		
	}
	
	@FXML
	public void resetFunction() {
		
		reset();
		
	}
	
	@FXML
	public void aStarFunction() {
		
		if(progressBar.getProgress() != 1 && progressBar.getProgress() != 0) {
			CustomFunctions.showErrorMessage("aStar already running", errorLabel);
			return;
		}
		
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				aStar();
			}
		});
		
	}
	
	@FXML
	public void minimizeFunction() {
		
		Main.stage.setIconified(true);
		
	}
	
	@FXML
	public void exitFunction() {
		
		Main.stage.close();
		if(executor.getActiveCount()>0) {
    		
    		System.out.println(CustomFunctions.debugInfo()+"terminating application...");
    		executor.shutdown();
    		
    		if(executor.isShutdown()) {
    			System.exit(1);
    		}
    		
    	}else {
    		
    		System.out.println(CustomFunctions.debugInfo() + "shutting down application...");
    		System.exit(0);
    		
    	}
		
	}
	
	public void reset() {
		
		knots = new Knot[(int) canvas.getWidth()/padding][(int) canvas.getHeight()/padding];
		
		openlist.clear();
		closedlist.clear();
		
		startKnot = null;
		objectiveKnot = null;
		totalObstacles = 0;
		
		gc = canvas.getGraphicsContext2D();
		
		if(knotWidth < 4) {
			gc.setLineWidth(1);
		}else {
			gc.setLineWidth(2);
		}
		
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				generateKnots(canvas, knots, padding, knotWidth);
				
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						knotsLabel.setText(knots.length*knots[0].length+"");
						columnsLabel.setText(knots[0].length+"");
						rowsLabel.setText(knots.length+"");
					}
				});
				
			}
		});
		
		progressBar.setProgress(0);
		
		stateLabel.setText("resetted");
		
	}
	
	public void generateKnots(Canvas canvas, Knot[][] knots, int padding, int knotWidth) {
		
		for(int x = 0 ; x < knots.length ; x++) {
			
			for(int y = 0; y < knots[0].length ; y++) {
				
					Knot knot = new Knot(x, y);
					if(y == 0 && x == 0) {
						
						knot.changeState(6, canvas.getGraphicsContext2D(), x*padding, y*padding, knotWidth);
						startKnot = knot;
						
					}else if(y == (int)canvas.getHeight()/padding-1 && x == (int)canvas.getWidth()/padding-1){
						
						knot.changeState(7, canvas.getGraphicsContext2D(), x*padding, y*padding, knotWidth);
						objectiveKnot = knot;
						
					}else {
						
						knot.changeState(0, canvas.getGraphicsContext2D(), x*padding, y*padding, knotWidth);
						
					}
					knot.setCost(1);
					knots[x][y] = knot;
			}
			
		}
		
		CustomFunctions.setDistanceToObjective(knots, objectiveKnot);
		
	}
	
	public void generateObstacles(Knot[][] knots, TextField obstaclesField, TextField mountainsField, TextField highwayField, Canvas canvas, int padding, int knotWidth) {
		
		if(obstaclesField.getText().isEmpty()) obstaclesField.setText("0");
		if(mountainsField.getText().isEmpty()) mountainsField.setText("0");
		if(highwayField.getText().isEmpty()) highwayField.setText("0");
		
			Random ran = new Random();
			
			int countOb = 0;
			int countMo = 0;
			int countHi = 0;
			
			try {
				countOb = Integer.valueOf(obstaclesField.getText());
				countMo = Integer.valueOf(mountainsField.getText());
				countHi = Integer.valueOf(highwayField.getText());
			}catch(NumberFormatException e) {
				System.out.println(CustomFunctions.debugInfo()+":::ERROR::: letters are not allowed as number");
				CustomFunctions.showErrorMessage("letters are not allowed", errorLabel);
				return;
			}
			
			int counttotal = countHi+countMo+countOb;
			
			if(counttotal+totalObstacles > (knots.length*knots[0].length)-3) {
				System.out.println(CustomFunctions.debugInfo()+":::ERROR::: too many obstacles");
				CustomFunctions.showErrorMessage("too many obstacles", errorLabel);
				return;
			}
			
			int countcurrent = 0;
			
			progressBar.setProgress(0);
			
			for(int i = 0 ; i  < countOb ; i++) {
				
				countcurrent++;
			
				int ranX = ran.nextInt(knots.length);
				int ranY = ran.nextInt(knots[0].length);
			
				Knot knot = knots[ranX][ranY];
				int state = knot.getState();
			
				if(state != 3 && state != 4 && state != 5 && state != 6 && state != 7) {
				
					knots[ranX][ranY].changeState(3, canvas.getGraphicsContext2D(), ranX*padding, ranY*padding, knotWidth);
				
				}else {
					i--;
					countcurrent--;
				}
				
				progressBar.setProgress(countcurrent/counttotal);
				
			}
			
			for(int i = 0 ; i  < countMo ; i++) {
				
				countcurrent++;
			
				int ranX = ran.nextInt(knots.length);
				int ranY = ran.nextInt(knots[0].length);
			
				Knot knot = knots[ranX][ranY];
				int state = knot.getState();
			
				if(state != 3 && state != 4 && state != 5 && state != 6 && state != 7) {
				
					knots[ranX][ranY].changeState(4, canvas.getGraphicsContext2D(), ranX*padding, ranY*padding, knotWidth);
					knots[ranX][ranY].setCost(4);
					
				}else {
					i--;
					countcurrent--;
				}
				
				progressBar.setProgress(countcurrent/counttotal);
				
			}
			
			for(int i = 0 ; i  < countHi ; i++) {
				
				countcurrent++;
			
				int ranX = ran.nextInt(knots.length);
				int ranY = ran.nextInt(knots[0].length);
			
				Knot knot = knots[ranX][ranY];
				int state = knot.getState();
			
				if(state != 3 && state != 4 && state != 5 && state != 6 && state != 7) {
				
					knots[ranX][ranY].changeState(5, canvas.getGraphicsContext2D(), ranX*padding, ranY*padding, knotWidth);
					knots[ranX][ranY].setCost(-1);
				
				}else {
					i--;
					countcurrent--;
				}
				
				progressBar.setProgress(countcurrent/counttotal);
				
			}
			
			totalObstacles += counttotal;
			
	}

	
	public void aStar() {
		
		System.out.println(CustomFunctions.debugInfo()+"-aStar- starting...");
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				stateLabel.setText("calculating...");
				progressBar.setProgress(-1);
			}
		});
		
		int delay = 0;
		if(seenKnotsCheck.isSelected() || disKnotsCheck.isSelected() || delayCheck.isSelected()) {
			
			delayCheck.setSelected(true);
			if(delayField.getText().equals("")) delayField.setText("1");
			
			try {
				
				delay = Integer.valueOf(delayField.getText());
				
			}catch(NumberFormatException e) {
				delayField.setText("1");
				delay = Integer.valueOf(delayField.getText());
			}
			
			if(delay <= 0) {
				
				delay = 1;
				delayField.setText("1");
				CustomFunctions.showErrorMessage("delay can't be <= 0", errorLabel);
				
			}
			
		}
		
		Knot currentKnot = null;
		
		startKnot.setCost(-2);
		openlist.add(startKnot);
		
		while(openlist.size()>0) {
			
			currentKnot = openlist.get(CustomFunctions.getLowestResult(openlist));
			openlist.remove(CustomFunctions.getLowestResult(openlist));
			
			if(currentKnot.getX() == objectiveKnot.getX() && currentKnot.getY() == objectiveKnot.getY()) {
				
				System.out.println(CustomFunctions.debugInfo()+"-aStar- Way found!");
				
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						stateLabel.setText("path found");
						progressBar.setProgress(1);
					}
				});
				
				Knot historyKnot = currentKnot;
				GraphicsContext gc = canvas.getGraphicsContext2D();
				while(!historyKnot.getSuccessor().equals(startKnot)) {
					
					gc.setLineWidth(2);
					gc.setStroke(Color.GREEN);
					gc.setFill(Color.GREEN);
					
					final int xH = historyKnot.getX()*padding+knotWidth/2;
					final int yH = historyKnot.getY()*padding+knotWidth/2;
					final int xS = historyKnot.getSuccessor().getX()*padding+knotWidth/2;
					final int yS = historyKnot.getSuccessor().getY()*padding+knotWidth/2;
					
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							gc.setStroke(Color.GREEN);
							gc.strokeLine(xH, yH, xS, yS);
						}
					});
					
					historyKnot = historyKnot.getSuccessor();
				}
				
				final int xH = historyKnot.getX()*padding+knotWidth/2;
				final int yH = historyKnot.getY()*padding+knotWidth/2;
				final int xS = historyKnot.getSuccessor().getX()*padding+knotWidth/2;
				final int yS = historyKnot.getSuccessor().getY()*padding+knotWidth/2;
				
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						gc.setStroke(Color.GREEN);
						gc.strokeLine(xH, yH, xS, yS);
						
					}
				});
				
				return;
			}
			
			closedlist.add(currentKnot);
			
			if(disKnotsCheck.isSelected()) {
				currentKnot.changeState(2, gc, currentKnot.getX()*padding, currentKnot.getY()*padding, knotWidth);
			}
			
			expandKnot(currentKnot, delay);
			
		}
		
		if(openlist.size() == 0) {
			
			System.out.println(CustomFunctions.debugInfo()+"-aStar- no path found");
			
			gc.setLineWidth(2);
			gc.setStroke(Color.RED);
			gc.setFill(Color.RED);
			
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					gc.strokeLine(startKnot.getX()*padding+knotWidth/2, startKnot.getY()*padding+knotWidth/2, objectiveKnot.getX()*padding+knotWidth/2, objectiveKnot.getY()*padding+knotWidth/2);
					stateLabel.setText("no path");
					progressBar.setProgress(1);
				}
			});
			
		}
		
	}
	
	public void expandKnot(Knot currentKnot, int delay) {
		
		CustomFunctions.getNeighbors(knots, currentKnot, closedlist, diagonalCheck.isSelected());
		
		int teng = 0;
		
		for(int i=0;i<currentKnot.getNeighbors().size();i++) {	
			
			Knot successor = currentKnot.getNeighbors().get(i);
			
			if(seenKnotsCheck.isSelected()) successor.changeState(1, gc, successor.getX()*padding, successor.getY()*padding, knotWidth);
			if(closedlist.contains(successor)) continue;
			
			teng = successor.getCost() + currentKnot.getCost();
			
			if(openlist.contains(successor) && teng >= successor.getCost()) continue;
			
			successor.setSuccessor(currentKnot);
			successor.setCost(teng);
			
			int f = teng + successor.getDistance();
			
			if(openlist.contains(successor)) {
				
				openlist.remove(successor);
				
			}else {
				
				successor.setfValue(f);
				openlist.add(successor);
				
				if(seenKnotsCheck.isArmed()) successor.changeState(1, gc, successor.getX()*padding, successor.getY()*padding, i);
				
			}
			
			
			if(delayCheck.isSelected()) {
				
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}
	
}
