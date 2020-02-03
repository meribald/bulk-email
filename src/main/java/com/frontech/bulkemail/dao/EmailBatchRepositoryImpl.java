package com.frontech.bulkemail.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.frontech.bulkemail.model.EmailInfo;

@Resource
public class EmailBatchRepositoryImpl implements EmailBatchRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private static final int BATCH_SIZE = 25;

	@Override
	@Transactional
	public void createBatch(List<EmailInfo> emailInfos) {
		for (int i = 0; i < emailInfos.size(); ++i) {
			if (i > 0 && i % BATCH_SIZE == 0) {
				entityManager.flush();
				entityManager.clear();
			}

			entityManager.persist(emailInfos.get(i));
		}
	}

	@Override
	@Transactional
	public void updateBatch(List<EmailInfo> emailInfos) {
		for (int i = 0; i < emailInfos.size(); ++i) {
			if (i > 0 && i % BATCH_SIZE == 0) {
				entityManager.flush();
				entityManager.clear();
			}

			entityManager.merge(emailInfos.get(i));
		}
	}

}
