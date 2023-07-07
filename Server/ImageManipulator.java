package Server;

import java.io.Serializable;

import compute.Task;

public class ImageManipulator implements Task<int[][][]>, Serializable {
    private int[][][] imageData;
    private int layer;

    private static final long serialVersionUID = 227L;

    ImageManipulator(int[][][] i, int layer) {
        this.imageData = i;
        this.layer = layer;
    }

    

    public int[][][] execute() {
        return grayed();
    }

    public int[][][] grayed() {
        int width = imageData.length;
        int height = imageData[0].length;

        int[][][] grayedData = new int[width][height][3];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grayedData[x][y][0] = imageData[x][y][layer];
                grayedData[x][y][1] = imageData[x][y][layer];
                grayedData[x][y][2] = imageData[x][y][layer];
            }
        }

        return grayedData;
    }

}
