package com.frontech.bulkemail.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.frontech.bulkemail.model.EmailInfo;

public interface EmailInfoRepository extends EmailBatchRepository, PagingAndSortingRepository<EmailInfo, Long> {

	List<EmailInfo> findByEmailIn(Collection<String> emails);

	EmailInfo findByEmail(String email);
}
