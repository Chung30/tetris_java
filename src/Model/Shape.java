package Model;

import java.awt.Color;
import java.awt.Graphics;

import Controler.Board;

public class Shape {
	public static final int Board_wid = 10, Board_hei = 20, Block_size = 30;
	private int x = 4, y = 0;
	private long beginTime;
	private int deltaX = 0;
	private boolean collision = false;
	private int[][] coords;
	private Board board;
	private Color color;
	private int fast = 30;
	private double delayTimeforMovement;
			
	public Shape(int[][] coords, Board board, Color color) {
		this.coords = coords;
		this.board = board;
		this.color = color;
	}
	
	public void setX(int x) {
		this.x = x;
	}


	public void setY(int y) {
		this.y = y;
	}


	public void reset() {
		this.x = 4;
		this.y = 0;
		collision = false;
		cScore();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int[][] getCoords(){
		return coords;
	}
	
	public Color getColor() {
        return color;
    }

	public void update() {
		if(collision) {
			for(int row = 0; row<coords.length; row++) {
				for(int col = 0; col<coords[0].length; col++) {
					if(coords[row][col]!=0) {
						board.getBoard()[y+row][x+col] = color;
					}
				}
			}
			checkLine();
			board.setCurrentShape();
			return;
		}
		
		boolean movX = true;
		if(x+deltaX+coords[0].length<=10&&x+deltaX>=0) {
			for(int row=0; row<coords.length; row++) {
				for(int col=0; col<coords[row].length; col++) {
					if(coords[row][col]!=0) {
						if(board.getBoard()[y+row][x+deltaX+col]!=null) {
							movX = false;
						}
					}
				}
			}
			if(movX) x += deltaX;
		}
		deltaX = 0;
		
		if(System.currentTimeMillis() - beginTime > delayTimeforMovement) {
			
			if(y+1+coords.length<=Board_hei) {
				for(int row=0; row<coords.length; row++) {
					for(int col=0; col<coords[row].length; col++) {
						if(coords[row][col] != 0) {
							if(board.getBoard()[y+1+row][x+deltaX+col] != null) {
								collision = true;
							}
						}
					}
				}
				if(collision == false) {
					y++;
				}
			}
			else {
				collision = true;
			}
			beginTime = System.currentTimeMillis();
			speedDown();
		}
	}
	
	//setup speed when achieve the goal
	private void cScore() {
		if(board.getCntScore() * 250 <= board.getScore()) {
			board.setNormal(board.getNormal() * 0.87);
			board.setCntScore(board.getCntScore()+1);
			System.out.println(board.getCntScore() + " " + board.getScore() + " " + board.getNormal());
		}
	}
	
	private void checkLine() {
		int bottomLine = board.getBoard().length - 1;
		int d=0;
		for(int topLine = board.getBoard().length - 1; topLine>0; topLine--) {
			int cnt = 0;
			for(int col=0; col<board.getBoard()[0].length; col++) {
				if(board.getBoard()[topLine][col] != null) {
					cnt++;
				}
				board.getBoard()[bottomLine][col] = board.getBoard()[topLine][col];
			}
			if(cnt < board.getBoard()[0].length) {
				bottomLine--;
			}
			if(cnt == board.getBoard()[0].length) {
				d++;
			}
		}
		if(d>0) {
			board.addScore(d);
		}
	}
	
	//xoay hình khối
	public void rotateShape() {
		int[][] rotateShape = transposeMatrix(coords);
		reverseRow(rotateShape);
		//
		if(x + rotateShape[0].length > Board.Board_wid || y + rotateShape.length > 20)
			return;
		
		for(int row=0; row<rotateShape.length; row++) {
			for(int col=0; col<rotateShape[row].length; col++) {
				if(rotateShape[row][col] != 0) {
					if(board.getBoard()[y+row][x+col] != null)
						return;
				}
			}
		}
		coords = rotateShape;
	}
	
	//ma trận chuyển vị
	public int[][] transposeMatrix(int[][] matrix) {
		int[][] tem = new int[matrix[0].length][matrix.length];
		for(int row=0; row<matrix.length; row++) {
			for(int col=0; col<matrix[0].length; col++) {
				tem[col][row] = matrix[row][col];
			}
		}
		return tem;
	}
	
	//đảo hàng
	public void reverseRow(int[][] matrix) {
		int mid = matrix.length/2;
		for(int row=0; row<mid; row++) {
			int[] tem = matrix[row];
			matrix[row] = matrix[matrix.length - row - 1];
			matrix[matrix.length - row - 1] = tem;
		}
	}
	
	public void render(Graphics g) {
		for(int row=0;row<coords.length;row++) {
			for(int col=0;col<coords[0].length;col++) {
				if(coords[row][col]!=0) {
					g.setColor(color);
					g.fillRect(col*Block_size+x*Block_size, row*Block_size+y*Block_size, Block_size, Block_size);
				}
			}
		}
	}
	
	public void speedUp() {
		delayTimeforMovement = fast;
	}
	
	public void speedDown() {
		delayTimeforMovement = board.getNormal();
	}
	
	public void dropDown() {
		boolean ck = false;
		while(y+1+coords.length<=Board_hei) {
			for(int row=0; row<coords.length; row++) {
				for(int col=0; col<coords[row].length; col++) {
					if(coords[row][col] != 0) {
						if(board.getBoard()[y+1+row][x+deltaX+col] != null) {
							ck = true;
						}
					}
				}
			}
			if(ck == false) {
				y++;
			}
			else break;
		}
	}
	
	public void moveRight() {
		deltaX = 1;
	}
	
	public void moveLeft() {
		deltaX = -1;
	}
}