window.org_vaadin_liveimageeditor_LiveImageEditor = function() {
    var tx = 0;
    var ty = 0;
    var r = 0;
    var s = 1;

    var image;
    var editor;

    var mouseDown = false;
    var px;
    var py;

    var connector = this;
    var elem = $(connector.getElement());

    connector.setImageUrl = function(imageUrl) {
        var state = connector.getState();
        var imageUrl = connector.translateVaadinUri(imageUrl) + "?" + Math.random();
        var imageEditorId = "image-editor-" + connector.getConnectorId();
        var imageId = "image-" + connector.getConnectorId();

        elem.html('<div id="' + imageEditorId + '" class="image-editor"><div class="crop-border"><img id="' + imageId + '" src="' + imageUrl + '" /><div class="grid"><div class="horizontal-grid"></div><div class="vertical-grid"></div></div></div></div>');

        image = $("#" + imageId);
        editor = $("#" + imageEditorId);

        image.width(state.width);
        updateImage();

        image.mousedown(function(event) {
            event.preventDefault();
        });

        editor.mousedown(function(event) {
            mouseDown = true;
            px = event.pageX;
            py = event.pageY;
            editor.addClass("mouse-down");
        });

        editor.mouseup(function() {
            mouseDown = false;
            editor.removeClass("mouse-down");
        });

        editor.mouseleave(function() {
            mouseDown = false;
            editor.removeClass("mouse-down");
        });

        editor.mousemove(function(event) {
            if (mouseDown) {
                if (event.shiftKey) {
                    var bounds = image[0].getBoundingClientRect();
                    var cx = bounds.left + (bounds.width / 2);
                    var cy = bounds.top + (bounds.height / 2);
                    var r1 = Math.atan2(px - cx, py - cy);
                    var r2 = Math.atan2(event.pageX - cx, event.pageY - cy);
                    r += r1 - r2;

                } else {
                    tx += (event.pageX - px) / editor.width();
                    ty += (event.pageY - py) / editor.height();
                }

                px = event.pageX;
                py = event.pageY;
                updateImage();
            }
        });

        editor.mousewheel(function(event, delta) {
            s += 0.06 * delta;
            updateImage();
            event.preventDefault();
        });
    }

    connector.setTranslateX = function(translateX) {
        tx = translateX;
        updateImage();
    }

    connector.setTranslateY = function(translateY) {
        ty = translateY;
        updateImage();
    }

    connector.setRotate = function(rotate) {
        r = rotate;
        updateImage();
    }

    connector.setScale = function(scale) {
        s = scale;
        updateImage();
    }

    connector.onRequestServerStateUpdate = function() {
        connector.updateServerState(tx, ty, r, s, editor.width(), editor.height());
    }

    function updateImage() {
        image.css("transform", "translate(" + (tx * editor.width()) + "px, " + (ty * editor.height()) + "px) rotate(" + r + "rad) scale(" + s + ")");
    }

}