package cn.ljj.connect.ip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import cn.ljj.connect.utils.Logger;

public class TCPSession extends Thread {
	public static final String TAG = "TCPSession";

	private String remoteIp;
	private int remotePort;
	private IReadIPListener listener = null;
	private Socket socket;
	private InputStream inStream;
	private OutputStream outStream;
	private boolean sessionAlive = false;

	public TCPSession(Socket socket) throws IOException {
		this.remoteIp = socket.getInetAddress().getHostAddress();
		this.remotePort = socket.getPort();
		inStream = socket.getInputStream();
		outStream = socket.getOutputStream();
	}

	public void startSession(IReadIPListener l) {
		listener = l;
		sessionAlive = true;
		start();
	}

	public void endSession() {
		sessionAlive = false;
		closeInputStream();
		closeOutputStream();
		closeSocket();
	}

	public boolean writeData(byte[] data) {
		if (outStream == null) {
			return false;
		}
		try {
			outStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
			closeOutputStream();
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[IReadIPListener.RECEIVE_BUF_SIZE];
		while (sessionAlive && inStream != null) {
			try {
				int len = inStream.read(buffer);
				if (len <= 0) {
					notifyListenerEnd(IReadIPListener.END_REASON_REACH_STREAM_END);
					Logger.e(TAG, "inStream reach end len=" + len);
					return;
				}
				byte[] data = Arrays.copyOfRange(buffer, 0, len);
				Logger.d(TAG, "onReceive data.length=" + data.length + "; address=" + remoteIp);
				if (listener != null) {
					listener.onReceiveData(remoteIp, remotePort, data);
				}
			} catch (IOException e) {
				closeInputStream();
				notifyListenerEnd(IReadIPListener.END_REASON_INPUT_CLOSED);
				Logger.w(TAG, "read end! " + e.getMessage());
				return;
			}
		}
		notifyListenerEnd(IReadIPListener.END_REASON_MANUAL_CLOSED);
		Logger.w(TAG, "read end sessionAlive=" + sessionAlive);
	}

	private void closeInputStream() {
		if (inStream != null) {
			try {
				inStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			inStream = null;
		}
	}

	private void closeOutputStream() {
		if (outStream != null) {
			try {
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			outStream = null;
		}
	}

	private void closeSocket() {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			socket = null;
		}
	}

	private void notifyListenerEnd(int reason) {
		if (listener != null) {
			listener.onListenEnd(reason);
		}
	}

	@Override
	public String toString() {
		return "TCPSession [remoteIp=" + remoteIp + ", remotePort=" + remotePort + ", sessionAlive=" + sessionAlive
				+ "]";
	}

}
