package cn.ljj.connect.protocol.sniffer;

import java.nio.ByteBuffer;

import cn.ljj.connect.pkg.InvalidProtocolData;
import cn.ljj.connect.pkg.PackUtils;
import cn.ljj.connect.pkg.PackageProtocol;

public class Terminal implements PackageProtocol {
	public String ipAddress;
	public short tcpServerPort;
	public short udpServerPort;
	public short version;
	public short status;
	public String extra;

	@Override
	public int size() {
		return 8 + PackUtils.calcMarshallSize(ipAddress) + PackUtils.calcMarshallSize(extra);
	}

	@Override
	public ByteBuffer packToBuffer(ByteBuffer buffer) {
		PackUtils.pack(buffer, ipAddress);
		buffer.putShort(tcpServerPort);
		buffer.putShort(udpServerPort);
		buffer.putShort(version);
		buffer.putShort(status);
		PackUtils.pack(buffer, extra);
		return buffer;
	}

	@Override
	public void unPackFromBuffer(ByteBuffer buffer) throws InvalidProtocolData {
		ipAddress = PackUtils.unPackShortString(buffer);
		tcpServerPort = buffer.getShort();
		udpServerPort = buffer.getShort();
		version = buffer.getShort();
		status = buffer.getShort();
		extra = PackUtils.unPackShortString(buffer);
	}

	@Override
	public String toString() {
		return "Terminal [ipAddress=" + ipAddress + ", tcpServerPort=" + tcpServerPort + ", udpServerPort="
				+ udpServerPort + ", version=" + version + ", status=" + status + ", extra=" + extra + "]";
	}

}
