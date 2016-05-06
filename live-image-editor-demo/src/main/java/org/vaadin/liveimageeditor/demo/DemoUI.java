package org.vaadin.liveimageeditor.demo;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import org.vaadin.liveimageeditor.LiveImageEditor;
import org.vaadin.liveimageeditor.MyComponent;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Theme("demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.liveimageeditor.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private LiveImageEditor imageEditor = new LiveImageEditor();
    private Button send = new Button("Send", this::sendClicked);
    private Image image = new Image();
    private ByteArrayOutputStream outputStream;

    @Override
    protected void init(VaadinRequest request) {
        Upload upload = new Upload(null, this::receiveUpload);
        upload.setImmediate(true);
        upload.addSucceededListener(this::uploadSucceeded);

        send.setVisible(false);
        image.setVisible(false);
        imageEditor.setWidth(600, Unit.PIXELS);
        imageEditor.setHeight(400, Unit.PIXELS);

        VerticalLayout layout = new VerticalLayout(upload, imageEditor, send, image);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    private OutputStream receiveUpload(String filename, String mimeType) {
        return outputStream = new ByteArrayOutputStream();
    }

    private void uploadSucceeded(Upload.SucceededEvent event) {
        imageEditor.setImage(new ByteArrayInputStream(outputStream.toByteArray()));
        imageEditor.setTranslateX(50.0);
        imageEditor.setTranslateY(25.0);
        imageEditor.setScale(2.0);
        imageEditor.setRotate(0.78);
        send.setVisible(true);
    }

    private void sendClicked(Button.ClickEvent event) {
        InputStream editedImage = imageEditor.getEditedImage();
        StreamResource resource = new StreamResource(() -> editedImage, "edited-image");
        image.setSource(resource);
        image.setVisible(true);
    }

}
