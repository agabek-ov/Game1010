import java.awt.*;

public class Cell {
    private int value;
    private Rectangle rect;

    public Cell(int x, int y, int size) {
        rect = new Rectangle(x, y, size, size);
        value = 0;
    }

    public void copyValue(Cell cell) {
        value = cell.value;
    }

    public Rectangle getRect() {
        return rect;
    }

    public int getValue() {
        return value;
    }

    public void clearValue() {
        value = 0;
    }

    public Color getColor() {
        return GameUtilities.clr_cell[value];
    }


    // method for setting the shape (a 2D array of cells) as selected
    // by scaling it, in order to highlight it
    public static void setSelected(Cell[][] shape, boolean flag) {
        if (shape == null) return;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[0].length; col++) {
                if (shape[row][col] == null) continue;
                if (shape[row][col].getRect().width == 24 && !flag ||
                        shape[row][col].getRect().width == 40 && flag) return;
                Rectangle _rect = shape[row][col].rect;
                shape[row][col].rect = (flag) ? new Rectangle(_rect.x + 20 * row, _rect.y + 20 * col, 40, 40)
                        : new Rectangle(_rect.x - 20 * row, _rect.y - 20 * col, 24, 24);
            }
        }
    }

    // method for filling up a 2D array with a specific value
    // index is for the order of the three shapes under the grid
    public static void fill(Cell[][] cells, int _value, int _index) {
        for (int row = 0; row < cells.length; row++)
            for (int col = 0; col < cells[0].length; col++) {
                int _x = 10 + _index * 155 + row * 24,
                        _y = 600 + col * 24;
                cells[row][col] = new Cell(_x, _y, 24);
                cells[row][col].value = _value;
            }
    }
}
