package models.request.api.bb;

import java.util.List;

public class SyncPossessionsRequest {
	public String nitechId;
	public List<Entry> posts;
	
	public static class Entry {
		public String idDate;
		public int idIndex;
		public boolean isFavorite;
	}
}
