package cn.ljj.connect.ip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cn.ljj.connect.utils.Logger;

public class TCPServer extends Thread {
	public static final String TAG = "TCPServer";

	public interface IConnectionListener {
		void onClientConnecte(TCPSession tcpSession);

		void onServerStopped();
	}

	private ServerSocket serverSocket = null;
	private boolean serverAlive = false;
	private IConnectionListener listener;

	public boolean startServer(int port, IConnectionListener l) {
		if (l == null) {
			throw new NullPointerException("IConnectionListener can't be null !");
		}
		listener = l;
		if (serverAlive) {
			Logger.e(TAG, "server has already started!");
			return false;
		}
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			Logger.e(TAG, "create server catch exception!");
			e.printStackTrace();
			return false;
		}
		serverAlive = true;
		start();
		return true;
	}

	@Override
	public void run() {
		while (serverAlive && serverSocket != null) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				Logger.e(TAG, "server quite accept! " + e.getMessage());
				break;
			}
			try {
				TCPSession session = new TCPSession(socket);
				Logger.d(TAG, "handle new accept client:" + session);
				listener.onClientConnecte(session);
			} catch (Exception e) {
				Logger.e(TAG, "handle new accept client catch exception!");
				e.printStackTrace();
			}
		}
		if(serverAlive){
			listener.onServerStopped();
			stopServer();
		}
		Logger.e(TAG, "server run end!");
	}

	public void stopServer() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			serverSocket = null;
		}
		serverAlive = false;
	}
}
