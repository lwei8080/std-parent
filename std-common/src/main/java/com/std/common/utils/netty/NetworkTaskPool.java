package com.std.common.utils.netty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkTaskPool {
	public final static ExecutorService POOL_SERVER_TASK = Executors.newFixedThreadPool(300);
	public final static ExecutorService POOL_CLIENT_TASK = Executors.newFixedThreadPool(50);
}
