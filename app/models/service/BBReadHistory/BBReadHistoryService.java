package models.service.BBReadHistory;

import models.entity.BBItemHead;
import models.entity.BBReadHistory;
import models.entity.User;
import models.request.api.bbanalyzer.BBReadHistoryItem;
import models.request.api.bbanalyzer.BBReadHistoryRequest;
import models.response.api.bbanalyzer.BBReadHistoryResponse;
import models.service.BBItemHead.BBItemHeadModelService;
import models.service.BBNaiveBayesParam.BBNaiveBayesParamService;
import models.service.User.UserModelService;
import models.service.api.bbanalyzer.BBAnalyzerService;

public class BBReadHistoryService {
	
	public static BBReadHistoryService use() {
		return new BBReadHistoryService();
	}
	
	
	/**
	 * BBReadHistoryRequest を処理する
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	public BBReadHistoryResponse storeReceivedHistory(BBReadHistoryRequest request) throws Exception {
		
		// User 取得
		User user = UserModelService.use().findByNitechId(request.hashedNitechId);
		if (user == null) {
			// 新規 User 作成
			user = new User(request.hashedNitechId);
			
			// User の保存
			if (user.store() == null) {
				// 保存に失敗した場合は internalServerError を返す
				return new BBReadHistoryResponse(BBAnalyzerService.use().getInternalErrorResponse());
			}
		}
		
		
		// 各 ReadHistory を保存
		for (BBReadHistoryItem historyItem : request.histories) {
			// BBItemHead 取得
			BBItemHead item = BBItemHeadModelService.use().findByUserDateIndex(user, historyItem.getIdDate(), historyItem.getIdIndex());
			if (item == null) {
				// 新規 BBItemHead 作成
				item = new BBItemHead(historyItem.getIdDate(), historyItem.getIdIndex(), null, null, null);
				
				// BBItemHead の保存
				if (item.store() == null) {
					// 保存に失敗した場合は internalServerError を返す
					return new BBReadHistoryResponse(BBAnalyzerService.use().getInternalErrorResponse());
				}
			}
			
			// すでに同じ ReadHistory が保存されていないか確認
			if (BBReadHistoryModelService.use().findByUserHeadTime(user, item, historyItem.getOpenTime()) != null) {
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
				return new BBReadHistoryResponse(BBAnalyzerService.use().getInternalErrorResponse());
			}
		}
		
		
		// ベイズ推定用パラメータの設定
		BBNaiveBayesParamService.use().calcParam(user);
		
		
		// 成功
		BBReadHistoryResponse response = new BBReadHistoryResponse(BBAnalyzerService.use().getOKResponse());
		response.setMessage("OK");
		return response;
	}
}
