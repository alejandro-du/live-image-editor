package org.vaadin.liveimageeditor.demo;

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
import java.io.OutputStream;

@Theme("demo")
@Title("MyComponent Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.liveimageeditor.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private LiveImageEditor imageEditor = new LiveImageEditor();
    private Button send = new Button("Send", this::sendClicked);
    private ByteArrayOutputStream outputStream;

    @Override
    protected void init(VaadinRequest request) {
        Upload upload = new Upload("Upload image file", this::receiveUpload);
        upload.setImmediate(true);
        upload.addSucceededListener(this::uploadSucceeded);

        send.setVisible(false);

        VerticalLayout layout = new VerticalLayout(upload, imageEditor, send);
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }

    private OutputStream receiveUpload(String filename, String mimeType) {
        return outputStream = new ByteArrayOutputStream();
    }

    private void uploadSucceeded(Upload.SucceededEvent event) {
        imageEditor.setImage(new ByteArrayInputStream(outputStream.toByteArray()));
        send.setVisible(true);
    }

    private void sendClicked(Button.ClickEvent event) {
        OutputStream image = imageEditor.getEditedImage();
    }

}
