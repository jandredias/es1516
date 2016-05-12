package pt.tecnico.myDrive.service.dto;

public class VariableDto implements Comparable<VariableDto>{

	private String name;
	private String value;
	
	public VariableDto(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(VariableDto o) {
		return getName().compareTo(o.getName());
	}
	
}
