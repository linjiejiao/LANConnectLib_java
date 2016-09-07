package cn.ljj.connect.ip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import cn.ljj.connect.utils.Logger;

public class UDPListener extends Thread{
	public static final String TAG = "UDPListener";
	
	private	DatagramSocket datagramSocket = null;
	private IReadIPListener listener = null;
	private boolean stop = true;
	
	public UDPListener(){
		super("UDPListener");
	}

	public DatagramSocket startListen(int port, IReadIPListener l) throws Exception {
		if (l == null) {
			throw new NullPointerException("IReadIPListener can't be null !");
		}
		if(!stop){
			throw new Exception("listener has already been started !");
		}
		datagramSocket = new DatagramSocket(port);
		listener = l;
		stop = false;
		start();
		return datagramSocket;
	}
	
	@Override
	public void run(){
		byte[] buffer = new byte[IReadIPListener.RECEIVE_BUF_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (!stop && datagramSocket != null) {
			try {
				datagramSocket.receive(packet);
				byte[] data = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength());
				Logger.d(TAG, "onReceive data.length=" + data.length + "; address=" + packet.getSocketAddress());
				listener.onReceiveData(packet.getAddress().getHostAddress(), packet.getPort(), data);
			} catch (Exception e) {
				listener.onListenEnd(IReadIPListener.END_REASON_INPUT_CLOSED);
				e.printStackTrace();
				stopListen();
				Logger.w(TAG, "run end with exception!");
				return;
			}
		}
		listener.onListenEnd(IReadIPListener.END_REASON_MANUAL_CLOSED);
		Logger.w(TAG, "run end!");
	}

	public void stopListen() {
		if(datagramSocket != null){
			datagramSocket.close();
			datagramSocket = null;
		}
		stop = true;
	}
}
