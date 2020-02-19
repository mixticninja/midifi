package com.flat20.fingerplay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyReader {
	
	private String toLaunch = "";
	private int port=-1;
	private InputStream inputStream;
	private OutputStream output ;
	
	private static Properties prop = new Properties();
	String propFileName = "config.properties";
	
	private static PropertyReader instance;
 
	private PropertyReader() {
		super();
		try {
			readPropValues();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static PropertyReader getInstance() {
		if (instance==null) {
			instance = new PropertyReader();
		}
		return instance;
	}
	
	public void readPropValues() throws IOException {
 
		try {
			
			 inputStream = null;
			 
			    // First try loading from the current directory
			    try {
			        File f = new File(propFileName);
			        inputStream = new FileInputStream( f );
			    }
			    catch ( Exception e ) { inputStream = null; }
			     
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				 File f = new File(propFileName);
				   
				 output = new FileOutputStream(f);
				 
			        // save properties to project root folder
		         prop.store(output, null);
				//throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 
			// get the property value and print it out
			toLaunch = prop.getProperty("launch");
			port= Integer.parseInt(prop.getProperty("port"));
		
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		
	}
	public void writeProp(String name, String value) {
		
		 try {

			 File f = new File(propFileName);
	   
			 output = new FileOutputStream(f);

	         // set the properties value
	         prop.setProperty(name, value);
	    
	         // save properties to project root folder
	         prop.store(output, null);

	         System.out.println(prop);

	     } catch (Exception io) {
	         io.printStackTrace();
	     }
		 
		
	}
	
	
	public int getPort() {
		return port;
	}
	public String getToLaunch() {
		return toLaunch;
	}
	
	

}
