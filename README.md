[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/live-image-editor)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/live-image-editor.svg)](https://vaadin.com/directory/component/live-image-editor)
[![Latest version on vaadin.com/directory](https://img.shields.io/vaadin-directory/v/live-image-editor.svg)](https://img.shields.io/vaadin-directory/v/live-image-editor.svg)

# Live Image Editor Add-on for Vaadin

An image editor component that allows users editing images on the client side. Editing is done by performing
transformations over the original image on the client side. No requests to the server are done while manipulating
the image.

![Alt text](/screenshot-01.png?raw=true "Screenshot")

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to http://vaadin.com/addon/live-image-editor

## Building and running the demo

git clone https://github.com/alejandro-du/live-image-editor.git
mvn clean install
cd live-image-editor-demo
mvn jetty:run

To see the demo, navigate to http://localhost:8080/

## License

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.
