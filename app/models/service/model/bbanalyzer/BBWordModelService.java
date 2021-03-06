package models.service.model.bbanalyzer;

import java.lang.reflect.Type;
import java.util.List;

import models.entity.bbanalyzer.BBWord;
import models.service.model.ModelService;
import utils.bbanalyzer.GsonUtil;

import com.google.gson.reflect.TypeToken;

public class BBWordModelService implements ModelService<Long, BBWord> {
	
	private static final Type GSON_TYPE_FEATURES = new TypeToken<List<String>>(){}.getType();
	
	private GsonUtil gsonUtil = new GsonUtil();

	public static BBWordModelService use() {
		return new BBWordModelService();
	}
	
	@Override
	public BBWord findById(Long id) {
		if (id != null) {
			BBWord o = BBWord.find.byId(id);
//			deserializeJson(o);
			return o;
		}
		return null;
	}

	@Override
	public BBWord save(BBWord entry) {
		if (entry != null) {
//			serializeJson(entry);
			entry.save();
			if (entry.getId() != null) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public void delete(BBWord entry) {
		if (entry != null) {
			entry.delete();
		}
	}
	
	
	/**
	 * Surface でエントリを探す
	 * @param entry
	 * @return
	 */
	public BBWord findBySurface(String surface) {
		if (surface != null) {
			BBWord o = BBWord.find.where().eq("surface", surface).findUnique();
//			deserializeJson(o);
			return o;
		}
		return null;
	}
	
	
	
//	private void deserializeJson(BBWord o) {
//		if (o != null) {
//			String jsonFeatures = o.getJsonFeatures();
//			if (jsonFeatures != null) {
//				List<String> features = gsonUtil.fromJson(jsonFeatures, GSON_TYPE_FEATURES);
//				o.setFeatures(features);
//			}
//		}
//	}
//	
//	private void serializeJson(BBWord o) {
//		if (o != null) {
//			String jsonFeatures = gsonUtil.toJson(o.getFeatures());
//			o.setJsonFeatures(jsonFeatures);
//		}
//	}

}
