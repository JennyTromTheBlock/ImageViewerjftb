package dk.easv;

public class Result {
    private int colorGreen = 0;
    private int colorRed = 0;
    private int colorBlue = 0;
    private int mixed = 0;

    public Result(int colorGreen, int colorRed, int colorBlå, int mixed) {
        this.colorGreen = colorGreen;
        this.colorRed = colorRed;
        this.colorBlue = colorBlå;
        this.mixed = mixed;
    }

    public int getColorGreen() {
        return colorGreen;
    }
    public int getColorRed() {
        return colorRed;
    }

    public int getColorBlå() {
        return colorBlue;
    }

    public int getMixed() {
        return mixed;
    }

}
