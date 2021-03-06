package models.service.bbanalyzer;

import models.entity.User;
import models.entity.bbanalyzer.BBItem;
import models.entity.bbanalyzer.BBReadHistory;
import models.request.api.bbanalyzer.ReadHistoryRequest;
import models.response.api.bbanalyzer.ReadHistoryResponse;
import models.service.AbstractService;
import models.service.api.bbanalyzer.BBAnalyzerService;
import models.service.model.UserModelService;
import models.service.model.bbanalyzer.BBReadHistoryModelService;

public class BBReadHistoryService extends AbstractService {

	private static final long DEFAULT_FETCH_OPENTIME_SPAN = 30L * 24L * 60L * 60L * 1000L;
	private static final double MINIMUM_VARIANCE_VALUE = 10E-5;
	private static final double STANDARD_NORMAL_DISTRIBUTION_PADDING = 2.0;
	private static final double STANDARD_NORMAL_DISTRIBUTION_MAXIMUM = 2*STANDARD_NORMAL_DISTRIBUTION_PADDING;
	
	
	public static BBReadHistoryService use() {
		return new BBReadHistoryService();
	}
	
	
	/**
	 * BBReadHistoryRequest を処理する
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	public ReadHistoryResponse storeReceivedHistory(ReadHistoryRequest request) throws Exception {
		
		// User 取得
		User user = UserModelService.use().findByNitechId(request.hashedNitechId);
		if (user == null) {
			// 新規 User 作成
			user = new User(request.hashedNitechId);
			
			// User の保存
			if (user.store() == null) {
				// 保存に失敗した場合は internalServerError を返す
				return new ReadHistoryResponse(BBAnalyzerService.use().getInternalErrorResponse());
			}
		}
		
		
		// 各 ReadHistory を保存
		for (models.request.api.bbanalyzer.ReadHistoryRequest.BBReadHistoryItem historyItem : request.histories) {
			// BBItemHead 取得
			BBItem item = new BBItem(historyItem.getIdDate(), historyItem.getIdIndex()).unique();
			if (item == null) {
				// 新規 BBItemHead 作成
				item = new BBItem(historyItem.getIdDate(), historyItem.getIdIndex());
				
				// BBItemHead の保存
				if (item.store() == null) {
					// 保存に失敗した場合は internalServerError を返す
					return new ReadHistoryResponse(BBAnalyzerService.use().getInternalErrorResponse());
				}
			}
			
			// すでに同じ ReadHistory が保存されていないか確認
			if (BBReadHistoryModelService.use().findByUserItemTime(user, item, historyItem.getOpenTime()) != null) {
				continue;
			}
			
			// BBReadHistory 作成
			String[] splitted = historyItem.getReferer().split("#", 2);
			String referer = splitted[0];
			String filter = (splitted.length == 2) ? splitted[1] : null;
			BBReadHistory history = new BBReadHistory(user, item, historyItem.getOpenTime(), historyItem.getReadTimeLength(), referer, filter);
			
			// BBReadHistory 保存
			if (history.store() == null) {
				// 保存に失敗した場合は internalServerError を返す
				return new ReadHistoryResponse(BBAnalyzerService.use().getInternalErrorResponse());
			}
		}
		
		// 成功
		ReadHistoryResponse response = new ReadHistoryResponse(BBAnalyzerService.use().getOKResponse());
		response.setMessage("OK");
		return response;
	}
	
	
	/**
	 * 掲示表示履歴から掲示をカテゴリ分け
	 * @param readHistories
	 * @return
	 * @throws Exception 
	 */
	public void categorizeFromReadHistory(User user) throws Exception {
		// TODO
//		// カテゴリ準備
//		Map<String, BBCategory> categories = new HashMap<String, BBCategory>();
//		for(String catName : BBAnalyzerSetting.CATEGORY_NAMES) {
//			BBCategory category = new BBCategory(user, catName).unique();
//			if (category == null) {
//				category = new BBCategory(user, catName).store();
//			}
//			categories.put(catName, category);
//		}
//		
//		// 表示履歴を取得
//		Long minOpenTime = Long.valueOf(System.currentTimeMillis() - DEFAULT_FETCH_OPENTIME_SPAN);
//		List<BBReadHistory> readHistories = new BBReadHistory().findListForUser(user, minOpenTime, null);
//		
//		// 作業用 Map
//		Map<BBItemHead, ParamHolder> params = new HashMap<BBItemHead, ParamHolder>();
//		for(BBReadHistory history : readHistories) {
//			BBItemHead item = history.getItem();
//			if (!params.containsKey(item)) {
//				params.put(item, new ParamHolder());
//			}
//			
//			ParamHolder param = params.get(item);
//			param.count = param.count + 1.0;
//		}
//		
//		Set<BBItemHead> items = params.keySet();
//		
//		for(BBItemHead item : items) {
//			ParamHolder param = params.get(item);
//			LogUtil.info("BBReadHistoryService#categorize...(): item = "+item.getId()+", "+item.getTitle()+", count="+param.count);
//		}
//		
//		// 記事閲覧回数の平均と分散を計算
//		double d_tmp = 0.0;
//		double d_diff_from_average = 0.0;
//		double count_average = 0.0;
//		double count_variance = 0.0;
//		double total_item_num = (double) items.size();
//		double normalized_value = 0.0;
//		for(BBItemHead item : items) {
//			ParamHolder param = params.get(item);
//			count_average += param.count / total_item_num;
//		}
//		for(BBItemHead item : items) {
//			ParamHolder param = params.get(item);
//			d_tmp = (param.count - count_average);
//			d_diff_from_average += d_tmp*d_tmp;
//		}
//		count_variance = Math.sqrt(d_diff_from_average / (total_item_num - 1.0));
//		if (count_variance < MINIMUM_VARIANCE_VALUE) {
//			count_variance = MINIMUM_VARIANCE_VALUE;
//		}
//		
//		LogUtil.info("count_average = "+count_average);
//		LogUtil.info("count_variance = "+count_variance);
//		
//		double total_category_num = (double) BBAnalyzerSetting.CATEGORY_NAMES.length;
//		double divide_value = STANDARD_NORMAL_DISTRIBUTION_MAXIMUM / total_category_num;
//		LogUtil.info("total_category_num = "+total_category_num);
//		LogUtil.info("divide_value = "+divide_value);
//		
//		// 集計結果から各記事をカテゴリ分け
//		for(BBItemHead item : items) {
//			StringBuilder sb = new StringBuilder();
//			ParamHolder param = params.get(item);
//			long itemId = item.getId().longValue();
//			
//			// 標準化
//			normalized_value = (param.count - count_average) / count_variance;
//			
//			// カテゴリ分類
//			int catNum = (int) ((normalized_value + STANDARD_NORMAL_DISTRIBUTION_PADDING) / divide_value);
//			catNum = catNum + 1;
//			sb.append("(item "+itemId+") normalized_value = "+normalized_value);
//			sb.append("\n");
//			sb.append("(item "+itemId+") with padding = "+(normalized_value + STANDARD_NORMAL_DISTRIBUTION_PADDING));
//			sb.append("\n");
//			sb.append("(item "+itemId+") calculated catNumInDouble = "+(double)(1.0 + (normalized_value + STANDARD_NORMAL_DISTRIBUTION_PADDING) / divide_value));
//			sb.append("\n");
//			sb.append("(item "+itemId+") calculated catNum="+catNum);
//			sb.append("\n");
//			LogUtil.info(sb.toString());
//			if (catNum < 1) {
//				catNum = 1;
//			} else if (BBAnalyzerSetting.CATEGORY_NAMES.length <= catNum) {
//				catNum = BBAnalyzerSetting.CATEGORY_NAMES.length;
//			}
//
//			BBCategory category = categories.get(String.valueOf(catNum));
//			if (category == null) {
//				throw new Exception("Missing category "+catNum+" for user "+user.toString());
//			}
//			
//			BBItemAppendix appendix = item.getAppendix();
//			if (appendix == null) {
//				appendix = new BBItemAppendix(item, category);
//				item.setAppendix(appendix);
//			} else {
//				appendix.setCategory(category);
//			}
//			item.store();
//		}
	}
	

	private class ParamHolder {
		public double count = 0;
	}
}
