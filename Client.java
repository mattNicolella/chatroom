import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame implements ActionListener {

    // declare the visual components

    private JTextArea dialog = new JTextArea();
    private JScrollPane scroll = new JScrollPane(dialog);
    private JTextField msg = new JTextField(30);
    private JButton btnSend = new JButton("Send");
    private JPanel p;
    private String name, results;

    // declare low level and high level objects for input
    private InputStream inStream;
    private DataInputStream inDataStream;

    // declare low level and high level objects for output
    private OutputStream outStream;
    private DataOutputStream outDataStream;

    // declare socket
    private Socket connection;

    // declare attribute to told details of remote machine and port
    private String remoteMachine;
    private int port;

    // constructor

    public Client(String remoteMachineIn, int portIn){
        remoteMachine = remoteMachineIn;
        port= portIn;

		remoteMachine = JOptionPane.showInputDialog(null, "What Is The Server Ip?");
        name = JOptionPane.showInputDialog(null, "What's Your Name?");


		name = "["+name+"] ";
        //add the visual components
        p = new JPanel();
		p.setLayout(new BorderLayout());

		msg.setSize(221,56);
		btnSend.setSize(63,28);


        add(scroll);
        p.add(msg, BorderLayout.CENTER);
        p.add(btnSend, BorderLayout.LINE_END);
        add(p);


        dialog.setEditable(false);

        setLayout(new GridLayout(2, 1));
        setTitle("Client");
        msg.setHorizontalAlignment(JLabel.CENTER);
        btnSend.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350,250);
        setVisible(true);
        setLocationRelativeTo(null);
        setResizable(false);

        //dialog.append("\nMsg:\n" + "\tHeight: " + msg.getHeight() + "\n\tWidth: " + msg.getWidth());
        //dialog.append("\nScroll:\n" + "\tHeight: " + scroll.getHeight() + "\n\tWidth: " + scroll.getWidth());
        //dialog.append("\nbtnSend:\n" + "\tHeight: " + btnSend.getHeight() + "\n\tWidth: " + btnSend.getWidth());
        //start the helper method that starts the client
        startClient();

        msg.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_ENTER)
					try
					{
						write();
					} catch (IOException ioe) { dialog.append("Unable To Send Message!");}
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
    }

    private void startClient() {
        // TODO Auto-generated method stub
        try{
            // attempt to create a connection to the server
            connection = new Socket(remoteMachine,port);
            dialog.setText("Connection Established..\n");

            // create an input stream from the server
            inStream = connection.getInputStream();
            inDataStream = new DataInputStream(inStream);

            //create output stream to the server
            outStream = connection.getOutputStream();
            outDataStream = new DataOutputStream(outStream);

            //send the host IP to the server
            outDataStream.writeUTF(connection.getLocalAddress().getHostName());

            Thread input = new Thread(new Runnable()
            {
				public void run()
				{
					while(true)
					{
						try {
							results = inDataStream.readUTF();
							dialog.append(results);
							dialog.setCaretPosition(dialog.getDocument().getLength());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
	        input.start();

        }catch (UnknownHostException e){
            dialog.append("Unknow host..");
        }
        catch (IOException except){
            dialog.append("Network Exception..");
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        try{
            // send the two integers to the server
            write();

            //read and display the results sent back from the server
            //String results= inDataStream.readUTF();
            dialog.append(results);
            msg.setText("");
            }catch(IOException ie){
            ie.printStackTrace();
        }
    }
    public void write() throws IOException
    {
		outDataStream.writeUTF(name + msg.getText());
	}

    public static void main (String args[]){
        new Client("10.13.9.188", 4444);
}
    }