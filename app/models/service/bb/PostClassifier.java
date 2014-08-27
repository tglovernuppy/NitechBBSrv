package models.service.bb;

import models.entity.NitechUser;
import models.entity.bb.Post;
import models.setting.BBSetting;
import play.Logger;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.Ebean;


public class PostClassifier {

	public static PostClassifier use() {
		return new PostClassifier();
	}
	
	/**
	 * １つの掲示について他のすべての掲示との距離を計算して格納・更新する
	 * @param post
	 */
	public void calcPostDistance(Post post) {
		if (post != null) {
			Ebean.beginTransaction();
			try {
				CallableSql cSql = Ebean.createCallableSql("{call CalculatePostDistance(?)}")
										.setParameter(1, post.getId());
				Ebean.execute(cSql);
	            Ebean.execute(Ebean.createCallableSql("commit;"));
				Ebean.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(PostClassifier.class.getName()+"#calcPostDistances()", e);
				Ebean.rollbackTransaction();
			} finally {
				Ebean.endTransaction();
			}
		}
	}
	
	/**
	 * 全ての掲示について他の全ての掲示との距離を計算して格納・更新する
	 */
	public void calcPostDistances() {
		Ebean.beginTransaction();
		try {
			CallableSql cSql = Ebean.createCallableSql("{call CalculatePostDistances()}");
			Ebean.execute(cSql);
            Ebean.execute(Ebean.createCallableSql("commit;"));
			Ebean.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(PostClassifier.class.getName()+"#calcPostDistances()", e);
			Ebean.rollbackTransaction();
		} finally {
			Ebean.endTransaction();
		}
	}
	
	/**
	 * １ユーザについてその掲示閲覧記録から推定パラメータを学習させる
	 * @param nitechUser
	 */
	public void train(NitechUser nitechUser) {
		train(nitechUser, BBSetting.TRAIN_DATA_CLASSIFICATION_THRESHOLD);
	}
	
	/**
	 * １ユーザについてその掲示閲覧記録から推定パラメータを学習させる
	 * @param nitechUser
	 * @param threshold 掲示閲覧履歴から掲示の「興味あり」「興味なし」を判断する際に使う、閲覧回数を標準化した時のしきい値
	 */
	public void train(NitechUser nitechUser, double threshold) {
		if (nitechUser != null) {
			Ebean.beginTransaction();
			try {
				CallableSql cSql1 = Ebean.createCallableSql("{call PrepareTrainDataFor(?,?)}")
										 .setParameter(1, nitechUser.getId())
										 .setParameter(1, threshold);
				Ebean.execute(cSql1);
				CallableSql cSql2 = Ebean.createCallableSql("{call TrainFor(?,?)}")
										 .setParameter(1, nitechUser.getId())
										 .setParameter(1, 1);
				Ebean.execute(cSql2);
				CallableSql cSql3 = Ebean.createCallableSql("{call TrainFor(?,?)}")
										 .setParameter(1, nitechUser.getId())
										 .setParameter(1, 0);
				Ebean.execute(cSql3);
	            Ebean.execute(Ebean.createCallableSql("commit;"));
				Ebean.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(PostClassifier.class.getName()+"#train()", e);
				Ebean.rollbackTransaction();
			} finally {
				Ebean.endTransaction();
			}
		}
	}
	
	/**
	 * 全ユーザについて掲示閲覧記録から推定パラメータを学習させる
	 */
	public void train() {
		train(BBSetting.TRAIN_DATA_CLASSIFICATION_THRESHOLD);
	}
	
	/**
	 * 全ユーザについて掲示閲覧記録から推定パラメータを学習させる
	 */
	public void train(double threshold) {
		Ebean.beginTransaction();
		try {
			CallableSql cSql1 = Ebean.createCallableSql("{call PrepareTrainData(?)}")
									.setParameter(1, threshold);
			Ebean.execute(cSql1);
			CallableSql cSql2 = Ebean.createCallableSql("{call Train()}");
			Ebean.execute(cSql2);
            Ebean.execute(Ebean.createCallableSql("commit;"));
			Ebean.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(PostClassifier.class.getName()+"#train()", e);
			Ebean.rollbackTransaction();
		} finally {
			Ebean.endTransaction();
		}
	}
	
	/**
	 * 掲示のクラスを予測する
	 * @param nitechUser
	 * @param post
	 */
	public void estimate(NitechUser nitechUser, Post post) {
		if (nitechUser != null && post != null) {
			Ebean.beginTransaction();
			try {
				CallableSql cSql = Ebean.createCallableSql("{call Estimate(?,?)}")
										.setParameter(1, nitechUser.getId())
										.setParameter(2, post.getId());
				Ebean.execute(cSql);
	            Ebean.execute(Ebean.createCallableSql("commit;"));
				Ebean.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(PostClassifier.class.getName()+"#estimate()", e);
				Ebean.rollbackTransaction();
			} finally {
				Ebean.endTransaction();
			}
		}
	}
	
}
