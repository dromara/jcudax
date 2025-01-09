package org.dromara.jcudax;

/**
 * @author: wen.y
 * @date: 2025/1/9
 */
public class JCudaxVersion {
	public static String get() {
		return "12.0.0";
	}

	public static boolean isAvailable() {
		try {
			// TODO
			return true;
		} catch (Throwable var1) {
			return false;
		}
	}

	private JCudaxVersion() {

	}
}
