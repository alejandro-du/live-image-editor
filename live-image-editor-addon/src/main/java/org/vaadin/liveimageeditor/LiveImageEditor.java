package org.vaadin.liveimageeditor;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import org.vaadin.liveimageeditor.client.LiveImageEditorState;

@JavaScript({"jquery-1.12.3.min.js", "jquery.mousewheel.js", "live-image-editor.js"})
@StyleSheet("live-image-editor.css")
public class LiveImageEditor extends AbstractJavaScriptComponent {

    @Override
    public LiveImageEditorState getState() {
        return (LiveImageEditorState) super.getState();
    }

}
