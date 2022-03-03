// Only to allow gui to be run through intellij play button
// can also be run using `mvn clean javafx:run` in terminal
module gui {
    requires javafx.controls;
    opens gui.main;
    exports gui.main;
}