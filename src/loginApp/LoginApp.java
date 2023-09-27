
package loginApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;

import java.sql.Timestamp;
import java.util.Date;


import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import static javafx.application.Application.launch;
import javax.swing.JOptionPane;

public class LoginApp extends Application {

	//directory of the text files, same with directory of this class
	private final static String FILE_LOCATION = "/Users/ameliablack/NetBeansProjects/loginApp";
	// counter for login attempts
	private int attempts = 0;
        
        
        

	private final Date date = new Date();

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Login");
		// Grid Pane divides your window into grids
		GridPane grid = new GridPane();
		// Align to Center
		// Note Position is geometric object for alignment
		grid.setAlignment(Pos.CENTER);
		// Set gap between the components
		// Larger numbers mean bigger spaces
		grid.setHgap(10);
		grid.setVgap(10);
		// Create some text to place in the scene
		Text scenetitle = new Text("Welcome. Login to continue.");
		// Add text to grid 0,0 span 2 columns, 1 row
		grid.add(scenetitle, 0, 0, 2, 1);
		// Create Label
		Label userName = new Label("Username:");
		// Add label to grid 0,1
		grid.add(userName, 0, 1);
		// Create Textfield
		TextField userTextField = new TextField();
		// Add textfield to grid 1,1
		grid.add(userTextField, 1, 1);
		// Create Label
		Label pw = new Label("Password:");
		// Add label to grid 0,2
		grid.add(pw, 0, 2);
		// Create Passwordfield
		PasswordField pwBox = new PasswordField();
		// Add Password field to grid 1,2
		grid.add(pwBox, 1, 2);
		// Create Login Button
                Label pin = new Label("Pin:");
		// Add label to grid 0,2
		grid.add(pin, 0, 3);
		// Create Passwordfield
		PasswordField pinBox = new PasswordField();
		// Add Password field to grid 1,2
		grid.add(pinBox, 1, 3);
		Button btn = new Button("Login");
		// Add button to grid 1,4
		grid.add(btn, 1, 5);
		final Text actiontarget = new Text();
		grid.add(actiontarget, 1, 6);

		// Set the Action when button is clicked
		btn.setOnAction((ActionEvent e) -> {
			// Authenticate the user
			// If isValid is valid clear the grid and Welcome the user
			boolean isValid = authenticate(userTextField.getText().trim(), pwBox.getText().trim());
			if (isValid) {
				grid.setVisible(false);
				GridPane grid2 = new GridPane();
				// Align to Center
				// Note Position is geometric object for alignment
				grid2.setAlignment(Pos.CENTER);
				// Set gap between the components
				// Larger numbers mean bigger spaces
				grid2.setHgap(10);
				grid2.setVgap(10);
				// AC-8 System use notification security and privacy notices
				Text sceneText = new Text(50, 50, "Welcome " + userTextField.getText() + "! "
                                        + "\nYou are accessing a COMPANY Information System (IS) that is provided for COMPANY-authorized use only."
                                        + "\nThis IS may contain federal contract information and controlled unclassified information."
                                        + "\nBy using this IS (which includes any device attached to this IS), you consent to the following conditions: "
                                        + "\n- COMPANY routinely intercepts and monitors communications on this IS "
                                        + "\n- At any time, COMPANY may inspect and seize data stored on this IS. "
                                        + "\n- Communications using, or data stored on, this IS are not private, are subject to routine monitoring, interception, "
                                        + "\n   and search, and may be disclosed or used for any COMPANY authorized purpose. "
                                        + "\n- This IS includes security measures (e.g., authentication and access controls) "
                                        + "\n   to protect COMPANY interests, not for your personal benefit or privacy.");
				// Add text to grid 0,0 span 2 columns, 1 row
				grid2.add(sceneText, 0, 0, 2, 1);
				// grid2.add(sysUseNotification, 0, 0, 2, 1);
				Scene scene = new Scene(grid2, 500, 400);
				primaryStage.setScene(scene);
				primaryStage.show();
			} else {
				final Text actiontarget1 = new Text();
				grid.add(actiontarget1, 1, 6);
				actiontarget1.setFill(Color.FIREBRICK);
				actiontarget1.setText("Please try again.");
				// attempts added after each unsuccessful login
				attempts++;
				System.out.println(attempts);
				// AC-7 AC-12 The information automatically closes the session after 3 failed
				// login attempts
				if (attempts == 3) {
					JOptionPane.showMessageDialog(null, //
							"You have exceeded the limit of unsuccessful login attempts, please try again another time.");
					// AU-3 AU-8 event logged to auditlog

					String log = "Event: Login failure limit exceeded. " + "User: Unknown" //
							+ " Date and time: " + new Timestamp(date.getTime());
					try {
						auditText(log);
					}

					// catch(Exception e3) {
					catch (ClassNotFoundException e3) {
					}

					Platform.exit();
					System.exit(0);
				}
			}
		});

		// Set the size of Scene
		Scene scene = new Scene(grid, 500, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void auditText(String text) throws ClassNotFoundException {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(FILE_LOCATION + "auditlog.txt", true));
			// create object
			bw.write(text); // write audit text into file
			bw.newLine(); // new line after text
			bw.flush(); // flush after audit text
		} catch (IOException e) {
		}

		finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e2) {
				}
		}
	}

	/**
	 * @param username
	 * @param password
	 * @return isValid true for authenticated the login information is stored
	 *         outside of the application in txt files referenced to authenticate
	 *         users if the login information is accurate the boolean is returned as
	 *         true
	 */
	public static boolean authenticate(String username, String password) {
		boolean matched = false;
		// Scanner objects for verifying login information
		File unameFile = null;
		File pwordFile = null;
		Scanner scanUname = null;
		Scanner scanPword = null;
		// date object for audit log
		Date date = new Date();
		// IA-2 usernames.txt and passwords.txt uniquely identifies and authenticates
		// all users of the application
		try {
                    unameFile = new File(FILE_LOCATION + "usernames.txt");
                    pwordFile = new File(FILE_LOCATION + "passwords.txt");
                    scanUname = new Scanner(unameFile);
                    scanPword = new Scanner(pwordFile);
		} catch (FileNotFoundException e) {
                    System.out.println("Not able to find file");
                    System.exit(0);
		}

		
		//will hold all the names in the usernames.txt
		List<String> nameList = new ArrayList<>();
		
		//read the usernames.txt per line
		while (scanUname.hasNextLine()) {
			//split each line of name by comma delimiter
			String[] unames = scanUname.nextLine().split(",");
			
			//add each names in the nameList
			for (String n : unames) {
				nameList.add(n.trim());
			}
		}

		//will hold all the passwords in passwords.txt
		List<String> pwList = new ArrayList<>();
		
		//read the passwords.txt per line
		while (scanPword.hasNextLine()) {
			//split each line by comma delimeter
			String[] pws = scanPword.nextLine().split(",");
			//add each password in the pwList
			for (String pw : pws) {
				pwList.add(pw.trim());
			}
		}
		
		//will hold the index of the name if found in the nameList
		int index = 0;
		if (!nameList.isEmpty() && !pwList.isEmpty()) {
			
			// loop in the nameList
			for (int i = 0; i < nameList.size(); i++) {
				//check if username is will match in the nameList
				if (username.equalsIgnoreCase(nameList.get(i))) {
					//pass the index
					index = i;
				}
			}
			
			//check if in the pwList, index is not null 
			if (pwList.get(index) != null) {
				// match the password in the pwList index
				if (password.equals(pwList.get(index))) {
					matched = true;
				}
			}
		}
		String log = "";
                
		if (matched) {
                    
                    
                    
			log = " Time stamp: " + new Timestamp(date.getTime()) +
                                "\n---> Successful Login. " + "User: " + username;
		} else {
                    
			log = " Time stamp: " + new Timestamp(date.getTime()) 
                                + "\n---> Login failure. " + "User: " + username;
		
                }

		try {
			auditText(log);
		} catch (ClassNotFoundException e) {
		}

		return matched;
	}
}
