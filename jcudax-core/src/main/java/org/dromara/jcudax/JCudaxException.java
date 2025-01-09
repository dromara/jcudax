package org.dromara.jcudax;

/**
 * @author: wen.y
 * @date: 2025/1/9
 */
public class JCudaxException extends RuntimeException {
	private static final long serialVersionUID = 1587809813906124159L;

	public JCudaxException(String message) {
		super(message);
	}

	public JCudaxException(String message, Throwable cause) {
		super(message, cause);
	}
}
