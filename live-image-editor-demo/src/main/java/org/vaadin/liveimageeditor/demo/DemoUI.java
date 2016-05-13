package org.vaadin.liveimageeditor.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.vaadin.liveimageeditor.LiveImageEditor;

import javax.servlet.annotation.WebServlet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    private Button send = new Button("Send", this::sendClicked);
    private Image editedImage = new Image();
    private ByteArrayOutputStream outputStream;

    @Override
    protected void init(VaadinRequest request) {
        Upload upload = new Upload(null, this::receiveUpload);
        upload.setImmediate(true);
        upload.addSucceededListener(this::uploadSucceeded);

        send.setVisible(false);
        editedImage.setVisible(false);
        imageEditor.setWidth(600, Unit.PIXELS);
        imageEditor.setTranslateX(.5);
        imageEditor.setTranslateY(.25);
        imageEditor.setRotate(0.7);
        imageEditor.setScale(2.0);

        VerticalLayout layout = new VerticalLayout(upload, imageEditor, send, editedImage);
        layout.setSizeUndefined();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    private OutputStream receiveUpload(String filename, String mimeType) {
        return outputStream = new ByteArrayOutputStream();
    }

    private void uploadSucceeded(Upload.SucceededEvent event) {
        imageEditor.setImage(outputStream.toByteArray());
        send.setVisible(true);
    }

    private void sendClicked(Button.ClickEvent event) {
        imageEditor.requestEditedImage();
    }

    private void receiveImage(InputStream inputStream) {
        StreamResource resource = new StreamResource(() -> inputStream, "edited-image-" + System.currentTimeMillis());
        this.editedImage.setSource(resource);
        this.editedImage.setVisible(true);
    }

}
