package report.assignment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ISeqReader {
	public final String mEndStr = "}" + "\n" + "end";
	public final String sEndStr = "}" + "\n" + "}";
	
	public static enum WidgetType{
		Button,	
		Label,
		Text
	}
	
	public static enum ScriptType{
		Marathon,
		SWTBot
	}
	
	private String fileName;
	private static BufferedReader br;

	public ISeqReader(String fileName) {
		this.fileName = fileName;
	}
	
	private Stream<String> getAllWidigets() throws FileNotFoundException {		
		//try {
			return new BufferedReader(new FileReader(fileName))
					.lines()
					.map(str -> {
							int splitIndex = str.lastIndexOf(",");
							return str.substring(splitIndex + 1, str.length() - 1);}
					);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}		
		//return null;
	}
	
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
	
	public StringBuilder getAllActions(List<SequenceAction> objList, ScriptType scriptType) throws FileNotFoundException{
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
							//click("+")
							sBuilder.append("click(\"").append(content).append("\")").append("\n");
							break;
						case SWTBot:
							//bot.button("yesStartBtn").click();
							sBuilder.append("\t\tbot.button(\"").append(content).append("\").click();").append("\n");
							break;								
					}
					
				}
			}
		}
		return sBuilder;
	}
	
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
	
	public String readAppName(String appName) {
		return ("with_window(\"" + appName + "\"){\n");
	}	
		
}
