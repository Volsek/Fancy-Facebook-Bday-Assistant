import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("FBA");


        TextField Email = new TextField();
        Email.setPromptText("Your Email");

        PasswordField Password = new PasswordField();
        Password.setPromptText("Your Password");

        EventHandler<ActionEvent> ButtonClick = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    run(Email,Password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Button Run_Button = new Button();
        Run_Button.setText("Run");
        Run_Button.setOnAction(ButtonClick);

        VBox vert = new VBox();
        vert.setAlignment(Pos.CENTER);

        HBox email_row = new HBox();
        email_row.getChildren().add(Email);
        email_row.setAlignment(Pos.CENTER);
        vert.getChildren().add(email_row);

        HBox password_row = new HBox();
        password_row.getChildren().add(Password);
        password_row.setAlignment(Pos.CENTER);
        vert.getChildren().add(password_row);

        vert.getChildren().add(Run_Button);
        vert.setSpacing(10);

        primaryStage.setScene(new Scene(vert,250,250));
        primaryStage.setResizable(false);
        primaryStage.show();


    }
    private static void run(TextField Email, PasswordField password ) throws IOException {
        Email.setStyle(null);
        password.setStyle(null);
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("$dUFy+jf2L2wZ7&m");
        Properties settings = new EncryptableProperties(encryptor);
        if (!Email.getText().equals("") && !password.getText().equals("")){
            OutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + "\\settings.properties");

            settings.setProperty("Email", encryptor.encrypt(Email.getText()));
            settings.setProperty("Password", encryptor.encrypt(password.getText()));
            settings.store(outputStream, null);

            Platform.exit();
        }else{
            if (Email.getText().equals("")){
                Email.setStyle("-fx-background-color: firebrick");
            }
            if (password.getText().equals("")){
                password.setStyle("-fx-background-color: firebrick");
            }


        }
    }
}
