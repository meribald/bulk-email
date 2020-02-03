package com.frontech.bulkemail.dao;

import java.util.List;

import com.frontech.bulkemail.model.EmailInfo;

public interface EmailBatchRepository {

	public void createBatch(List<EmailInfo> emailInfos);

	public void updateBatch(List<EmailInfo> emailInfos);

}
