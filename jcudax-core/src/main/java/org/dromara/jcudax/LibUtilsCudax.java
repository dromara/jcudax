package org.dromara.jcudax;

import static org.dromara.jcudax.LibUtils.*;

/**
 * @author: wen.y
 * @date: 2025/1/9
 */
public class LibUtilsCudax {

	public static void loadLibrary(String libraryName, String... dependentLibraryNames) {
		LibUtils.OSType os = LibUtils.calculateOS();
		if (os == OSType.APPLE) {
			throw new JCudaxException("This CUDA version is not available on MacOS");
		} else {
			LibUtils.loadLibrary(libraryName, dependentLibraryNames);
		}
	}

}
