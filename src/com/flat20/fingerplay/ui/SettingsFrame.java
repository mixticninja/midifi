package com.flat20.fingerplay.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;

import com.flat20.fingerplay.PropertyReader;



public class SettingsFrame extends JFrame  implements ActionListener{

	private String toLaunch;
	
	JTextArea label = new JTextArea();
	GridBagConstraints c = new GridBagConstraints();
	
	
	JButton okBtn,cancelBtn;
	SettingButton chooseBtn;
	
	   JFileChooser chooser;
	   String choosertitle = "Select midi virtual cable .exe path";
	   
	    Font font = new Font("SansSerif", Font.BOLD, 16);

	    JLabel portLabel,pathLabel;
	    JFormattedTextField portInput;
	    
	   

	   
	public SettingsFrame() throws HeadlessException {
		super();
		// TODO Auto-generated constructor stub
	}

	public SettingsFrame(GraphicsConfiguration arg0,int port) {
		super(arg0);
		setTitle("Midifi server setup");
		
		BackgroundPanel back = new BackgroundPanel("back.jpg");
		
		chooseBtn = new SettingButton("Virtual midi .exe path", "btn.png","btnh.png");
		
		
		 toLaunch= PropertyReader.getInstance().getToLaunch();
	
		label = makeLabelStyle(label);
		
		makeMultilineLabel(label);

        label.setSize(500,300);
        label.setFont(font);
        label.setForeground(Color.WHITE);
            
        if (toLaunch.isEmpty()) {
        	String rpl = "No prelaunch executable selected !";
        	label.setText(rpl);
        }else {
        	label.setText(toLaunch);
        }
        
		chooseBtn.addActionListener(this);	 
		add(back);
		  
		   NumberFormat integerFieldFormatter = NumberFormat.getIntegerInstance();
		   integerFieldFormatter.setGroupingUsed(false);

		    portLabel = new JLabel("Enter Listening port number:");
		  //  portLabel.setSize(300,100);
		    portLabel.setForeground(Color.WHITE);
		    
		    c.weightx = 0.2;
		    c.ipady = 20; 
	        c.gridx = 0;
	        c.gridy = 0;
	        back.add(portLabel,c);
		    
		   
		    portInput = new JFormattedTextField(integerFieldFormatter);
		    try {
			    MaskFormatter mf = new MaskFormatter("####") {
			        @Override
			        public char getPlaceholderCharacter() {
			            return '0'; // replaces default space characters with zeros
			        }
			    };
			    mf.install(portInput); //assign the maskformatter to the text field
			} catch (ParseException e) {
			    e.printStackTrace(); // always, remember, ALWAYS print stack traces
			}
		    
		    
		    portInput.setValue(port);
		    portInput.setColumns(4);
		    portInput.setSize(60, 20);
		    portInput.setFont(font);
		    c.ipadx = 150;  
		    c.ipady = 20;   
	        c.gridx = 0;
	        c.gridy = 1;
//		    
		    back.add(portInput,c);
		    
		 
		    
		    pathLabel     = new JLabel("Clic to choose executable path : ");   
		    pathLabel.setForeground(Color.WHITE);
	        c.gridx = 0;
	        c.gridy = 3;
	        c.ipady = 20; 
	        back.add(pathLabel,c);
		   
	        c.gridx = 0;
	        c.gridy = 4;
	  
			back.add(chooseBtn,c);
			
			 c.gridx = 1;
		       c.gridy = 5;
		     back.add(label,c);
			
			
		 okBtn = new JButton("OK");
		 c.fill = GridBagConstraints.HORIZONTAL;
		 c.ipady = 0;       //reset to default
		 c.weighty = 1.0;   //request any extra vertical space
		 c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		 c.insets = new Insets(10,0,0,0);  //top padding
		 c.gridx = 3;       //aligned with button 2
		 c.gridwidth = 1;   //2 columns wide
		 c.gridy = 6;       //third row
		 back.add(okBtn, c);
		 
		 okBtn.addActionListener(this);
		 
		 cancelBtn = new JButton("Cancel");
		 c.fill = GridBagConstraints.HORIZONTAL;
		 c.ipady = 0;       //reset to default
		 c.weighty = 1.0;   //request any extra vertical space
		 c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		 c.insets = new Insets(10,0,0,0);  //top padding
		 c.gridx = 4;       //aligned with button 2
		 c.gridwidth = 1;   //2 columns wide
		 c.gridy = 6;       //third row
		 back.add(cancelBtn, c); 
		 cancelBtn.addActionListener(this);	 
	
			java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			  int height = screenSize.height;
			  int width = screenSize.width;
			  setMinimumSize(new Dimension(640, 480));
			 setPreferredSize(new Dimension(width/3, height/2));
			    pack();
			
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setResizable(true);
		
			setVisible(true);
		 
		 
	}
	public static JTextArea makeLabelStyle(JTextArea textArea) {
		  if (textArea == null)
		    return null;
		  textArea.setEditable(false);
		  textArea.setCursor(null);
		  textArea.setOpaque(false);
		  textArea.setFocusable(false);
		  return textArea;
		}
	
	public static void makeMultilineLabel(JTextComponent area) {
		   area.setFont(UIManager.getFont("Label.font"));
		   area.setEditable(false);
		   area.setOpaque(false);    
		   if (area instanceof JTextArea) {
		     ((JTextArea)area).setWrapStyleWord(true);
		     ((JTextArea)area).setLineWrap(true);
		   }
		 }
	
	public void actionPerformed(ActionEvent e) {
	        if (e.getSource() ==chooseBtn){
	        	chooser = new JFileChooser(); 
			    chooser.setSelectedFile(new java.io.File(toLaunch));
			    chooser.setDialogTitle(choosertitle);
			    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
			    //
			    // disable the "All files" option.
			    //
			    chooser.setAcceptAllFileFilterUsed(false);
			    //    
			    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
			      System.out.println("getCurrentDirectory(): " 
			         +  chooser.getCurrentDirectory());
			      System.out.println("getSelectedFile() : " 
			         +  chooser.getSelectedFile());

			      label.setText( chooser.getSelectedFile().getAbsolutePath());
			      }
			    else {
			      System.out.println("No Selection ");
			      }
	        }
		    
	        if (e.getSource()==okBtn){
	     
	        	 PropertyReader.getInstance().writeProp("launch", label.getText());
	        	 try {
					portInput.commitEdit();
				} catch (ParseException e1) {
	
					e1.printStackTrace();
					return;
				}
	        	 long port = (Long)portInput.getValue();
	        	 
	        	 PropertyReader.getInstance().writeProp("port", String.valueOf(port));
	        	 System.out.println("New Values saved, please restart server ...");
	        	 System.exit(0);
	        }
	        if (e.getSource()== cancelBtn){
	        	this.dispose();
	        }
	}
	
	
//	public void restartApplication() 
//	{
//		
//		 System.out.println("Restarting server ...");
//	  final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
//	  File currentJar = null;
//	try {
//		currentJar = new File(FingerPlayServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
//	} catch (URISyntaxException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		this.dispose();
//		return;
//	}
//	 
//	  /* is it a jar file? */
//	  if(!currentJar.getName().endsWith(".jar"))
//	    return;
//
//	  /* Build command: java -jar application.jar */
//	  final ArrayList<String> command = new ArrayList<String>();
//	  command.add(javaBin);
//	  command.add("-jar");
//	  command.add(currentJar.getPath());
//
//	  final ProcessBuilder builder = new ProcessBuilder(command);
//	  try {
//		builder.start();
//		  System.exit(0);
//	} catch (IOException e) {
//		e.printStackTrace();
//		this.dispose();
//		return;
//	}
//	  
//	  System.out.println("Restart done !");
//	  this.dispose();
//	
//	}
//	

}
