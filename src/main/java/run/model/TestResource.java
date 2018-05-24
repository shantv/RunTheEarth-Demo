package run.model;

import java.util.List;
import java.util.Map;

import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestResource extends PagedResources<Resource<Workout>> {
    @JsonProperty("total_count") private int totalCount;
    @JsonProperty("offset") private int offset;
    @JsonProperty("_embedded") private Map<String, List<Workout>> routes;
	public TestResource() {
	}

	public Map<String, List<Workout>> getRoutes() {
		return routes;
	}

	public void setRoutes(Map<String, List<Workout>> routes) {
		this.routes = routes;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "TestResource [totalCount=" + totalCount + ", offset=" + offset + "]";
	}

}
