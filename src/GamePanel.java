import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private Cell[][] grid;
    private Object[] shapes;
    private Cell[][] selectedShape;
    private int gridSize;
    private int score;
    private boolean isInGame, isDragging;
    private Point mousePtStart, mousePtEnd;
    private int bestScore;
    private Random rnd;

    Rectangle _rect;

    //JButtons
    private JButton jButton_start;
    private JButton jButton_exit;

    //JButton handler
    private ActionHandler actionHandler;

    public GamePanel() {
        this.setLayout(null);
        isInGame = false;
        rnd = new Random();
        gridSize = 10;
        actionHandler = new ActionHandler();
        readFromAFile();
        setUpUI();
        setUIVisibility(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        this.setBackground(GameUtilities.clr_background);

        if (isInGame) {
            drawGrid(g);
            drawShapes(g);
        } else drawMenu(g);

        g.setColor(new Color(255, 0, 0));
        if (mousePtStart != null && mousePtEnd != null) {
            g.drawRect(mousePtStart.x - 5, mousePtStart.y - 5, 10, 10);
            g.drawRect(mousePtEnd.x - 5, mousePtEnd.y - 5, 10, 10);
            g.drawLine(mousePtStart.x, mousePtStart.y, mousePtEnd.x, mousePtEnd.y);
            Graphics2D g2d = (Graphics2D) g;
            if (_rect != null) g2d.draw(_rect);
        }

        g.dispose();
    }

    private void setUpUI() {
        //Adding JButtons to the JPanel
        //Start JButton
        jButton_start = new JButton("     START", GameUtilities.sprites[0]);
        jButton_start.setBounds(10, 235, 430, 50);
        jButton_start.addActionListener(actionHandler);
        add(jButton_start);


        //Exit JButton
        jButton_exit = new JButton("       EXIT", GameUtilities.sprites[1]);
        jButton_exit.setBounds(10, 325, 430, 50);
        jButton_exit.addActionListener(actionHandler);
        add(jButton_exit);
    }

    private void setUIVisibility(boolean isVisible) {
        jButton_start.setVisible(isVisible);
        jButton_exit.setVisible(isVisible);
    }

    private void initiateGrid() {
        score = 0;
        grid = new Cell[gridSize][gridSize];

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                grid[row][col] = new Cell(5 + row * 44, 150 + col * 44, 40);
            }
        }

        shapes = new Object[3];
        for (int i = 0; i < 3; i++)
            shapes[i] = createRandomShape(i);
    }

    private void drawMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 36));
        g2d.setColor(GameUtilities.clr_text);
        g2d.drawString("Онға Он", 165, 60);
    }

    private void drawGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(GameUtilities.clr_grid);
        g2d.fillRect(0, 145, 445, 445);

        // printShape(g2d);

        for (Cell[] row : grid)
            for (Cell cell : row) {
                g2d.setColor(cell.getColor());
                g2d.fill(cell.getRect());
                highlight(g2d, cell);
            }

        g2d.setFont(new

                Font("TimesRoman", Font.PLAIN, 36));
        g2d.setColor(GameUtilities.clr_text);

        if (bestScore < score) bestScore = score;
        g2d.drawString("Score: " + score, 0, 100);
        g2d.drawString("Best: " + bestScore, 270, 100);
        g2d.drawString("Онға Он", 0, 30);
    }

    private void highlight(Graphics2D g2d, Cell cell) {
        if (selectedShape == null) return;
        for (Cell[] row : selectedShape)
            for (Cell shapeCell : row) {
                if(shapeCell == null) continue;
                int x1 = (int)shapeCell.getRect().getCenterX() + mousePtEnd.x-mousePtStart.x,
                        x2 = (int)cell.getRect().getCenterX(),
                        y1 = (int)shapeCell.getRect().getCenterY() + mousePtEnd.y-mousePtStart.y,
                        y2 = (int)cell.getRect().getCenterY();
                double dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

                if (dist <= 19) {
                    g2d.setColor(Color.BLUE);
                    g2d.drawLine((int)shapeCell.getRect().getX()+ mousePtEnd.x-mousePtStart.x,
                            (int)shapeCell.getRect().getY()+ mousePtEnd.y-mousePtStart.y,
                            (int)cell.getRect().getX(), (int)cell.getRect().getY());
                    g2d.setColor(new Color(0, 255, 0, 120));
                    g2d.draw(cell.getRect());
                    return;
                }
            }
    }

    private void drawShapes(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 2; i>-1; i--) {
            Cell[][] shape = (Cell[][]) shapes[i];
            if(shape == null) continue;
            for (Cell[] cells : shape)
                for (int col = 0; col < shape[0].length; col++) {
                    if (cells[col] == null) continue;
                    Rectangle _rect = cells[col].getRect();
                    if(shape == selectedShape) {
                        int dX = mousePtEnd.x - mousePtStart.x,
                                dY = mousePtEnd.y - mousePtStart.y;
                        _rect = new Rectangle(_rect.x + dX, _rect.y + dY, _rect.width, _rect.height);
                    }
                    g2d.setColor(cells[col].getColor());
                    g2d.fill(_rect);
                    g2d.setColor(GameUtilities.clr_background);
                    g2d.draw(_rect);
                }
        }
    }

    /*
    Possible shapes (Combinations):
    3x3, 3x3-2x2 (4), 3x1 (2),      Total: 3 (7)
    2x2, 2x2-1x1 (4), 2x1 (2),      Total: 3 (7)
    1x1, 4x1 (2), 5x1 (2)           Total: 3 (5)

    Shape      |  Code
    1x1        |  1
    2x1        |  2
    3x1        |  3
    4x1        |  4
    5x1        |  5
    2x2        |  6
    2x2-1x1    |  7
    3x3        |  8
    3x3-2x2    |  9
     */
    private Cell[][] createRandomShape(int index) {
        int shapeCode = rnd.nextInt(9) + 1; // to start from 1, end at 9

        int row, col;
        Cell[][] shape;
        switch (shapeCode) {
            case 2:
                row = rnd.nextInt(2); //either vertical (0) or horizontal (1) line
                shape = new Cell[row + 1][2 - row];
                Cell.fill(shape, 2, index);
                break;
            case 3:
                row = rnd.nextInt(2); //either vertical (0) or horizontal (1) line
                shape = new Cell[2 * row + 1][3 - 2 * row];
                Cell.fill(shape, 3, index);
                break;
            case 4:
                row = rnd.nextInt(2); //either vertical (0) or horizontal (1) line
                shape = new Cell[3 * row + 1][4 - 3 * row];
                Cell.fill(shape, 4, index);
                break;
            case 5:
                row = rnd.nextInt(2); //either vertical (0) or horizontal (1) line
                shape = new Cell[4 * row + 1][5 - 4 * row];
                Cell.fill(shape, 5, index);
                break;
            case 6:
                shape = new Cell[2][2];
                Cell.fill(shape,6, index);
                break;
            case 7:
                shape = new Cell[2][2];
                Cell.fill(shape,7, index);

                row = rnd.nextInt(1);
                col = rnd.nextInt(1);

                //Randomly deleting 1x1 inside a 2x2
                shape[row][col] = null;
                break;
            case 8:
                shape = new Cell[3][3];
                Cell.fill(shape,8, index);
                break;
            case 9:
                shape = new Cell[3][3];
                Cell.fill(shape,9, index);

                row = rnd.nextInt(1);
                col = rnd.nextInt(1);

                //Randomly deleting 2x2 inside a 3x3
                shape[row][col] = null;
                shape[row + 1][col] = null;
                shape[row][col + 1] = null;
                shape[row + 1][col + 1] = null;
                break;
            default:
                shape = new Cell[1][1];
                Cell.fill(shape,1, index);
                break;
        }

        return shape;
    }

    private boolean hasSelectedShape(Point mousePos) {
        for (Object obj : shapes) {
            if(obj == null) continue;
            Cell[][] shape = (Cell[][]) obj;
            for (Cell[] cells : shape)
                for (int col = 0; col < shape[0].length; col++) {
                    if(cells[col] == null) continue;
                    Rectangle _rect = cells[col].getRect();
                    if (_rect.x <= mousePos.x &&
                            _rect.x + _rect.width >= mousePos.x &&
                            _rect.y <= mousePos.y &&
                            _rect.y + _rect.height >= mousePos.y) {

                        Cell.setSelected(shape, true);
                        selectedShape = shape;
                        return true;
                    }
                }
        }
        return false;
    }

    private boolean assignShapeToGrid() {
        if (selectedShape == null) return false;
        Cell.setSelected(selectedShape, true);
        Cell copyCell = null;
        // indexes of cells in the grid that will be coloured
        List<int[]> indexes = new ArrayList<>();
        for (Cell[] row : selectedShape)
            for (Cell cell : row) {
                if (cell == null) continue;
                int[] xy = isInsideAGrid(cell);
                if (xy == null) return false;
                indexes.add(xy);
                if (copyCell == null)
                    copyCell = cell;
            }
        for (int[] xy : indexes)
            grid[xy[0]][xy[1]].copyValue(copyCell);
        score += GameUtilities.points[copyCell.getValue()];
        return true;
    }

    private int[] isInsideAGrid(Cell shapeCell) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col].getValue() != 0) continue;
                Cell cell = grid[row][col];
                int x1 = (int) shapeCell.getRect().getCenterX() + mousePtEnd.x - mousePtStart.x,
                        x2 = (int) cell.getRect().getCenterX(),
                        y1 = (int) shapeCell.getRect().getCenterY() + mousePtEnd.y - mousePtStart.y,
                        y2 = (int) cell.getRect().getCenterY();
                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

                if (distance <= 19) return new int[]{row, col};
            }
        }
        return null;
    }

    private void updateGrid() {
        // Clear assigned shape from new shapes list
        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] == selectedShape)
                shapes[i] = null;
        }
        selectedShape = null;

        // Check if new shapes list is empty
        boolean shapesEmpty = true;
        for (Object obj : shapes) {
            if (obj != null) {
                shapesEmpty = false;
                break;
            }
        }

        // If empty, create three new shapes
        if (shapesEmpty) {
            boolean isTerminal = true;

            // If all three new shapes does not fit, create three new
            while (isTerminal) {
                for (int i = 0; i < 3; i++)
                    shapes[i] = createRandomShape(i);
                for (Object shape : shapes) {
                    if (shape == null) continue;
                    if (shapeFits((Cell[][]) shape)) {
                        isTerminal = false;
                        break;
                    }
                }
            }
        }

        // Count non-empty cells for rows and cols
        int[] rowsCount = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Counter of non-empty cells for each row
        int[] colsCount = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Counter of non-empty cells for each col
        for (int row = 0; row < gridSize; row++)
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col].getValue() != 0) {
                    rowsCount[row] += 1;
                    colsCount[col] += 1;
                }
            }

        // Check if any vertical or horizontal line is full
        for (int i = 0; i < gridSize; i++) {
            if (rowsCount[i] == 10) {
                score += 10;
                for (int j = 0; j < gridSize; j++)
                    grid[i][j].clearValue();
            }
            if (colsCount[i] == 10) {
                score += 10;
                for (int j = 0; j < gridSize; j++)
                    grid[j][i].clearValue();
            }
        }

        // Check if enough space in the grid for at least one of the new shapes
        boolean isTerminal = true;
        for (Object shape : shapes) {
            if (shape == null) continue;
            if (shapeFits((Cell[][]) shape)) {
                isTerminal = false;
                break;
            }
        }

        // Game ends and shows the message box
        if (isTerminal) {
            repaint();
            JOptionPane.showMessageDialog(this, "Game has ended");
            initiateGrid();
            repaint();
        }
    }

    private boolean shapeFits(Cell[][] shape) {
        for (int row = 0; row < gridSize; row++)
            for (int col = 0; col < gridSize; col++) {
                boolean fits = true;
                for (int r = 0; r < shape.length; r++) {
                    for (int c = 0; c < shape[0].length; c++) {
                        if (shape[r][c] == null) continue;
                        if (row + r >= gridSize || col + c >= gridSize){
                            fits = false;
                            break;
                        }
                        if (grid[row + r][col + c].getValue() != 0) {
                            fits = false;
                            break;
                        }
                    }
                    if (!fits)
                        break;
                }
                if (fits)
                    return true;
            }
        return false;
    }

    private void saveToAFile() {
        if (score < bestScore) return;
        try (PrintWriter out = new PrintWriter("bestscore.txt")) {
            out.println(bestScore);
        } catch (FileNotFoundException ignored) {
        }
    }

    private void readFromAFile() {
        try (FileReader in = new FileReader("bestscore.txt")) {
            BufferedReader bufferedReader = new BufferedReader(in);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                bestScore = Integer.parseInt(line);
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        //To exit the game
        //Escape key is pressed
        switch (keyCode) {
            case KeyEvent.VK_R:
                initiateGrid();
                repaint();
                break;
            case KeyEvent.VK_ESCAPE:
                int response = JOptionPane.showConfirmDialog(
                        this, "Are you sure you want to exit the game?",
                        "Powers of Two", JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
                saveToAFile();
                if (response == 0) {
                    System.exit(0);
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isDragging = hasSelectedShape(e.getPoint());
        if(isDragging) {
            mousePtStart = e.getPoint();
            mousePtEnd = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDragging = false;

        mousePtEnd = e.getPoint();
        if(assignShapeToGrid()) updateGrid();
        Cell.setSelected(selectedShape, false);
        mousePtEnd = mousePtStart;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isDragging){
            mousePtEnd = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    // inner class to handle action events from JButtons
    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == jButton_start) {
                isInGame = true;
                setUIVisibility(false);
                initiateGrid();
                repaint();
            } else if (e.getSource() == jButton_exit) {
                saveToAFile();
                System.exit(0);
            }
        }
    }
}
