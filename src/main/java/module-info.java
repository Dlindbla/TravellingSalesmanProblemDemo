module com.dlindbla.travellingsalesmanproblemdemo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.dlindbla.travellingsalesmanproblemdemo to javafx.fxml;
    exports com.dlindbla.travellingsalesmanproblemdemo;
}