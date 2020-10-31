import javax.swing.*;
import java.awt.*;

public class GameUtilities {
    //Colors
    public static Color clr_background = new Color(0);
    public static Color clr_grid = new Color(60,60,60);
    public static Color clr_text = new Color(255,255,255);
    public static Color[] clr_cell = new Color[]{
            new Color(127, 127, 127), // Empty
            new Color(127,255,127), // 1
            new Color(255,0,0), // 2
            new Color(0,255,0), // 3
            new Color(0,0,255), // 4
            new Color(255,255,0), // 5
            new Color(255,0,255), // 6
            new Color(0,255,255), // 7
            new Color(255,127,127), // 8
            new Color(127,127,255), // 9
    };

    //Images
    public static ImageIcon[] sprites = new ImageIcon[]{
            new ImageIcon("sprites/startIcon.png"), //start icon
            new ImageIcon("sprites/exitIcon.png") //exit icon
    };

    //Points
    /*
    Shape      |  Code  |  Points
    1x1        |  1     |  1
    2x1        |  2     |  2
    3x1        |  3     |  3
    4x1        |  4     |  4
    5x1        |  5     |  5
    2x2        |  6     |  4
    2x2-1x1    |  7     |  3
    3x3        |  8     |  9
    3x3-2x2    |  9     |  5
     */
    public static int[] points = new int[]{
            0, 1, 2, 3, 4, 5, 4, 3, 9, 5
    };
}
