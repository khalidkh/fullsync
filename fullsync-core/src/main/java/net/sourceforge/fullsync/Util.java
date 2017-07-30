/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class Util {
	/**
	 * used for all I/O buffers.
	 */
	private static final int IOBUFFERSIZE = 0x1000;

	public static String getResourceAsString(final String name) {
		StringBuilder out = new StringBuilder();
		try (InputStream is = getContextClassLoader().getResourceAsStream(name)) {
			if (null != is) {
				final char[] buffer = new char[IOBUFFERSIZE];
				Reader in = new InputStreamReader(is, StandardCharsets.UTF_8);
				int read;
				do {
					read = in.read(buffer, 0, buffer.length);
					if (read > 0) {
						out.append(buffer, 0, read);
					}
				} while (read >= 0);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return out.toString();
	}

	public static String getFullSyncVersion() {
		return Util.getResourceAsString("net/sourceforge/fullsync/version.txt").trim();
	}

	public static File getInstalllocation() {
		URL codeSource = Util.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			return new File(codeSource.toURI().resolve("../"));
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return new File(".");
	}

	public static Set<String> loadDirectoryFromClasspath(String path)
		throws URISyntaxException, UnsupportedEncodingException, IOException {
		ClassLoader cl = getContextClassLoader();
		Enumeration<URL> urls = cl.getResources(path);
		Set<String> children = new HashSet<>();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			File src;
			if ("jar".equals(url.getProtocol())) {
				src = new File(new URI(url.toString().replaceAll("^jar:(.+)!/.*$", "$1")));
			}
			else {
				src = new File(url.toURI());

			}
			if (src.isDirectory() && src.exists()) {
				src.toPath().forEach(p -> children.add(p.toFile().getName()));
			}
			else if (src.isFile() && src.exists()) {
				try (JarFile jar = new JarFile(src)) {
					Enumeration<JarEntry> jarEntries = jar.entries();
					String prefix = path;
					if ('/' == prefix.charAt(0)) {
						prefix = prefix.substring(1);
					}
					while (jarEntries.hasMoreElements()) {
						JarEntry entry = jarEntries.nextElement();
						String name = entry.getName();
						if (!entry.isDirectory() && name.startsWith(prefix)) { //filter according to the path
							name = name.substring(prefix.length());
							children.add(name);
						}
					}
				}
			}
		}
		return children;
	}

	private static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static void fileRenameToPortableLegacy(String from, String to) throws Exception {
		File srcFile = new File(from);
		File dstFile = new File(to);
		if (!srcFile.renameTo(dstFile)) {
			File tmpFile = File.createTempFile("fullsync", "tmp", dstFile.getParentFile());
			tmpFile.delete();
			if (dstFile.renameTo(tmpFile)) {
				if (srcFile.renameTo(dstFile)) {
					tmpFile.delete();
				}
				else {
					tmpFile.renameTo(dstFile);
					throw new Exception("File.renameTo failed (cannot rename file)");
				}
			}
			else {
				throw new Exception("File.renameTo failed (cannot move old file away)");
			}
		}
	}
}