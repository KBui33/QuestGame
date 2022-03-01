module gui {
    requires javafx.controls;
    requires java.datatransfer;
    requires java.desktop;
    opens gui.main;
    exports gui.main;
}