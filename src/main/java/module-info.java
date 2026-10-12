module sk.koshi.elevator {
    requires javafx.controls;
    requires javafx.fxml;

    opens sk.koshi.elevator to javafx.fxml;
    exports sk.koshi.elevator;
}
