/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
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
