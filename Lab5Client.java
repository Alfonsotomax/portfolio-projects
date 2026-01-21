import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class Lab5Client extends JFrame implements ActionListener {
	JTextArea result = new JTextArea(20,40); // instance fields for the chat room
    JTextField input = new JTextField(27);
	JTextField server = new JTextField("localhost",10);
    JTextField port = new JTextField("5555",5);
    JLabel ser = new JLabel("Server: ");
    JLabel por = new JLabel("Port: ");
	JButton connectButton = new JButton("Connect");
	JButton sendButton = new JButton("Send");
	JLabel errors = new JLabel();
	JScrollPane scroller = new JScrollPane();
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	Thread thread;
    Font f = new Font("Georgia",Font.PLAIN,15);
	
	public Lab5Client() {
        setTitle("Chat Room"); // making the GUI look nice
		setLayout(new java.awt.FlowLayout());
		setSize(500,450);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		scroller.getViewport().add(result);
		add(scroller);
        result.setForeground(Color.blue);
        result.setWrapStyleWord(true);
        add(input); input.addActionListener(this);
        input.setHorizontalAlignment(JTextField.CENTER);
        input.setForeground(Color.BLUE);
        input.setFont(f);
        add(sendButton); sendButton.addActionListener(this); sendButton.setEnabled(false);
        add(ser);
		add(server); server.addActionListener(this);
        server.setHorizontalAlignment(JTextField.CENTER);
        server.setForeground(Color.RED);
        add(por);
        add(port); port.addActionListener(this);
        port.setHorizontalAlignment(JTextField.CENTER);
        port.setForeground(Color.RED);
		add(connectButton); connectButton.addActionListener(this);
		add(errors);
	}
	
	public void actionPerformed(ActionEvent evt) {
		try {
			// if the connect button is pressed then it reads from the port and server textfield and connect to the serverport
			if (evt.getActionCommand().equals("Connect") || connectButton.getText().equals("Connect") && evt.getSource() == server && evt.getSource() == port) {
				Scanner scanner = new Scanner(server.getText());
                Scanner scanner1 = new Scanner(port.getText());
				if (!scanner.hasNext()) 
				return;
                if (!scanner1.hasNextInt())
                return;
				String host = scanner.next();
				int portInt = scanner1.nextInt();
				socket = new Socket(host, portInt);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				thread = new ReadThread(in, result);
				thread.start();
				sendButton.setEnabled(true);
				connectButton.setText("Disconnect");
				server.setText("localhost");
                port.setText("5555");
			}
			// if it disconnects then cut everything off and set button to connect
			else if (evt.getActionCommand().equals("Disconnect")) {
				thread.interrupt();
				socket.close();
				in.close();
				out.close();
				sendButton.setEnabled(false);
				connectButton.setText("Connect");
			}
			// if send then read from the input text field and print the message
			else if (evt.getActionCommand().equals("Send") || sendButton.isEnabled() && evt.getSource() == input) {
				out.print(input.getText() + "\r\n");
				out.flush();
				input.setText("");
			}
		} catch(UnknownHostException uhe) {
			errors.setText(uhe.getMessage());
		} catch(IOException ioe) {
			errors.setText(ioe.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Lab5Client display = new Lab5Client();
		display.setVisible(true);
	}
}
// a class that reads from the textarea and sends to the server
class ReadThread extends Thread {
	BufferedReader in;
	JTextArea area;
	public ReadThread(BufferedReader br, JTextArea jta) {
		in = br;
		area = jta;
	}
	public void run() {
		String input;
		try {
			while ((input = in.readLine()) != null) {
				area.append(input);
			}
		} catch (IOException ioe) {
			System.out.println("Error reading from socket");
		}
	}
}