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

    connector.onStateChange = function() {
        var state = connector.getState();

        if (state.imageUrl) {
            var imageUrl = connector.translateVaadinUri(state.imageUrl) + "?" + Math.random();
            elem.html('<div id="cropBorder" class="crop-border"><img id="image" src="' + imageUrl + '" /></div>');

            image = $("#image");
            crop = $("#cropBorder");

            tx = state.translateX;
            ty = state.translateY;
            r = state.rotate;
            s = state.scale;

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
                        tx += event.pageX - px;
                        ty += event.pageY - py;
                    }

                    px = event.pageX;
                    py = event.pageY;
                    updateImage();
                }
            });

            crop.mousewheel(function(event, delta) {
                s += 0.06 * delta;
                updateImage();
            });
        }
    }

    function updateImage() {
        image.css("transform", "translate(" + tx + "px, " + ty + "px) rotate(" + r + "rad) scale(" + s + ")");
    }

}