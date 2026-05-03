package com.example.javaassi.util;

public class StyleUtil {
    public static String textFieldStyle() {
        return "-fx-font-size: 15px;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: #ffffff;" +
                "-fx-border-width: 1;" +
                "-fx-padding: 10;" +
                "-fx-background-color: rgba(255,255,255,0.8);" +
                "-fx-text-fill: #2c3e50;" +
                "-fx-font-family: 'Verdana';";
    }

    public static String loginButtonStyle() {
        return "-fx-background-color: #6a1b9a;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-family: 'Verdana';" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 2);";
    }
}
