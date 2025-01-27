package com.crawler.fetcher.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolManager {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            1,  // Nombre initial de threads
            50, // Taille maximale du pool
            60, // Temps d'attente avant la fermeture d'un thread inactif
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000) // Limite du nombre de tâches en attente
    );

    public void monitorThreadPool() {
        int activeThreads = threadPoolExecutor.getActiveCount();
        int poolSize = threadPoolExecutor.getPoolSize();
        int corePoolSize = threadPoolExecutor.getCorePoolSize();
        int maxPoolSize = threadPoolExecutor.getMaximumPoolSize();
        int queueSize = threadPoolExecutor.getQueue().size();

        logger.info("ThreadPool Status: ActiveThreads = {}, PoolSize = {}, CorePoolSize = {}, MaxPoolSize = {}, QueueSize = {}",
                activeThreads, poolSize, corePoolSize, maxPoolSize, queueSize);

        // Ajustements dynamiques (si nécessaire)
        if (activeThreads > corePoolSize * 0.75 && maxPoolSize < 100) {
            threadPoolExecutor.setMaximumPoolSize(Math.min(maxPoolSize + 5, 100));
            logger.info("Increased max pool size to {}", threadPoolExecutor.getMaximumPoolSize());
        } else if (activeThreads < corePoolSize * 0.25 && maxPoolSize > 10) {
            threadPoolExecutor.setMaximumPoolSize(Math.max(corePoolSize, 10));
            logger.info("Reduced max pool size to {}", threadPoolExecutor.getMaximumPoolSize());
        }
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public void shutdown() {
        threadPoolExecutor.shutdown();
        try {
            if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPoolExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPoolExecutor.shutdownNow();
        }
    }
}