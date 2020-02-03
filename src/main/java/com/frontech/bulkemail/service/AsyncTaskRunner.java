package com.frontech.bulkemail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncTaskRunner {

	private List<Callable<Void>> tasks = new ArrayList<>();

	public void addTask(Callable<Void> asyncTask) {
		tasks.add(asyncTask::call);
	}

	public void run() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(5);

		List<Future<Void>> futures = executorService.invokeAll(tasks);

		for (Future<Void> future : futures) {
			future.get();
		}

		executorService.shutdown();
	}
}
