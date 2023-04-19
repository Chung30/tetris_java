package View;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.Timer;

import Model.ImageLoader;

public class Title extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

	private BufferedImage instructions;
	private WindowGame window;
	private Timer timer;
	private int mouseX = 0, mouseY = 0;
	private boolean click = false;
	private Rectangle playBtn;

	public Title(WindowGame window) {
		instructions = ImageLoader.loadImage("/tetrislogo.jpg");

		timer = new Timer(1000 / 60, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}

		});
		timer.start();
		this.window = window;

		playBtn = new Rectangle(WindowGame.width / 2 - 70, 280, 120, 50);
	}

	public int updateScore() throws IOException {
		File file = new File("data/data_score.txt");
		int maxScore = 0;
		LinkedList<Integer> arrScore = new LinkedList<>();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line = "";
		try {
			while ((line = bufferedReader.readLine()) != null) {
				try {
					arrScore.add(Integer.parseInt(line));
				} catch (NumberFormatException e) {
					throw new IOException(e.getMessage());
				}

			}
			arrScore.sort(null);
			if (arrScore.isEmpty())
				maxScore = 0;
			else
				maxScore = arrScore.get(arrScore.size() - 1);
		} finally {
			if (inputStreamReader != null)
				inputStreamReader.close();
			if (bufferedReader != null)
				bufferedReader.close();
		}
		return maxScore;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.white);

		g.fillRect(0, 0, WindowGame.width, WindowGame.height);

		g.drawImage(instructions, WindowGame.width / 2 - instructions.getWidth() / 2 - 10,
				100 - instructions.getHeight() / 2, null);

		g.setColor(Color.black);
		g.setFont(new Font("Georgia", Font.BOLD, 24));
		try {
			g.drawString("Best score: " + updateScore(), 110, 200);
		} catch (IOException e) {
			g.drawString("Best score: 0", 110, 200);
			e.printStackTrace();
		}

		if (playBtn.contains(mouseX, mouseY)) {
			g.drawRoundRect(WindowGame.width / 2 - 80 - 5, 250 - 5, 130, 60, 25, 25);
		} else {
			g.drawRoundRect(WindowGame.width / 2 - 80, 250, 120, 50, 20, 20);
		}
		g.drawString("Play", WindowGame.width / 2 - 45, 285);
		
		g.drawRoundRect(WindowGame.width / 2 - 190, 330, 360, 250, 20, 20);
		g.setFont(new Font("TimeNewRoman", Font.BOLD, 16));
		g.drawString("Press VK_LEFT, VK_RIGHT, VK_DOWN", WindowGame.width / 2 - 170, 360);
		g.drawString(" to move left, right and speed up.", WindowGame.width / 2 - 170, 390);
		g.drawString("Press VK_UP to rotate shape.", WindowGame.width / 2 - 170, 420);
		g.drawString("Press VK_ENTER to drop down.", WindowGame.width / 2 - 170, 450);
		g.drawString("Press VK_SPACE to pause game", WindowGame.width / 2 - 170, 480);
		g.drawString(" or restart when end game.", WindowGame.width / 2 - 170, 510);
		g.drawString("Press VK_0 or Play to start game.", WindowGame.width / 2 - 170, 540);
	}

	public void menu() {
		if (playBtn.contains(mouseX, mouseY) && click) {
			window.startTetris();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			click = true;
			System.out.println(mouseX + " " + mouseY);
			if (playBtn.contains(mouseX, mouseY) && click) {
				System.out.println("true");
			}
			menu();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			click = false;
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

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_0) {
			window.startTetris();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}