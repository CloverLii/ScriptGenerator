package report.assignment;

public class ActionSequence {

	private String widgetID;	// widget name/content
	private String widgetType; // widget type, button, label
	private String widgetContent;	
	
	enum WidgetType{
		Button,
		Label
	}
	public WidgetType eleType;
	
	ActionSequence(String id, String type)
	{
		this.widgetID = id;
		this.widgetType = type;
	}
	
	ActionSequence(String id, String content, String type)
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
