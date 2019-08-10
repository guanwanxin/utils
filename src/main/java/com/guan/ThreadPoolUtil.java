package com.guan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {
	private static Integer maxPoolSize = Integer.valueOf(150);

	private static ExecutorService executorService = Executors
			.newFixedThreadPool(maxPoolSize.intValue());

	public static ExecutorService getInstance() {
		return executorService;
	}
}
