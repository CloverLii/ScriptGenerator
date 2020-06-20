package comp591.prototype;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * A helper class to handel ISeq input file
 */
public class ISeqReader {
	public final String mEndStr = "}" + "\n" + "end";
	public final String sEndStr = "}" + "\n" + "}";
	
	/*
	 * WidgetType: add new values when needed
	 */
	public static enum WidgetType{
		Button,	
		Label,
		Text
	}
	
	/*
	 * ScriptType: add new type of script when needed
	 */
	public static enum ScriptType{
		Marathon,
		SWTBot
	}
	
	private String fileName;
	private static BufferedReader br;

	/*
	 * Create instance of helper class ISeqReader
	 */
	public ISeqReader(String fileName) {
		this.fileName = fileName;
	}
	
	/*
	 * Get all widgets described in ISeq file
	 */
	private Stream<String> getAllWidigets() throws FileNotFoundException {		
		return new BufferedReader(new FileReader(fileName))
				.lines()
				.map(str -> {
						int splitIndex = str.lastIndexOf(",");
						return str.substring(splitIndex + 1, str.length() - 1);}
				);

	}
	
	/*
	 * Get unique widgets described in ISeq file, 
	 * for initialization of Widget Type ComboBox on Generator
	 */
	public List<String> getDistinceWidigets() throws FileNotFoundException{		
		try {
			return getAllWidigets()
					.distinct()
					.collect(Collectors.toList());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * Construct readable script fragments for different script types,
	 * by mapping values from: 1-list of all widgets that described in ISeq file;
	 * 						   2. predefined widget types;
	 * 						   3. list of <SequenceAction> that created based on user input
	 */
	public StringBuilder getAllActions(List<SequenceAction> objList, ScriptType scriptType) throws FileNotFoundException{
		// list of all widgets
		List<String> widgetList = getAllWidigets().collect(Collectors.toList());
		StringBuilder sBuilder = new StringBuilder("");
		
		for(String str: widgetList) {
			for(SequenceAction obj: objList) {
				
				String id = obj.getWidgetID();
				String type = obj.getWidgetType();
				String content = obj.getWidgetContent();
				
				if(str.equals(id) && type.equals(WidgetType.Button.toString())) {
					switch(scriptType) {
						case Marathon:
							//template: click("+")
							sBuilder.append("click(\"").append(content).append("\")").append("\n");
							break;
						case SWTBot:
							//template: bot.button("+").click();
							sBuilder.append("\t\tbot.button(\"").append(content).append("\").click();").append("\n");
							break;								
					}
					
				}
			}
		}
		return sBuilder;
	}
	
	/*
	 * Read header in script template
	 */
	public String readScriptHead(String filePath) {
		StringBuilder headerBuilder = new StringBuilder();
	    try{
	    	BufferedReader br = new BufferedReader(new FileReader(filePath));
	        String sCurrentLine;
	        while ((sCurrentLine = br.readLine()) != null) 
	        {
	            headerBuilder.append(sCurrentLine).append("\n");
	        }
	        br.close();
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	    return headerBuilder.toString();
	}
	
	/*
	 * Get name of targeted application (user input)
	 */
	public String readAppName(String appName) {
		return ("with_window(\"" + appName + "\"){\n");
	}	
		
}
