package models.response.bb;

import java.util.ArrayList;
import java.util.List;

import models.entity.bb.Post;
import models.setting.bb.api.BBStatusSetting;

public class RelevantsResponse extends BaseResponse {
	
	public List<Entry> posts = new ArrayList<Entry>();

	public RelevantsResponse() {
		super();
	}
	public RelevantsResponse(BBStatusSetting val) {
		super(val);
	}
	
	public void add(Post o, double distance){
		Entry e = new Entry();
		e.idDate = o.getIdDate();
		e.idIndex = o.getIdIndex();
		e.distance = distance;
		posts.add(e);
	}
	
	
	public class Entry {
		public String idDate;
		public int idIndex;
		public double distance;
	}

}
