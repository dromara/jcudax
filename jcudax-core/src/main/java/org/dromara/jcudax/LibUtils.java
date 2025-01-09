package org.dromara.jcudax;

import java.io.*;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: wen.y
 * @date: 2025/1/9
 */
public final class LibUtils {
	private static final Logger logger = Logger.getLogger(LibUtils.class.getName());
	private static final Level level;
	private static final String LIBRARY_PATH_IN_JAR = "/lib";

	private LibUtils() {
	}

	public static void loadLibrary(String libraryName, String... dependentLibraryNames) {
		logger.log(level, "Loading library: " + libraryName);

		try {
			logger.log(level, "Loading library as a file");
			System.loadLibrary(libraryName);
			logger.log(level, "Loading library as a file DONE");
		} catch (Throwable var7) {
			logger.log(level, "Loading library as a file FAILED");

			try {
				logger.log(level, "Loading library as a resource");
				loadLibraryResource("/lib", libraryName, "", dependentLibraryNames);
				logger.log(level, "Loading library as a resource DONE");
			} catch (Throwable var6) {
				logger.log(level, "Loading library as a resource FAILED", var6);
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println("Error while loading native library \"" + libraryName + "\"");
				pw.println("Operating system name: " + System.getProperty("os.name"));
				pw.println("Architecture         : " + System.getProperty("os.arch"));
				pw.println("Architecture bit size: " + System.getProperty("sun.arch.data.model"));
				pw.println("---(start of nested stack traces)---");
				pw.println("Stack trace from the attempt to load the library as a file:");
				var7.printStackTrace(pw);
				pw.println("Stack trace from the attempt to load the library as a resource:");
				var6.printStackTrace(pw);
				pw.println("---(end of nested stack traces)---");
				pw.close();
				throw new UnsatisfiedLinkError(sw.toString());
			}
		}
	}

	private static void loadLibraryResource(String resourceSubdirectoryName, String libraryName, String tempSubdirectoryName, String... dependentLibraryNames) throws Throwable {
		String[] var4 = dependentLibraryNames;
		int var5 = dependentLibraryNames.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			String dependentLibraryName = var4[var6];
			logger.log(level, "Library " + libraryName + " depends on " + dependentLibraryName);
			String dependentResourceSubdirectoryName = resourceSubdirectoryName + "/" + osString() + "/" + archString();
			String dependentLibraryTempSubDirectoryName = libraryName + "_dependents" + File.separator + osString() + File.separator + archString() + File.separator;
			loadLibraryResource(dependentResourceSubdirectoryName, dependentLibraryName, dependentLibraryTempSubDirectoryName);
		}

		String libraryFileName = createLibraryFileName(libraryName);
		File libraryTempFile = createTempFile(tempSubdirectoryName, libraryFileName);
		if (!libraryTempFile.exists()) {
			String libraryResourceName = resourceSubdirectoryName + "/" + libraryFileName;
			logger.log(level, "Writing resource  " + libraryResourceName);
			logger.log(level, "to temporary file " + libraryTempFile);
			writeResourceToFile(libraryResourceName, libraryTempFile);
		}

		logger.log(level, "Loading library " + libraryTempFile);
		System.load(libraryTempFile.toString());
		logger.log(level, "Loading library " + libraryTempFile + " DONE");
	}

	private static File createTempFile(String tempSubdirectoryName, String name) throws IOException {
		String tempDirName = System.getProperty("java.io.tmpdir");
		File tempSubDirectory = new File(tempDirName + File.separator + tempSubdirectoryName);
		if (!tempSubDirectory.exists()) {
			boolean createdDirectory = tempSubDirectory.mkdirs();
			if (!createdDirectory) {
				throw new IOException("Could not create directory for temporary file: " + tempSubDirectory);
			}
		}

		String tempFileName = tempSubDirectory + File.separator + name;
		File tempFile = new File(tempFileName);
		return tempFile;
	}

	private static void writeResourceToFile(String resourceName, File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("Target file may not be null");
		} else if (file.exists()) {
			throw new IllegalArgumentException("Target file already exists: " + file);
		} else {
			InputStream inputStream = LibUtils.class.getResourceAsStream(resourceName);
			if (inputStream == null) {
				throw new IOException("No resource found with name '" + resourceName + "'");
			} else {
				OutputStream outputStream = null;

				try {
					outputStream = new FileOutputStream(file);
					byte[] buffer = new byte['耀'];

					while(true) {
						int read = inputStream.read(buffer);
						if (read < 0) {
							outputStream.flush();
							return;
						}

						outputStream.write(buffer, 0, read);
					}
				} finally {
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException var14) {
							logger.log(Level.SEVERE, var14.getMessage(), var14);
						}
					}

					try {
						inputStream.close();
					} catch (IOException var13) {
						logger.log(Level.SEVERE, var13.getMessage(), var13);
					}

				}
			}
		}
	}

	public static String createLibraryFileName(String libraryName) {
		String libPrefix = createLibraryPrefix();
		String libExtension = createLibraryExtension();
		String fullName = libPrefix + libraryName + "." + libExtension;
		return fullName;
	}

	private static String createLibraryExtension() {
		OSType osType = calculateOS();
		switch (osType) {
			case APPLE:
				return "dylib";
			case ANDROID:
			case LINUX:
			case SUN:
				return "so";
			case WINDOWS:
				return "dll";
			default:
				return "";
		}
	}

	private static String createLibraryPrefix() {
		OSType osType = calculateOS();
		switch (osType) {
			case APPLE:
			case ANDROID:
			case LINUX:
			case SUN:
				return "lib";
			case WINDOWS:
				return "";
			default:
				return "";
		}
	}

	public static String createPlatformLibraryName(String baseName) {
		return baseName + "-" + osString() + "-" + archString();
	}

	private static String osString() {
		OSType osType = calculateOS();
		return osType.toString().toLowerCase(Locale.ENGLISH);
	}

	private static String archString() {
		ArchType archType = calculateArch();
		return archType.toString().toLowerCase(Locale.ENGLISH);
	}

	public static OSType calculateOS() {
		String vendor = System.getProperty("java.vendor");
		if ("The Android Project".equals(vendor)) {
			return LibUtils.OSType.ANDROID;
		} else {
			String osName = System.getProperty("os.name");
			osName = osName.toLowerCase(Locale.ENGLISH);
			if (osName.startsWith("mac os")) {
				return LibUtils.OSType.APPLE;
			} else if (osName.startsWith("windows")) {
				return LibUtils.OSType.WINDOWS;
			} else if (osName.startsWith("linux")) {
				return LibUtils.OSType.LINUX;
			} else {
				return osName.startsWith("sun") ? LibUtils.OSType.SUN : LibUtils.OSType.UNKNOWN;
			}
		}
	}

	public static ArchType calculateArch() {
		String osArch = System.getProperty("os.arch");
		osArch = osArch.toLowerCase(Locale.ENGLISH);
		if (!"i386".equals(osArch) && !"x86".equals(osArch) && !"i686".equals(osArch)) {
			if (!osArch.startsWith("amd64") && !osArch.startsWith("x86_64")) {
				if (osArch.startsWith("arm64")) {
					return LibUtils.ArchType.ARM64;
				} else if (osArch.startsWith("arm")) {
					return LibUtils.ArchType.ARM;
				} else if (!"ppc".equals(osArch) && !"powerpc".equals(osArch)) {
					if (osArch.startsWith("ppc")) {
						return LibUtils.ArchType.PPC_64;
					} else if (osArch.startsWith("sparc")) {
						return LibUtils.ArchType.SPARC;
					} else if (osArch.startsWith("mips64")) {
						return LibUtils.ArchType.MIPS64;
					} else if (osArch.startsWith("mips")) {
						return LibUtils.ArchType.MIPS;
					} else if (osArch.contains("risc")) {
						return LibUtils.ArchType.RISC;
					} else {
						return osArch.startsWith("aarch64") ? LibUtils.ArchType.AARCH64 : LibUtils.ArchType.UNKNOWN;
					}
				} else {
					return LibUtils.ArchType.PPC;
				}
			} else {
				return LibUtils.ArchType.X86_64;
			}
		} else {
			return LibUtils.ArchType.X86;
		}
	}

	static {
		String levelPropertyName = "jcuda.LibUtils.level";
		String levelValue = System.getProperty(levelPropertyName);
		Level parsedLevel = Level.FINE;
		if (levelValue != null) {
			try {
				parsedLevel = Level.parse(levelValue);
			} catch (IllegalArgumentException var4) {
				logger.warning("Invalid value for " + levelPropertyName + ": " + levelValue + " - defaulting to Level.FINE");
			}
		}

		level = parsedLevel;
	}

	public static enum ArchType {
		PPC,
		PPC_64,
		SPARC,
		X86,
		X86_64,
		ARM,
		ARM64,
		AARCH64,
		MIPS,
		MIPS64,
		RISC,
		UNKNOWN;

		private ArchType() {
		}
	}

	public static enum OSType {
		ANDROID,
		APPLE,
		LINUX,
		SUN,
		WINDOWS,
		UNKNOWN;

		private OSType() {
		}
	}
}
