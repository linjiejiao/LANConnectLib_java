package cn.ljj.connect.protocol.sniffer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import cn.ljj.connect.pkg.AbsPackage;
import cn.ljj.connect.pkg.InvalidProtocolData;
import cn.ljj.connect.pkg.PackUtils;
import cn.ljj.connect.protocol.UriDefine;

public class SnifferRes extends AbsPackage {
	public static final int URI = UriDefine.SnifferResUri;
	public Map<String, String> data = new HashMap<String, String>();

	@Override
	public int size() {
		return 4 + PackUtils.calcMarshallSize(data);
	}

	@Override
	public ByteBuffer packToBuffer(ByteBuffer buffer) {
		buffer.putInt(seqId);
		PackUtils.pack(buffer, data, String.class);
		return buffer;
	}

	@Override
	public void unPackFromBuffer(ByteBuffer buffer) throws InvalidProtocolData {
		seqId = buffer.getInt();
		PackUtils.unPack(buffer, data, String.class, String.class);
	}

	@Override
	public String toString() {
		return "SnifferRes{" + "seqId=" + seqId + ", data=" + data + '}';
	}
}
