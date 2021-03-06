import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class TriangleGrid implements Serializable {
    int width, height, trueWidth, trueHeight;
    Point[][] points;
    Polygon[] triangles;

    public TriangleGrid(int w, int h, int trueW, int trueH) {
        width = w;
        height = h;
        trueHeight = trueH;
        trueWidth = trueW;
        points = new Point[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                points[i][j] = new Point(trueW * i / (width - 1), trueH * j / (height - 1));
            }
        }
    }

    public Polygon[] setupGrid(){
        int triNum = (width-1) * (height - 1) *2;
        triangles = new Polygon[triNum];

        int count = 0;
        for(int i = 0; i< width -1; i++){
            for(int j = 0; j< height -1; j++){
                /*
                    p1---> *-----------* <---p2
                           |           |
                           |           |
                    p4---> *-----------* <---p3
                 */
                Point p1 = points[i][j];
                Point p2 = points[i+1][j];
                Point p3 = points[i+1][j+1];
                Point p4 = points[i][j+1];

                int[] xValuesTR = {p1.x, p3.x, p2.x};
                int[] xValuesBL = {p1.x, p3.x, p4.x};
                int[] yValuesTR = {p1.y, p3.y, p2.y};
                int[] yValuesBL = {p1.y, p3.y, p4.y};
                Polygon topRight = new Polygon(xValuesTR, yValuesTR, 3);
                Polygon bottomLeft = new Polygon(xValuesBL, yValuesBL, 3);
                triangles[count] = topRight;
                count++;
                triangles[count] = bottomLeft;
                count++;
            }
        }
        return triangles;
    }
    public void reset(){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                points[i][j] = new Point(trueWidth * i / (width - 1), trueHeight * j / (height - 1));
            }
        }
    }
    public int getWidth(){
        return width;

    }
    public int getHeight(){
        return height;
    }
    public int getTrueWidth(){
        return trueWidth;
    }
    public int getTrueHeight(){
        return trueHeight;
    }
    public String toString(){
        String returnString = "";
        for(int i = 0; i<width; i++){
            for(int j = 0; j < height; j++){
                returnString+=Integer.toString(points[i][j].x);
                returnString+=Integer.toString(points[i][j].y);

            }
        }
        return returnString;
    }
    public Polygon[] getTriangles(){
        return triangles;
    }

}
