/**
 * 
 */
package org.kernely.bootstrap.classpath;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.eclipse.jetty.util.log.Log;

/**
 * @author yak
 *
 */
public class ClasspathUpdater {
	
	String directory;
	
	public ClasspathUpdater(String pDirectory){
		directory = pDirectory;
	}
	
	@SuppressWarnings("unchecked")
	public void update(){
		IOFileFilter f = new SuffixFileFilter(".jar");
		File directory = new File("plugins");
		if(directory.exists()){
			Collection<File> listFiles = FileUtils.listFiles(directory, f, DirectoryFileFilter.INSTANCE);
			for (File file : listFiles) {
				try {
					URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
					Class urlClass = URLClassLoader.class;
					Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
					method.setAccessible(true);
					method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			Log.info("Cannot find plugins directory, so cannot update class path");
		}
	}
}
