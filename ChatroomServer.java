import javax.swing.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;


public class ChatroomServer extends JFrame
{
	private JTextArea jtaLog;
	private JTextField input;
	private int port;
	private ArrayList<User> clients;
	private String str;
	private DateFormat dateFormat;
	private Calendar cal;

	public static void main(String [] args)
	{
		new ChatroomServer(4444);
	}

	public ChatroomServer(int port)
	{

		clients = new ArrayList<User>();
		jtaLog = new JTextArea();
		JScrollPane scroll = new JScrollPane(jtaLog);
		input = new JTextField(20);
		this.port = port;

		TimerListener listener = new TimerListener();

		javax.swing.Timer tm = new javax.swing.Timer(50, listener);
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		cal = Calendar.getInstance();

		tm.start();

		jtaLog.setEditable(false);

		setTitle("Chatroom Server");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(input, BorderLayout.SOUTH);
		add(scroll);

		setSize(400, 300);

		//jtaLog.setSize(new Dimension(380,300));
		//input.setSize(new Dimension(20,300));

		setVisible(true);
		input.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_ENTER)
					runScript();
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});

		initServer();

	}
	public void initServer()
	{
		jtaLog.append("Server: Listening on port "+ port +"\n");
		Socket connection;
	    ServerSocket socket;
		String client, msg;

		saveIp();

		try
		{
			socket = new ServerSocket(port);

			while(true)
			{

					connection = socket.accept();

					jtaLog.append("Server: New Client Connected "+ connection.getInetAddress() +"\n");

					clients.add(new User(connection, new DataInputStream(connection.getInputStream()).readUTF()));

					//clients.get(clients.size()-1).start();

					//socket = new ServerSocket(port, 0, InetAddress.getLocalHost());
			}
		} catch(UnknownHostException e) {jtaLog.append("Server: Unknown Host Error\n");
		} catch(IOException e) {jtaLog.append("Server: Port Already In Use\n"); e.printStackTrace();
		} catch(Exception e) {jtaLog.append("Server: Client Not Successfully Loaded"); System.out.println(e.toString());}
	}
	public void runScript()
	{
		str = input.getText();
		input.setText("");
		if(str.indexOf("sendMsg ") >= 0)
		{
			cleanInput("sendMsg");
			str = "Admin:" + str;

			write();
		}
		else
			jtaLog.append("Server: Unknown Command");
	}
	public void saveIp()
	{
		try
		{
			FileWriter fw = new FileWriter(new File("ip.txt"));

			fw.write(InetAddress.getLocalHost().getHostAddress());
			fw.close();
		} catch(IOException e) {jtaLog.append("Server: IP Writer Crash");}

	}
	public void write()
	{
		for(User u : clients)
			u.sendMessage(str);
	}
	public void cleanInput(String keyword)
	{
		str = str.substring(keyword.length());
	}
	public boolean newMsg()
	{
		for(User u : clients)
			if(u.newMsg())
				return true;
		return false;
	}
	private class TimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(newMsg())
			{
				for(User u : clients)
				{
					if(u.newMsg())
					{
						str = dateFormat.format(cal.getTime())+ " " + u.getMsg();
						jtaLog.append(str+ "\n");
						jtaLog.setCaretPosition(jtaLog.getDocument().getLength());
						write();
					}
				}
			}
		}
	}

}