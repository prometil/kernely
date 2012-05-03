/**
 * 
 */
package org.kernely.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Http downloader tool
 * 
 */
public class ZipUtil {

	/**
	 * Extract a zip to a folder
	 * @param zin the zip
	 * @param outputFolderRoot the folder
	 * @throws IOException
	 */
	public static void extractToFolder(ZipInputStream zin, File outputFolderRoot, ProgressMonitor progressMonitor) throws IOException {

		FileOutputStream fos = null;
		byte[] buf = new byte[1024];
		ZipEntry zipentry;
		progressMonitor.start();

		for (zipentry = zin.getNextEntry(); zipentry != null; zipentry = zin.getNextEntry()) {

			try {
				String entryName = zipentry.getName();
				int n;

				File newFile = new File(outputFolderRoot, entryName);
				if (zipentry.isDirectory()) {
					newFile.mkdirs();
					continue;
				} else {
					newFile.getParentFile().mkdirs();
					newFile.createNewFile();
				}

				fos = new FileOutputStream(newFile);

				while ((n = zin.read(buf, 0, 1024)) > -1){
					progressMonitor.tick();
					fos.write(buf, 0, n);
				}
					
					

				fos.close();
				zin.closeEntry();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null)
					try {
						fos.close();
					} catch (Exception ignore) {
					}
			}
			progressMonitor.end();
		}

		zin.close();

	}
}
