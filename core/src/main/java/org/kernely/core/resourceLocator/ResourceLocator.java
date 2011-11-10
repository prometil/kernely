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

package org.kernely.core.resourceLocator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.inject.AbstractModule;

public class ResourceLocator extends AbstractModule {
	public URL getResource(String resource) throws MalformedURLException{ 
		if (resource==null || "".equals(resource)){
			throw new IllegalArgumentException("file path cannot be null or empty");
		}
		
		//add media directory to the url of the ressource

		String fullURL="../media" + resource;
		
		File file=new File(fullURL);
		 if(!file.exists()){
			 if(ResourceLocator.class.getResource(resource)==null){
				 throw new IllegalArgumentException("file doesn't exist");
			 }
			 else{
				 return ResourceLocator.class.getResource(resource);	 
			 }
		 }
		 URL url=file.toURI().toURL();

	     return url;
	}
	
	public ResourceLocator(){
		
	}

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		
	}	
}
