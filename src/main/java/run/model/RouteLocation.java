package run.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import run.config.View;

public class RouteLocation implements Serializable {
	@JsonView(View.Summary.class) private double lat;
	@JsonView(View.Summary.class) private double lng;
	@JsonIgnore private double dis;
	@JsonIgnore private double ele;
	
	public RouteLocation() {}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getDis() {
		return dis;
	}

	public void setDis(double dis) {
		this.dis = dis;
	}

	public double getEle() {
		return ele;
	}

	public void setEle(double ele) {
		this.ele = ele;
	}

	@Override
	public String toString() {
		return "RouteLocation [lat=" + lat + ", lng=" + lng + ", dis=" + dis + ", ele=" + ele + "]";
	}
	
}
