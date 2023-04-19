package Controler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;

import View.WindowGame;
import Model.Shape;
import Model.ImageLoader;

public class Board extends JPanel implements KeyListener, MouseListener, MouseMotionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean gamePaused = false;

    private boolean gameOver = false;
	
	private static int fps = 60;
	private static int delay = 1000/fps;

	public static final int Board_wid = 10, Board_hei = 20, Block_size = 30;
	private Timer looper;
	private Color[][] board = new Color[Board_hei][Board_wid];
	
	private Color[] colors = {
			Color.decode("#ff0000"), Color.decode("#0000ff"), Color.decode("#00ff00"),
			Color.decode("#ffff00"), Color.decode("#ff9903"), Color.decode("#ff00ff"), Color.decode("#6600ff")
	};
	
	private boolean leftClick = false;
	private Clip music;
	private Rectangle refreshBounds;
	private Rectangle stopBounds;
	private BufferedImage pause, refresh;
	private Shape[] shapes = new Shape[7];
	private Shape currentShape, nextShape;
	private Color colorBoard = Color.gray;
	private int mouseX, mouseY;
	private int score = 0, cntScore = 1;
	private double normal = 600;
	
	Random rd = new Random();
	
	private Timer buttonLapse = new Timer(300, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            buttonLapse.stop();
        }
    });
	
	
	public Board() {
			
		setPause(ImageLoader.loadImage("/pause.png"));
        setRefresh(ImageLoader.loadImage("/refresh.png"));
//        Player player = new Player(new FileInputStream("/file.mp3"));
//        music = ImageLoader.loadMusic("/MagicOfLove.mp3");
        
//        music.loop(Clip.LOOP_CONTINUOUSLY);
        
        setMouseX(0);
        setMouseY(0);
		
		setStopBounds(new Rectangle(345, 500, getPause().getWidth(), getPause().getHeight() + getPause().getHeight() / 2));
        setRefreshBounds(new Rectangle(350, 500 - getRefresh().getHeight() - 20, getRefresh().getWidth(),
                getRefresh().getHeight() + getRefresh().getHeight() / 2));
        
//      create game looper
        looper = new Timer(delay, new GameLooper());
		
		shapes[0] = new Shape(new int[][] {
			{ 1, 1, 1, 1}
		}, this, colors[0]);
		
		shapes[1] = new Shape(new int[][] {
			{ 1, 1, 1},
			{ 0, 1, 0},
		}, this, colors[1]);
		
		shapes[2] = new Shape(new int[][] {
			{ 1, 1, 1},
			{ 1, 0, 0},
		}, this, colors[2]);
		
		shapes[3] = new Shape(new int[][] {
			{ 1, 1, 1},
			{ 0, 0, 1},
		}, this, colors[3]);
		
		shapes[4] = new Shape(new int[][] {
			{ 1, 1, 0},
			{ 0, 1, 1},
		}, this, colors[4]);
		
		shapes[5] = new Shape(new int[][] {
			{ 0, 1, 1},
			{ 1, 1, 0},
		}, this, colors[5]);
		
		shapes[6] = new Shape(new int[][] {
			{ 1, 1},
			{ 1, 1},
		}, this, colors[6]);
	}
	
	
	//Setup game
	
	
	public void update() {
		if (getStopBounds().contains(getMouseX(), getMouseY()) && leftClick && !buttonLapse.isRunning() && !gameOver) {
            buttonLapse.start();
            gamePaused = !gamePaused;
        }

        if (getRefreshBounds().contains(getMouseX(), getMouseY()) && leftClick) {
        	gamePaused = !gamePaused;
        	startGame();
        }

        if (gamePaused || gameOver) {
            return;
        }
        currentShape.update();
	}
	
	public boolean isGamePaused() {
		return gamePaused;
	}


	public void setGamePaused(boolean gamePaused) {
		this.gamePaused = gamePaused;
	}


	public boolean isGameOver() {
		return gameOver;
	}


	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}


	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(getColorBoard());//black
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for(int row=0;row<board.length;row++) {
			for(int col=0;col<board[row].length;col++) {
				if(board[row][col]!=null) {
					g.setColor(board[row][col]);
					g.fillRect(col*Block_size, row*Block_size, Block_size, Block_size);
				}
			}
		}

		g.setColor(getNextShape().getColor());
		
        for (int row = 0; row < getNextShape().getCoords().length; row++) {
            for (int col = 0; col < getNextShape().getCoords()[0].length; col++) {
                if (getNextShape().getCoords()[row][col] != 0) {
                    g.fillRect(col * 30 + 320, row * 30 + 50, Board.Block_size, Board.Block_size);
                }
            }
        }
		
		currentShape.render(g);
		
		if (!getStopBounds().contains(getMouseX(), getMouseY())) {
            g.drawImage(getPause().getScaledInstance(getPause().getWidth() + 3, getPause().getHeight() + 3, 
            		BufferedImage.SCALE_DEFAULT), getStopBounds().x + 3, getStopBounds().y + 3, null);
        } else {
            g.drawImage(getPause(), getStopBounds().x, getStopBounds().y, null);
        }

        if (!getRefreshBounds().contains(getMouseX(), getMouseY())) {
            g.drawImage(getRefresh().getScaledInstance(getRefresh().getWidth() + 3, getRefresh().getHeight() + 3,
                    BufferedImage.SCALE_DEFAULT), getRefreshBounds().x + 3, getRefreshBounds().y + 3, null);
        } else {
            g.drawImage(getRefresh(), getRefreshBounds().x, getRefreshBounds().y, null);
        }

		if (gamePaused) {
            String gamePausedString = "GAME PAUSED";
            g.setColor(Color.WHITE);
            g.setFont(new Font("Georgia", Font.BOLD, 30));
            g.drawString(gamePausedString, 35, WindowGame.height / 2);
        }
        if (gameOver) {
            String gameOverString = "GAME OVER";
            g.setColor(Color.WHITE);
            g.setFont(new Font("Georgia", Font.BOLD, 30));
            g.drawString(gameOverString, 50, WindowGame.height / 2);
        }

        g.setColor(Color.WHITE);
				
		g.setFont(new Font("Georgia", Font.BOLD, 20));

        g.drawString("SCORE", WindowGame.width - 125, WindowGame.height / 2);
        g.drawString(score + "", WindowGame.width - 125, WindowGame.height / 2 + 30);

        for (int i = 0; i <= Board_hei; i++) {
            g.drawLine(0, i * Block_size, Board_wid * Block_size, i * Block_size);
        }
        for (int j = 0; j <= Board_wid; j++) {
            g.drawLine(j * Block_size, 0, j * Block_size, Board_hei * 30);
        }
	}
	
	public void setNextShape() {
        int index = rd.nextInt(shapes.length);
        int colorIndex = rd.nextInt(colors.length);
        nextShape = new Shape(shapes[index].getCoords(), this, colors[colorIndex]);
    }
	
	public void setCurrentShape() {
		
		currentShape = getNextShape();
		setNextShape();
		currentShape.reset();
		checkGameover();
	}
	
	public Color[][] getBoard() {
		return board;
	}
	
	private void checkGameover() {
		int [][]coords = currentShape.getCoords();
		for(int row = 0; row < coords.length; row++) {
			for(int col = 0; col < coords[0].length; col++) {
				if(coords[row][col] != 0) {
					if(board[row + currentShape.getY()][col + currentShape.getX()] != null) {
						gameOver = true;
					}
				}
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(gameOver==false && gamePaused==false) {
			if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				currentShape.speedUp();
			}
			else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				currentShape.moveRight();
			}
			else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				currentShape.moveLeft();
			}
			else if(e.getKeyCode() == KeyEvent.VK_UP) {
				currentShape.rotateShape();
			}
			else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				currentShape.dropDown();
			}
		}
		
		//clean board when game over
		if(gameOver == true) {
			File file = new File("data/data_score.txt");
    		
    		BufferedWriter bw = null;
    		FileWriter fw = null;
    		try {
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	
				// true = append file
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
	
				bw.write(score + "\n");
	
			} catch (IOException ei) {

				ei.printStackTrace();
			} finally {
				
				try {
					if (bw != null) bw.close();
					if (fw != null) fw.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				
				startGame();
			}
		}
		
		//pause game
		if(gamePaused==false) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				gamePaused = true;
			}
		}
		else if(gamePaused==true) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				gamePaused = false;
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			currentShape.speedDown();
		}
	}
	
	public void startGame() {
		score = 0;
        cntScore = 1;
        setNormal(600);
        stopGame();
        setNextShape();
        setCurrentShape();
        gameOver = false;
        looper.start();

    }
	
	public void stopGame() {
        
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = null;
            }
        }
        looper.stop();
    }
    
    class GameLooper implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }
    }

	@Override
	public void mouseDragged(MouseEvent e) {
		setMouseX(e.getX());
		setMouseY(e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setMouseX(e.getX());
		setMouseY(e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
            leftClick = true;
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
            leftClick = false;
        }
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void addScore(int d) {
        if(d == 1) {
        	score += 100;
        }
        else if(d == 2) {
        	score += 250;
        }
        else if(d == 3) {
        	score += 400;
        }
        else if(d == 4) {
        	score += 500;
        }
    }
	
	public int getCntScore() {
		return cntScore;
	}

	public void setCntScore(int cntScore) {
		this.cntScore = cntScore;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public double getNormal() {
		return normal;
	}

	public void setNormal(double normal) {
		this.normal = normal;
	}

	public Shape getNextShape() {
		return nextShape;
	}

	public void setNextShape(Shape nextShape) {
		this.nextShape = nextShape;
	}


	public int getMouseX() {
		return mouseX;
	}


	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}


	public int getMouseY() {
		return mouseY;
	}


	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}


	public BufferedImage getPause() {
		return pause;
	}


	public void setPause(BufferedImage pause) {
		this.pause = pause;
	}


	public Rectangle getRefreshBounds() {
		return refreshBounds;
	}


	public void setRefreshBounds(Rectangle refreshBounds) {
		this.refreshBounds = refreshBounds;
	}


	public BufferedImage getRefresh() {
		return refresh;
	}


	public void setRefresh(BufferedImage refresh) {
		this.refresh = refresh;
	}


	public Color getColorBoard() {
		return colorBoard;
	}


	public void setColorBoard(Color colorBoard) {
		this.colorBoard = colorBoard;
	}


	public Rectangle getStopBounds() {
		return stopBounds;
	}


	public void setStopBounds(Rectangle stopBounds) {
		this.stopBounds = stopBounds;
	}
}