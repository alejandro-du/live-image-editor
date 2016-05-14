package org.vaadin.liveimageeditor.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.liveimageeditor.LiveImageEditor;

import javax.servlet.annotation.WebServlet;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Theme("valo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    private LiveImageEditor imageEditor = new LiveImageEditor(this::receiveImage);

    private Upload upload = new Upload("Upload an image", this::receiveUpload);
    private Label instructions = new Label("<b>Drag</b> to move the image. Press <b>SHIFT</b> while dragging to rotate. Use the <b>mouse wheel</b> to scale. Click <b>Send</b> to transform the image on the server.");
    private Label result = new Label("Transformed image as received on the server:");
    private Button send = new Button("Send", this::sendClicked);
    private Image editedImage = new Image();
    private ByteArrayOutputStream outputStream;

    @Override
    protected void init(VaadinRequest request) {
        Label title = new Label("Live Image Editor add-on for Vaadin");
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_COLORED);

        instructions.setContentMode(ContentMode.HTML);
        instructions.setWidth(600, Unit.PIXELS);

        upload.setImmediate(true);
        upload.addSucceededListener(this::uploadSucceeded);

        imageEditor.setWidth(600, Unit.PIXELS);
        imageEditor.setBackgroundColor(0, 52, 220);

        VerticalLayout layout = new VerticalLayout(title, upload, instructions, imageEditor, send, result, editedImage);
        layout.setSizeUndefined();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        setupUploadStep();
    }

    private OutputStream receiveUpload(String filename, String mimeType) {
        return outputStream = new ByteArrayOutputStream();
    }

    private void uploadSucceeded(Upload.SucceededEvent event) {
        imageEditor.setImage(outputStream.toByteArray());
        imageEditor.resetTransformations();
        setupEditingStep();
    }

    private void sendClicked(Button.ClickEvent event) {
        imageEditor.requestEditedImage();
    }

    private void receiveImage(InputStream inputStream) {
        StreamResource resource = new StreamResource(() -> inputStream, "edited-image-" + System.currentTimeMillis());
        editedImage.setSource(resource);
        setupFinalStep();
    }

    private void setupUploadStep() {
        upload.setVisible(true);
        instructions.setVisible(false);
        imageEditor.setVisible(false);
        send.setVisible(false);
        result.setVisible(false);
        editedImage.setVisible(false);
    }

    private void setupEditingStep() {
        upload.setVisible(false);
        instructions.setVisible(true);
        imageEditor.setVisible(true);
        send.setVisible(true);
        result.setVisible(false);
        editedImage.setVisible(false);
    }

    private void setupFinalStep() {
        upload.setVisible(true);
        instructions.setVisible(false);
        imageEditor.setVisible(false);
        send.setVisible(false);
        result.setVisible(true);
        editedImage.setVisible(true);
    }

}
