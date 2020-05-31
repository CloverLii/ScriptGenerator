package report.assignment;

public class SequenceAction {

	private String widgetID;	
	private String widgetType; 
	private String widgetContent;	
	
	enum WidgetType{
		Button,
		Label
	}
	
	SequenceAction(String id, String type)
	{
		this.widgetID = id;
		this.widgetType = type;
	}
	
	SequenceAction(String id, String content, String type)
	{
		this.widgetID = id;
		this.widgetContent = content;
		this.widgetType = type;
	}
	
	public String getWidgetID(){
		return widgetID;
	}
	
	public String getWidgetType(){
		return widgetType;
	}
	
	public String getWidgetContent(){
		return widgetContent;
	}
	
	public void setWidgetID(String id) {
		this.widgetID = id;
	}
	
	public void setWidgetType(String type) {
		this.widgetID = type;
	}
	
	public void setWidgetContent(String content) {
		this.widgetContent = content;
	}

}
