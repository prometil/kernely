/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Update the classpath by adding plugins url.
 */
public class ClasspathUpdater {
	
	//logger
	private static Logger log = LoggerFactory.getLogger(ClasspathUpdater.class);

	//the directory to look at
	private String directory;
	
	/**
	 * Constructor which needs the directory where plugins are.
	 * @param pDirectory The directory where plugins are.
	 */
	public ClasspathUpdater(String pDirectory){
		directory = pDirectory;
	}
	
	/**
	 * update the classpath with the plugins found 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(){
		
		IOFileFilter f = new SuffixFileFilter(".jar");
		File newDirectory = new File(this.directory);
		
		if(newDirectory.exists()){
			Collection<File> listFiles = FileUtils.listFiles(newDirectory, f, DirectoryFileFilter.INSTANCE);
			for (File file : listFiles) {
				try {
					URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
					Class urlClass = URLClassLoader.class;
					Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
					method.setAccessible(true);
					method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
					
				} catch (IllegalArgumentException e) {
					log.error("Classpath udpate failed, api change ?",e);
				} catch (IllegalAccessException e) {
					log.error("Classpath udpate failed, api change ?",e);
				} catch (InvocationTargetException e) {
					log.error("Classpath udpate failed, api change ?",e);
				} catch (SecurityException e) {
					log.error("Classpath udpate failed, api change ?",e);
				} catch (NoSuchMethodException e) {
					log.error("No such method, api change ?",e);
				} catch (MalformedURLException e) {
					log.error("Invalid url ?",e);
				}
			}
		}
		else{
			Log.info("Cannot find plugins directory, so cannot update class path");
		}
	}
}
