package comp591.prototype;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import comp591.prototype.ISeqReader.ScriptType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.*;

public class ScriptGenerator extends JFrame {
	private JFrame frame;
	private JComboBox<String> eleInfoComBox;
	private JComboBox<String> eleTypeComBox;
	private JTextArea eleInfoArea;
	private JLabel dirLabel;
	private String filePath = "";
	private JTextField eleContentText;
	private JTextField appNameText;
	
	private final String MARATHON_BASE = "base/Script_Marathon";
	private final String SWTBOT_BASE = "base/Script_SWTBot";
	StringBuilder eleInfoPair;
	List<SequenceAction> eleFilterList;
	String appName;
    static final String hintText = " Hints:" + "\n" + " Widget ID: the idenfication of wigdet in ISeq file " + "\n" + " Widget Content:  the content shown on the widget";
	static final String[] eleTypes = new String[]{"Button", "Label", "Text"};
    String date;
    ISeqReader seq;
	
	public ScriptGenerator() {
		initialize();
	}
	
	/*
	 * Create all elements for Script Generator
	 */
	public void initialize() {
		frame = new JFrame("Script Generator");
		frame.setBounds(100, 100, 590, 550 );		
		Container container = frame.getContentPane();	
		
		dirLabel  = new JLabel(" Please select ISeq file first ");
		dirLabel.setBounds(40, 20, 360, 30);
		Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
		dirLabel.setBorder(border);
		container.add(dirLabel);
			
		JButton selectFileBtn = new JButton("Select ISeq File");
		selectFileBtn.setBounds(410, 18, 130, 35);
		selectFileBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				selectISeqFile();
			}
		});
		container.add(selectFileBtn);
				
	    String eleNameStr = "by widget name";
	    JRadioButton eleNameRadioBtn = new JRadioButton(eleNameStr);
		eleNameRadioBtn.setMnemonic(KeyEvent.VK_B);
		eleNameRadioBtn.setActionCommand(eleNameStr);
		
		String eleContentStr = "by widget content";
		JRadioButton eleTextRadioBtn = new JRadioButton(eleContentStr, true);
		eleTextRadioBtn.setMnemonic(KeyEvent.VK_B);
		eleTextRadioBtn.setActionCommand(eleContentStr);
		
		//Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(eleNameRadioBtn);
	    group.add(eleTextRadioBtn);
	    
	    JPanel p1 = new JPanel(new GridLayout(1, 2, 40, 10));
	    p1.add(eleNameRadioBtn);
	    p1.add(eleTextRadioBtn);
	    p1.setBounds(40, 80, 500, 60);
	    p1.setBorder(BorderFactory.createTitledBorder("How to idenfity widget in ISeq?"));
	    container.add(p1);
	    
	    JLabel hintLabel1 = new JLabel(" Application Title");
	    JLabel hintLabel2  = new JLabel(" Widget ID in ISeq");
	    JLabel hintLabel3 = new JLabel(" Widget Content on App");
	    JLabel hintLabel4 = new JLabel(" Widget Type");
	   
	    eleInfoComBox = new JComboBox<String>();	    
	    eleContentText = new JTextField(" ");	   
	    appNameText = new JTextField(" ");	      
	    eleTypeComBox = new JComboBox<String>();
	    		
		JButton addEleInfoBtn = new JButton("Add");
		addEleInfoBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addWidgetInfo();
			}
		});
		
	    JPanel p2s1 = new JPanel(new GridLayout(4, 1));
	    p2s1.add(hintLabel1);
	    p2s1.add(hintLabel2);
	    p2s1.add(hintLabel3);
	    p2s1.add(hintLabel4);
    
	    JPanel p2s2 = new JPanel(new GridLayout(4, 1));
	    p2s2.add(appNameText);
	    p2s2.add(eleInfoComBox);
	    p2s2.add(eleContentText);
	    p2s2.add(eleTypeComBox);
	    
	    JPanel p2 = new JPanel(new GridLayout(1, 3));
	    p2.add(p2s1);
	    p2.add(p2s2);
	    p2.add(addEleInfoBtn);
	    p2.setBounds(40, 170, 500, 140);
	    p2.setBorder(BorderFactory.createTitledBorder("Input widget info"));
	    container.add(p2);
	    
		eleInfoArea = new JTextArea(hintText);
		eleInfoArea.setEditable(false);			
		JScrollPane scroll = new JScrollPane(eleInfoArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(40, 320, 500, 120);
        container.add(scroll);	
        
		JButton generateMBtn = new JButton("Generate Marathon Scripts");
		generateMBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
					try {
						generateMarathonScript();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
			}
		});
		
		JButton generateSBtn = new JButton("Generate SWTBot Scripts");
		generateSBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try {
					generateSWTBotScript();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

	    JPanel p4 = new JPanel(new GridLayout(1, 2, 60, 10));	    
	    p4.add(generateMBtn);
	    p4.add(generateSBtn);
	    p4.setBounds(50, 460, 470, 40);
	    container.add(p4);
		    	
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/*
	 * Initialize values
	 */
	private void initializeValue() {
		// read all widgets from Iseq file and shown in eleInfoCombox
		seq = new ISeqReader(filePath);
		try {
			List<String> elements = seq.getDistinceWidigets();
			for(String eleName: elements) {
				eleInfoComBox.addItem(eleName);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// add element types for eleTypeComBox
		for(String type: eleTypes) {
			eleTypeComBox.addItem(type);
		}
		
		clean();
		appName = "";
		eleInfoPair = new StringBuilder("");
		eleFilterList = new ArrayList<SequenceAction>();
		date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
	}
	
	/*
	 * Clean inputed info when select new file
	 */
	private void clean() {
		appNameText.setText("");
		eleContentText.setText("");
		eleInfoArea.setText(hintText);
	}

	/*
	 * Select ISeq file as input
	 */
	private void selectISeqFile() {
		JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int result = fileChooser.showOpenDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
		    filePath = fileChooser.getSelectedFile().getAbsolutePath();
		    System.out.println("***** Selected file: " + filePath);
		    dirLabel.setText(filePath);
		    initializeValue();
		}
	}
	
	/*
	 *  Save generated script file to local folder
	 */
	private void saveScript(String scriptName, String content) {
		JFileChooser saveChooser = new JFileChooser();
		saveChooser.setDialogTitle("Save script file to");
		saveChooser.setSelectedFile(new File(scriptName));
		 //FileNameExtensionFilter filter = new FileNameExtensionFilter("Ruby Script", "rb");
		// saveChooser.setFileFilter(filter);
		
        int option = saveChooser.showSaveDialog(frame);       
        if(option == JFileChooser.APPROVE_OPTION){
           File file = saveChooser.getSelectedFile();
           try {
        	   		file.createNewFile();
        	   		FileWriter writer = new FileWriter(file);
        	   		writer.write(content);
        	   		writer.flush();
        	   		writer.close();
           }catch(IOException e) {
        	   		e.printStackTrace();
           }
           System.out.println("***** Save " + file.getName() + " to "+ file.getAbsolutePath());
        }
	}
	
	/*
	 * Generate script for Marathon, ruby file
	 */
	private void generateMarathonScript() throws FileNotFoundException {
		// targeted app name is one of param to generate script
		appName = appNameText.getText(); 
		System.out.println("***** App name: " + appName);
		String fileName = "MarathonScript" + date +".rb";		
		String outFileStr = "";
		
		if(filePath.trim().equals("")) {
			JOptionPane.showMessageDialog(new JFrame(),
				    "Please select an ISeq file",
				    "Information",
				    JOptionPane.PLAIN_MESSAGE);
		}else if(eleFilterList.size() == 0) {
			JOptionPane.showMessageDialog(new JFrame(),
				    "Please add widget information",
				    "Information",
				    JOptionPane.PLAIN_MESSAGE);
		}else if(appName.equals("")){
			JOptionPane.showMessageDialog(new JFrame(),
				    "Please input application name",
				    "Information",
				    JOptionPane.PLAIN_MESSAGE);
		}else {
			try {
				outFileStr = seq.readScriptHead(MARATHON_BASE)
						+ seq.readAppName(appName)
						+ seq.getAllActions(eleFilterList, ScriptType.Marathon)
						+ seq.mEndStr;
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println("******** Marathon final output script **********");
			System.out.println(outFileStr);
			saveScript(fileName, outFileStr);
		}

	}
	
	/*
	 * Generate script from SWTBot, java file
	 */
	private void generateSWTBotScript()  throws FileNotFoundException {
		String fileName = "SWTBotScript" + date +".java";
		String outFileStr = "";
		
		if(filePath.trim().equals("")) {
			JOptionPane.showMessageDialog(new JFrame(),
				    "Please select an ISeq file",
				    "Information",
				    JOptionPane.PLAIN_MESSAGE);
		}else if(eleFilterList.size() == 0) {
			JOptionPane.showMessageDialog(new JFrame(),
				    "Please add widget information",
				    "Information",
				    JOptionPane.PLAIN_MESSAGE);
		}else {
			try {
				outFileStr = seq.readScriptHead(SWTBOT_BASE)
						+ seq.getAllActions(eleFilterList, ScriptType.SWTBot)
						+ seq.sEndStr;
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println("******** SWTBot final output script **********");
			System.out.println(outFileStr);
			saveScript(fileName, outFileStr);
		}
		
	}
	
	/*
	 * Construct list of SequenceAction and show inputed info in text area
	 */
	private void addWidgetInfo() {
		String content = eleContentText.getText().trim();
	
		if(filePath.trim().equals("")) {
			JOptionPane.showMessageDialog(new JFrame(),
				    "Please select an ISeq file",
				    "Information",
				    JOptionPane.PLAIN_MESSAGE);
		}else if(content.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(),
				    "Please input widget content",
				    "Information",
				    JOptionPane.PLAIN_MESSAGE);

		}else {	
			String id = eleInfoComBox.getSelectedItem().toString().trim();
			String type = eleTypeComBox.getSelectedItem().toString().trim();
			
			// eleFilterList is one of param to generate script
			SequenceAction newObj = new SequenceAction(id, content, type);
			eleFilterList.add(newObj);
			eleInfoPair.append(id).append("--").append(content).append("--").append(type).append("\n");

			// display added widget information
			eleInfoArea.setText(eleInfoPair.toString());
		}
				
	}
	
	/*
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScriptGenerator window = new ScriptGenerator();
					window.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
