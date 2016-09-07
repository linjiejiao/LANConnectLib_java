package cn.ljj.connect.ip;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cn.ljj.connect.utils.Logger;

public class TCPClient {
	public static final String TAG = "TCPClient";
	
	public static TCPSession connectTo(String serverIp, int port){
		try {
			Socket socket = new Socket(serverIp, port);
			TCPSession session = new TCPSession(socket);
			Logger.d(TAG, "connect to " + serverIp + ":" + port + " success!");
			return session;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.d(TAG, "connect to " + serverIp + ":" + port + " failed!");
		return null;
	}

}
