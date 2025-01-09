package org.dromara.jcudax;

/**
 * @author: wen.y
 * @date: 2025/1/9
 */
public class JCudax {

	private static boolean initialized = false;

	public static void initialize() {
		if (!initialized) {
			String libraryBaseName = "JCudax-" + JCudaxVersion.get();
			String libraryName = LibUtils.createPlatformLibraryName(libraryBaseName);
			LibUtilsCudax.loadLibrary(libraryName, new String[0]);
			initialized = true;
		}
	}

	public static native void matrixSoftMaxPd(double[] qkt, double[] errorMatrix, double[] grMatrix, int x, int y, double wordVectorDimension);
}
