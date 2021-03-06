package models.service.model.bbanalyzer;

import java.util.Set;

import models.entity.bbanalyzer.BBItem;
import models.entity.bbanalyzer.BBItemWordCount;
import models.entity.bbanalyzer.BBWord;
import models.service.model.ModelService;

public class BBItemWordCountModelService implements ModelService<Long, BBItemWordCount> {

	public static BBItemWordCountModelService use() {
		return new BBItemWordCountModelService();
	}
	
	@Override
	public BBItemWordCount findById(Long id) {
		if (id != null) {
			BBItemWordCount o = BBItemWordCount.find.byId(id);
			return o;
		}
		return null;
	}

	@Override
	public BBItemWordCount save(BBItemWordCount entry) {
		if (entry != null) {
			entry.save();
			if (entry.getId() != null) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public void delete(BBItemWordCount entry) {
		if (entry != null) {
			entry.delete();
		}
	}
	
	
	public BBItemWordCount findByItemWord(BBItem item, BBWord word) {
		if (word != null) {
			BBItemWordCount o = BBItemWordCount.find.where().eq("item", item).eq("word", word).findUnique();
			return o;
		}
		return null;
	}
	
	
	/**
	 * Surface でエントリを探す
	 * @param entry
	 * @return
	 */
	public BBItemWordCount findByItemSurface(BBItem item, String surface) {
		if (surface != null) {
			BBWord word = BBWord.find.where().eq("surface", surface).findUnique();
			return findByItemWord(item, word);
		}
		return null;
	}
	
	
	public Set<BBItemWordCount> findSetByItem(BBItem item) {
		if (item != null) {
			return BBItemWordCount.find
								  .where()
								  		.eq("item", item)
								  .findSet();
		}
		return null;
	}
	
}
