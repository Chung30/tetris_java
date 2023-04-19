package View;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

import Controler.Board;

public class WindowGame {
    public static final int width = 445, height = 639;

    private Board board;
    private Title title;
    private JFrame window;

    public WindowGame() {

        window = new JFrame("Tetris");
        window.setSize(width, height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        
//      favicon
        Image icon = Toolkit.getDefaultToolkit().getImage("data/icon.png");    
        window.setIconImage(icon); 
        
        board = new Board();
        title = new Title(this);

        window.addKeyListener(board);
        window.addMouseMotionListener(title);
        window.addMouseListener(title);
        window.addKeyListener(title);
        window.add(title);

        window.setVisible(true);
    }

    public void startTetris() {
        window.remove(title);
        window.removeMouseMotionListener(title);
        window.removeMouseListener(title);
        window.removeKeyListener(title);
        window.addMouseMotionListener(board);
        window.addMouseListener(board);
        window.add(board);
        board.startGame();
        window.revalidate();
    }

    public static void main(String[] args) {
        new WindowGame();
    }

}