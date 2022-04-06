// Only to allow gui to be run through intellij play button
// can also be run using `mvn clean javafx:run` in terminal
module gui {
    requires javafx.controls;
    requires javafx.media;
    requires json.simple;
    requires org.apache.commons.lang3;
    opens gui.main;
    exports gui.main;
}