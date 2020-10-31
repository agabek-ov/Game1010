import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("10x10");
        setBounds(300, 200, 445, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gamePanel = new GamePanel();
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(gamePanel);
        gamePanel.addMouseListener(gamePanel);
        gamePanel.addMouseMotionListener(gamePanel);
        add(gamePanel);
    }

    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame();
        gameFrame.setVisible(true);
    }
}
