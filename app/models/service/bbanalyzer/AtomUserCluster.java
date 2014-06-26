package models.service.bbanalyzer;

import utils.bbanalyzer.BBAnalyzerUtil;
import models.entity.User;

public class AtomUserCluster extends UserCluster {

	public User user;
	
	@Override
	public void updateVector() {
		// do nothing
	}
	
	/* toString() */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(BBAnalyzerUtil.printVector(vector));
		sb.append(", user=");
		sb.append(user);
		return sb.toString();
	}
}
