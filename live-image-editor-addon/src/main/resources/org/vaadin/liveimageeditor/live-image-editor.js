window.org_vaadin_liveimageeditor_LiveImageEditor = function() {
    var tx = 0;
    var ty = 0;
    var r = 0;
    var s = 1;

    var image;
    var crop;

    var mouseDown = false;
    var px;
    var py;

    var connector = this;
    var elem = $(connector.getElement());

    connector.setImageUrl = function(imageUrl) {
        var state = connector.getState();
        var imageUrl = connector.translateVaadinUri(imageUrl) + "?" + Math.random();
        cropBorderId = "cropBorder-" + connector.getConnectorId();
        imageId = "image-" + connector.getConnectorId();
        elem.html('<div id="' + cropBorderId + '" class="crop-border"><img id="' + imageId + '" src="' + imageUrl + '" /></div>');

        image = $("#" + imageId);
        crop = $("#" + cropBorderId);

        image.width(state.width);
        updateImage();

        image.mousedown(function(event) {
            event.preventDefault();
        });

        crop.mousedown(function(event) {
            mouseDown = true;
            px = event.pageX;
            py = event.pageY;
            crop.addClass("mouse-down");
        });

        crop.mouseup(function() {
            mouseDown = false;
            crop.removeClass("mouse-down");
        });

        crop.mouseout(function() {
            mouseDown = false;
            crop.removeClass("mouse-down");
        });

        crop.mousemove(function(event) {
            if (mouseDown) {
                if (event.shiftKey) {
                    var bounds = image[0].getBoundingClientRect();
                    var cx = bounds.left + (bounds.width / 2);
                    var cy = bounds.top + (bounds.height / 2);
                    var r1 = Math.atan2(px - cx, py - cy);
                    var r2 = Math.atan2(event.pageX - cx, event.pageY - cy);
                    r += r1 - r2;

                } else {
                    tx += (event.pageX - px) / crop.width();
                    ty += (event.pageY - py) / crop.height();
                }

                px = event.pageX;
                py = event.pageY;
                updateImage();
            }
        });

        crop.mousewheel(function(event, delta) {
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
        connector.updateServerState(tx, ty, r, s, crop.width(), crop.height());
    }

    function updateImage() {
        image.css("transform", "translate(" + (tx * crop.width()) + "px, " + (ty * crop.height()) + "px) rotate(" + r + "rad) scale(" + s + ")");
    }

}