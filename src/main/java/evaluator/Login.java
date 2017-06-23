package evaluator;/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.impl.HttpClientImpl;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Login extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    Scene scene1, scene2;
    private Stage theStage;

    @Override
    public void start(Stage primaryStage) {
        theStage = primaryStage;
        primaryStage.setTitle("JavaFX Welcome");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        //A radio button with an empty string for its label
        CheckBox rb1 = new CheckBox();
        //Setting a text label
        rb1.setText("Is FE here?");
        grid.add(rb1, 1,3);

        Label otpLabel = new Label("OTP:");

        TextField otp = new TextField();

        otp.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    otp.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });


        Button btn = new Button("Sign in & evaluate");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);


        rb1.setOnAction(event -> {
            if(rb1.isSelected()) {
                grid.add(otp, 1, 5);
                grid.add(otpLabel, 0, 5);
            }
            else {
                grid.getChildren().remove(otp);
                grid.getChildren().remove(otpLabel);

            }
        });



        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        AtomicInteger level = new AtomicInteger(0);
        AtomicBoolean submitEnabled = new AtomicBoolean(true);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if(submitEnabled.compareAndSet(true, true)) {
                    actiontarget.setFill(Color.FIREBRICK);
                    String username = userTextField.getText();
                    String password = userTextField.getText();

                    PauseTransition pauseTransition = new PauseTransition(Duration.seconds(10));

                    Vertx vertx = Vertx.vertx();
                    HttpClient client = vertx.createHttpClient();
                    pauseTransition.setOnFinished(event -> {
//                        System.out.println(level + " " + level.compareAndSet(1, 1));
//                        if (level.compareAndSet(1, 1)) {
//                            // theStage.setScene(new ProgressBarComponent().getScene());
//                            System.out.println("Pause transition finished with success");
//
//                        } else {
                            System.out.println("Pause transition finished with failure");
                            theStage.close();
                      //  }
                        submitEnabled.set(true);
                    });
                    pauseTransition.play();
                    if(!rb1.isSelected()) {
                        client.post(8080, "localhost", "/first")
                                .handler(event -> {
                                    level.set(1);
                                    actiontarget.setText(event.statusMessage());
                                }).exceptionHandler(event -> {
                            System.out.println("Handling exceptionally");
                            actiontarget.setText("Some error occurred");

                        }).end(username + "__" +  password + "__" + RequiredSpecs.getSpecs());
                    }else{
                        client.post(8080, "localhost", "/second")
                                .handler(event -> {
                                    level.set(1);
                                    actiontarget.setText(event.statusMessage());
                                }).exceptionHandler(event -> {
                            System.out.println("Handling exceptionally");
                            actiontarget.setText("Some error occurred");

                        }).end(username + "__" + password + "__" + RequiredSpecs.getSpecs() + "__" + otp.getText());
                    }
                    submitEnabled.set(false);
                }
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}