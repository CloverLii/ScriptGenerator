package report.assignment;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import report.assignment.ISeqReader.ScriptType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.*;

public class ScriptGenerator extends JFrame {
	public JFrame frame;
	public JButton selectFileBtn;
	public JButton generateMBtn;
	public JButton generateSBtn;
	public JRadioButton eleNameRadioBtn;
	public JRadioButton eleTextRadioBtn;
	public JComboBox<String> eleInfoComBox;
	public JComboBox<String> eleTypeComBox;
	public JButton addEleInfoBtn;
	public JTextArea eleInfoArea;
	public JLabel dirLabel;
	public String filePath = "";
	public JScrollPane scroll;
	public JTextField eleContentText;
	public JTextField appNameText;
	
	private final String MARATHON_BASE = "base/Script_Marathon";
	private final String SWTBOT_BASE = "base/Script_SWTBot";
	StringBuilder eleInfoPair;
	List<SequenceAction> eleFilterList;
	String appName = "";
    static final String hintText = " Hints:" + "\n" + " Widget ID: the idenfication of wigdet in ISeq file " + "\n" + " Widget Content:  the content shown on the widget";
	static final String[] eleTypes = new String[]{"Button", "Label", "Text"};
    String date;
    ISeqReader seq;
	
	public ScriptGenerator() {
		initialize();
	}
	
	/*
	 * Create generator window and widgets
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
			
		selectFileBtn = new JButton("Select ISeq File");
		selectFileBtn.setBounds(410, 18, 130, 35);
		selectFileBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				selectISeqFile();
			}
		});
		container.add(selectFileBtn);
				
	    String eleNameStr = "by widget name";
		eleNameRadioBtn = new JRadioButton(eleNameStr, true);
		eleNameRadioBtn.setMnemonic(KeyEvent.VK_B);
		eleNameRadioBtn.setActionCommand(eleNameStr);
		
		String eleContentStr = "by widget content";
		eleTextRadioBtn = new JRadioButton(eleContentStr);
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
	    		
		addEleInfoBtn = new JButton("Add");
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
		scroll = new JScrollPane(eleInfoArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(40, 320, 500, 120);
        container.add(scroll);	
        
		generateMBtn = new JButton("Generate Marathon Scripts");
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
		
		generateSBtn = new JButton("Generate SWTBot Scripts");
		generateSBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				generateSWTBotScript();
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
		eleInfoPair = new StringBuilder("");
		eleFilterList = new ArrayList<SequenceAction>();
		date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
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
	 *  Save generated script file
	 */
	private void saveScript(String scriptName) {
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
        	   		FileWriter  writer = new FileWriter(file);
        	   		writer.write("This\n is\n an\n example\n");
        	   		writer.flush();
        	   		writer.close();
           }catch(IOException e) {
        	   		e.printStackTrace();
           }
           System.out.println("***** Save " + file.getName() + " to "+ file.getAbsolutePath());
        }
	}
	
	private void generateMarathonScript() throws FileNotFoundException {
		// the test script for Marathon is ruby file
		String fileName = "MarathonScript" + date +".rb";
		// targeted app name is one of param to generate script
		appName = appNameText.getText(); 
		System.out.println("***** App name: " + appName);
		String outFileStr = "";
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
		saveScript(fileName);
	}
	private void generateSWTBotScript() {
		// the test script for SWTBot is java file
		String fileName = "SWTBotScript" + date +".java";
		String outFileStr = "";
		try {
			outFileStr = seq.readScriptHead(SWTBOT_BASE)
					+ seq.getAllActions(eleFilterList, ScriptType.SWTBot)
					+ seq.mEndStr;
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("******** SWTBot final output script **********");
		System.out.println(outFileStr);
		saveScript(fileName);
	}
	/*
	 * Add widgets info to list and show in generator app
	 */
	private void addWidgetInfo() {
		String id = eleInfoComBox.getSelectedItem().toString();
		String content = eleContentText.getText();
		String type = eleTypeComBox.getSelectedItem().toString();
		
		// eleFilterList is one of param to generate script
		SequenceAction newObj = new SequenceAction(id, content, type);
		eleFilterList.add(newObj);
		eleInfoPair.append(id).append("--").append(content).append("--").append(type).append("\n");

		System.out.println("***** eleFilterList size is: " + eleFilterList.size());
		// display added widget information
		eleInfoArea.setText(eleInfoPair.toString());		
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
