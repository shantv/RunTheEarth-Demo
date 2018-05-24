package run.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import run.config.View;

public class MapMyRunLink implements Serializable {
	@JsonView(View.Summary.class)
	@JsonProperty(value="href")	private String href;
	@JsonView(View.Summary.class)
	@JsonProperty(value="id")	private String id;
	
	public MapMyRunLink() {}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MapMyRunLink [href=" + href + ", id=" + id + "]";
	}

	
}
