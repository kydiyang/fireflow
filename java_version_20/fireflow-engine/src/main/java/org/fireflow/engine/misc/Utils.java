package org.fireflow.engine.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
	//InputStream 转换成byte[]
	private static final int BUFFER_SIZE = 1024;

	public static byte[] getBytes(InputStream is) throws IOException {

	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   byte[] b = new byte[BUFFER_SIZE];
	   int len = 0;

	   while ((len = is.read(b, 0, BUFFER_SIZE)) != -1) {
	    baos.write(b, 0, len);
	   }

	   baos.flush();

	   byte[] bytes = baos.toByteArray();

	   return bytes;
	}
}
