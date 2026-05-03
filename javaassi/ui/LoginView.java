package com.example.javaassi.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.BiConsumer;
import com.example.javaassi.util.StyleUtil;

public class LoginView {

    public static void show(Stage stage, BiConsumer<String, String> onLogin) {
        // 1. Main layout with background image
        StackPane root = new StackPane();
        root.setStyle(
                "-fx-background-image: url('/logg3.jpg');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;"
        );

        // 2. The Glass Box styling you found
        VBox glassBox = new VBox(20);
        glassBox.setPadding(new Insets(40));
        glassBox.setAlignment(Pos.CENTER);
        glassBox.setMaxWidth(420);
        glassBox.setStyle(
                "-fx-background-color: rgba(245, 245, 245, 0.3);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 12, 0, 0, 6);"
        );

        // 3. Label with the Verdana font and shadow
        Label titleLabel = new Label("Welcome to HRX");
        titleLabel.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-family: 'Verdana';" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(one-pass-box, rgba(0, 0, 0, 0.7), 3, 0, 0, 3);"
        );

        TextField usernameField = new TextField();
        usernameField.setPromptText("Employee ID");
        usernameField.setStyle(StyleUtil.textFieldStyle()); // Keeping your helper class

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(StyleUtil.textFieldStyle());

        // 4. Button with the hover effects
        Button loginButton = new Button("Login");
        loginButton.setStyle(StyleUtil.loginButtonStyle());

        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(
                    "-fx-background-color: #8e24aa;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 16px;" +
                            "-fx-font-family: 'Verdana';" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10 20;" +
                            "-fx-background-radius: 20;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.4), 6, 0, 0, 3);"
            );
        });

        // Reset style on mouse exit so it doesn't stay purple
        loginButton.setOnMouseExited(e -> loginButton.setStyle(StyleUtil.loginButtonStyle()));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            onLogin.accept(username, password); // This triggers the logic in MainApp
        });

        // 5. Assembly
        glassBox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);
        root.getChildren().add(glassBox);

        Scene scene = new Scene(root, 800, 650);
        stage.setScene(scene);
        stage.setTitle("HRX Login");
        stage.show();
    }
}

