package com.vm.ui;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * Login Controller.
 */
public class LoginController extends AnchorPane implements Initializable {

	@FXML
	Label userIdL;
	
	@FXML
	Label passwordL;
	
	@FXML
	TextField userIp;
    @FXML
    TextField userId;
    @FXML
    PasswordField password;
    @FXML
    Button login;
    @FXML
    Label errorMessage;
    
    private Login application;
    
    
    public void setApp(Login application){
        this.application = application;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setText("");
        login.setText("登录");
        userIdL.setText("用户名");
        passwordL.setText("密码");
        userIp.setText("192.168.1.124");
        userId.setText("root");
        password.setText("123456");
        
    }
  
    public void processLogin(ActionEvent event) {
        if (application == null){
            // We are running in isolated FXML, possibly in Scene Builder.
            // NO-OP.
            errorMessage.setText("Hello " + userId.getText());
        } else {
            if (!application.userLogging(userIp.getText(), userId.getText(), password.getText())){
            	errorMessage.setTextFill(Color.RED);
                errorMessage.setText("IP/用户名/密码错误");
            }
        }
    }
}