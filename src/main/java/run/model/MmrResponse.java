package run.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import run.config.View;

public class MmrResponse<T> implements Serializable {
	@JsonView(View.Summary.class) 
	@JsonProperty(value="_links") 
	private Map<String, Object> links;
	
	@JsonView(View.Summary.class) 
	@JsonProperty(value="_embedded") 
	private Map<String, List<T>> data;
	
	@JsonView(View.Summary.class) 
	@JsonProperty(value="total_count") 
	private int totalCount;
	
	@JsonView(View.Summary.class) 
	private int offset;
	
	public MmrResponse() {}

	
	public int getOffset() {
		return offset;
	}


	public void setOffset(int offset) {
		this.offset = offset;
	}


	public Map<String, Object> getLinks() {
		return links;
	}

	public void setLinks(Map<String, Object> links) {
		this.links = links;
	}

	public Map<String, List<T>> getData() {
		return data;
	}
	public void setData(Map<String, List<T>> data) {
		this.data = data;
	}
	
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public String toString() {
		return "MmrResponse [links=" + links + ", data=" + data + ", totalCount=" + totalCount + "]";
	}
	
}
