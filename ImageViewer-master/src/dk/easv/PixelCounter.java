package dk.easv;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

public class PixelCounter implements Runnable, Callable<Result> {
    private final Image img;
    private final int width;
    private final int height;
    private int colorGreen = 0,  colorRed = 0,  colorBlue = 0, mixed = 0;

    public PixelCounter(Image img, int width, int height) {
        this.img = img;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result call() {
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                Color color = img.getPixelReader().getColor(i,j);
                double green = color.getGreen();
                double blue = color.getBlue();
                double red = color.getRed();

                if (red > blue && red > green) {
                    colorRed++;
                } else if (blue > red && blue > green) {
                    colorBlue++;
                } else if (green > red && green > blue) {
                    colorGreen++;
                } else {
                    mixed++;
                }
            }
        }

        return new Result(colorGreen,colorRed,colorBlue,mixed);
    }
}
