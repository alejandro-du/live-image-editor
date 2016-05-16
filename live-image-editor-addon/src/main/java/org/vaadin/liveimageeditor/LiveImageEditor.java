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

/**
 * <p>An image editor component that allows users editing images on the client side. Editing is done by performing
 * transformations over the original image on the client side. No requests to the server are done while manipulating
 * the image. To get the transformed image on the server side, you must request it by calling the
 * {@link #requestEditedImage()} method.</p>
 * <p>Final users can edit the image by using mouse actions. Crop is done against a fixed crop border by dragging
 * the image. Dragging while keeping the SHIFT key down, rotates the image. Scaling is done by using the mouse wheel.
 * </p>
 * <p>The image to be edited can be set by calling the {@link #setImage(byte[])} method.</p>
 * <p>The edited image can be obtainded by calling the {@link #requestEditedImage()} and using the
 * {@link ImageReceiver} instance passed during construction.</p>
 * <p>The background color can be configured using the {@link #setBackgroundColor(int, int, int)} method.</p>
 *
 * @author Alejandro Duarte.
 */
@JavaScript({"jquery-1.12.3.min.js", "jquery.mousewheel.js", "live-image-editor.js"})
@StyleSheet("live-image-editor.css")
public class LiveImageEditor extends AbstractJavaScriptComponent {

    public static interface ImageReceiver {

        void receiveImage(InputStream inputStream);

    }

    private static final String IMAGE_KEY = "image-key";

    private byte[] imageData;

    private ImageReceiver imageReceiver;

    private Double translateX = 0.0;

    private Double translateY = 0.0;

    private Double rotate = 0.0;

    private Double scale = 1.0;

    private Integer red = 255;

    private Integer green = 255;

    private Integer blue = 255;

    private Double cropWidth = null;

    private Double cropHeight = null;

    /**
     * Constructs a new editor that will send the edited image to the specified {@link ImageReceiver} after the
     * {@link #requestEditedImage()} method is called.
     *
     * @param imageReceiver The {@link ImageReceiver} used to return the edited image. The
     *                      {@link ImageReceiver#receiveImage(InputStream)} will be called after it is requested via the
     *                      {@link #requestEditedImage()} method.
     */
    public LiveImageEditor(ImageReceiver imageReceiver) {
        this.imageReceiver = imageReceiver;
        setWidth(100, Unit.PERCENTAGE);

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

    /**
     * Sets the image to be edited.
     */
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
        callFunction("setBackgroundColor", red, green, blue);
    }

    private String getResourceUrl(String key) {
        URLReference urlReference = getState().resources.get(key);
        if (urlReference == null) {
            return null;
        } else {
            return urlReference.getURL();
        }
    }

    /**
     * Sets the translation on the X axis as a percentage of the editor width.
     *
     * @param translateX translation amount as a percentage of the editor width. For example, a value of 0.5 would
     *                   make the left border of the image to be placed on the horizontal center of the editor.
     */
    public void setTranslateX(Double translateX) {
        this.translateX = translateX;
        callFunction("setTranslateX", translateX);
    }

    /**
     * @return The translation on the X axis as a percentage of the editor width.
     */
    public Double getTranslateX() {
        return translateX;
    }

    /**
     * Sets the translation on the Y axis as a percentage of the editor height.
     *
     * @param translateY translation amount as a percentage of the editor height. For example, a value of 0.5 would
     *                   make the top border of the image to be placed on the vertical center of the editor.
     */
    public void setTranslateY(Double translateY) {
        this.translateY = translateY;
        callFunction("setTranslateY", translateY);
    }

    /**
     * @return The translation on the Y axis as a percentage of the editor height.
     */
    public Double getTranslateY() {
        return translateY;
    }

    /**
     * Sets the rotation value.
     *
     * @param rotate the rotation value in radians.
     */
    public void setRotate(Double rotate) {
        this.rotate = rotate;
        callFunction("setRotate", rotate);
    }

    /**
     * @return The rotation value in radians.
     */
    public Double getRotate() {
        return rotate;
    }

    /**
     * @param scale Sets the scale factor. For example, 1.0 won't scale the original image, whilst 2.0 would make it
     *              twice its original size.
     */
    public void setScale(Double scale) {
        this.scale = scale;
        callFunction("setScale", scale);
    }

    /**
     * @return The scale factor.
     */
    public Double getScale() {
        return scale;
    }

    /**
     * Sets the color to be used as a background of the image.
     */
    public void setBackgroundColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        callFunction("setBackgroundColor", red, green, blue);
    }

    /**
     * Sets the transformation values so that the original image won't be changed.
     */
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
        g2d.setPaint(new Color(red, green, blue));
        g2d.fillRect(0, 0, destWidth, destHeight);
        g2d.drawImage(src, transform, null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(dest, "png", outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * Requests the edited or transformed image. Once the request is made, the transformed image is sent to the
     * {@link #imageReceiver} instance specified in the constructor.
     */
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
        imageReceiver.receiveImage(inputStream);
    }

}
