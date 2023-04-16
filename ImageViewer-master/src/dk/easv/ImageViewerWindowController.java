package dk.easv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController{
    private final List<Image> images = new ArrayList<>();
    @FXML
    private Label lblBlue, lblMixed, lblRed, lblGreen, lblImageName;
    @FXML
    private Button btnStop;
    private int currentImageIndex = 0;
    private boolean stopped;

    @FXML
    private ImageView imageView;
    private ExecutorService es;


    public ImageViewerWindowController() {
        imageLoop();
    }


    @FXML
    private void handleBtnLoadAction() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty()) {
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();
            imageLoop();
        }
    }

    @FXML
    private void handleBtnPreviousAction() throws Exception {
        if (!images.isEmpty()) {
            currentImageIndex = (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction() throws Exception {
        nextImage();
    }

    private void nextImage() throws Exception {
        if (!images.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
            changeFilenameLabel(images.get(currentImageIndex));
        }
    }

    private void countPixels(Image image) throws Exception{

        ExecutorService executor = Executors.newFixedThreadPool(1);
        PixelCounter counter = new PixelCounter(image, (int) image.getWidth(), (int) image.getHeight());

        Future<Result> future = executor.submit((Callable<Result>) counter);

        Result result = future.get();

        int red = result.getColorRed();
        int blue = result.getColorBlå();
        int mixed = result.getMixed();
        int green = result.getColorGreen();
        Platform.runLater(() -> setLabels(blue, red, green, mixed));
    }

    private void setLabels(int blue, int red, int green, int mixed) {
        lblBlue.setText("Blue: " + blue);
        lblRed.setText("Red: " + red);
        lblGreen.setText("Green: " + green);
        lblMixed.setText("Mixed: " + mixed);

    }

    private void displayImage() throws Exception {
        if (!images.isEmpty()) {
            imageView.setImage(images.get(currentImageIndex));
            countPixels(images.get(currentImageIndex));
        }
    }

    Runnable runnable = () -> {
        while (!images.isEmpty()) {
            try {
                nextImage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private void changeFilenameLabel(Image image) {
        String imageURL = image.getUrl();
        String imageName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
        Platform.runLater(() -> lblImageName.setText(imageName));
    }

    private void imageLoop() {
        es = Executors.newFixedThreadPool(1);
        es.submit(runnable);
    }

    @FXML
    private void handleStop() {
        if (stopped) {
            startShow();
        } else {
            stopShow();
        }
    }

    private void startShow() {
        imageLoop();
        stopped = false;
        btnStop.setText("STOP");
    }

    private void stopShow() {
        es.shutdownNow();
        stopped = true;
        btnStop.setText("START");
    }
}