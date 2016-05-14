package org.vaadin.liveimageeditor;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@JavaScript({"jquery-1.12.3.min.js", "jquery.mousewheel.js", "live-image-editor.js"})
@StyleSheet("live-image-editor.css")
public class LiveImageEditor extends AbstractJavaScriptComponent {

    public static interface ImageReceiver {

        void recieveImage(InputStream inputStream);

    }

    private static final String IMAGE_KEY = "image-key";

    private byte[] imageData;

    private ImageReceiver imageReceiver;

    private Double translateX = 0.0;

    private Double translateY = 0.0;

    private Double rotate = 0.0;

    private Double scale = 1.0;

    private Double cropWidth = null;

    private Double cropHeight = null;

    public LiveImageEditor(ImageReceiver imageReceiver) {
        this.imageReceiver = imageReceiver;

        addFunction("updateServerState", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                try {
                    updateServerState(arguments);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setImage(final byte[] imageData) {
        this.imageData = imageData;
        String imageFileName = "image-file-name-" + getConnectorId();
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                return new ByteArrayInputStream(imageData);
            }
        }, imageFileName);

        setResource(IMAGE_KEY, resource);
        String imageUrl = getResourceUrl(IMAGE_KEY);
        callFunction("setImageUrl", imageUrl);
    }

    private String getResourceUrl(String key) {
        URLReference urlReference = getState().resources.get(key);
        if (urlReference == null) {
            return null;
        } else {
            return urlReference.getURL();
        }
    }

    public void setTranslateX(Double translateX) {
        this.translateX = translateX;
        callFunction("setTranslateX", translateX);
    }

    public Double getTranslateX() {
        return translateX;
    }

    public void setTranslateY(Double translateY) {
        this.translateY = translateY;
        callFunction("setTranslateY", translateY);
    }

    public Double getTranslateY() {
        return translateY;
    }

    public void setRotate(Double rotate) {
        this.rotate = rotate;
        callFunction("setRotate", rotate);
    }

    public Double getRotate() {
        return rotate;
    }

    public void setScale(Double scale) {
        this.scale = scale;
        callFunction("setScale", scale);
    }

    public Double getScale() {
        return scale;
    }

    public void resetTransformations() {
        setTranslateX(.0);
        setTranslateY(.0);
        setRotate(.0);
        setScale(1.0);
    }

    private InputStream transformImage() throws IOException {
        BufferedImage src = ImageIO.read(new ByteArrayInputStream(imageData));

        double aspectRatio = cropWidth / cropHeight;
        int destWidth = src.getWidth();
        int destHeight = (int) (src.getWidth() / aspectRatio);

        BufferedImage dest = new BufferedImage(destWidth, destHeight, src.getType());

        int halfW = src.getWidth() / 2;
        int halfH = src.getHeight() / 2;

        AffineTransform restoreCenter = AffineTransform.getTranslateInstance(halfW, halfH);
        AffineTransform rotate = AffineTransform.getRotateInstance(getRotate());
        AffineTransform scale = AffineTransform.getScaleInstance(getScale(), getScale());
        AffineTransform translate = AffineTransform.getTranslateInstance(src.getWidth() * getTranslateX(), src.getWidth() / aspectRatio * getTranslateY());
        AffineTransform translateCenter = AffineTransform.getTranslateInstance(-halfW, -halfH);

        AffineTransform transform = new AffineTransform();
        transform.concatenate(restoreCenter);
        transform.concatenate(translate);
        transform.concatenate(rotate);
        transform.concatenate(scale);
        transform.concatenate(translateCenter);

        AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        operation.filter(src, dest);

        Graphics2D g2d = dest.createGraphics();
        g2d.drawImage(src, transform, null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(dest, "png", outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public void requestEditedImage() {
        callFunction("onRequestServerStateUpdate");
    }

    private void updateServerState(JsonArray arguments) throws IOException {
        setTranslateX(arguments.getNumber(0));
        setTranslateY(arguments.getNumber(1));
        setRotate(arguments.getNumber(2));
        setScale(arguments.getNumber(3));
        cropWidth = arguments.getNumber(4);
        cropHeight = arguments.getNumber(5);

        InputStream inputStream = transformImage();
        imageReceiver.recieveImage(inputStream);
    }

}
