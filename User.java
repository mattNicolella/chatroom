import java.io.*;
import java.net.*;

public class User extends Thread
{
	private DataInputStream in;
    private DataOutputStream out;
    private String name, msg;
    private boolean isNew;

    Socket socket;
    public User(Socket socket, String name)
    {
		this.socket = socket;
        this.name = name;

        initStream();
        this.start();
    }

    public void initStream()
    {
        try
        {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        }catch(IOException e) { e.printStackTrace(); }
    }

    public void closeStream()
    {
		try
		{
            out.close(); in.close();
            socket.close();
		} catch(IOException e) {}
	}

    public void sendMessage(String string) {
        try {
            out.writeUTF(string + "\n");
        }catch(IOException e) { e.printStackTrace(); }
    }

    public void run()
    {
        try
        {
            while(true)
            {
                msg = in.readUTF();
                isNew = true;
			}
        } catch(IOException e) { closeStream(); System.exit(0);}

    }
    public boolean newMsg()
    {
		return isNew;
	}
    public String getMsg()
	{
		isNew = false;
		return msg;
	}
}