package com.OMS.run.tool.impl;

import com.OMS.run.tool.k8sConfig;
import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobStatus;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Watch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class k8sWatcher implements CommandLineRunner {
    private static final String namespace = "default";
    private static final ConcurrentHashMap<String, Object> jobDeleteLocks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Object> jobCallBackLocks = new ConcurrentHashMap<>();
    private final ApiClient client;
    private final BatchV1Api api;
    private final ExecutorService watchExecutor;

    public k8sWatcher(ApiClient client) throws IOException {
        this.client = client;
        this.api = new BatchV1Api(client);
        Configuration.setDefaultApiClient(client);
        this.watchExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * 启动监听线程，监控 Job 的事件
     */
    public void startWatching() {
        watchExecutor.submit(() -> {
            try {
                Watch<V1Job> watch = Watch.createWatch(
                        client,
                        api.listNamespacedJobCall(namespace, null, null, null, null, null, null, null, null, null, true, null),
                        new TypeToken<Watch.Response<V1Job>>() {}.getType());

                System.out.println("Started watching for Job events in namespace: " + namespace);

                // 持续监听事件
                for (Watch.Response<V1Job> item : watch) {
                    String jobName = item.object.getMetadata().getName();
                    String eventType = item.type;
                    System.out.printf("Watch event: Job %s, Event %s%n", jobName, eventType);

                    // 检测到Job删除事件
                    if ("DELETED".equals(eventType)) {
                        System.out.println("Deleting job " + jobName);
                        Object lock = jobDeleteLocks.get(jobName);
                        if (lock != null) {
                            synchronized (lock) {
                                lock.notify(); // 唤醒挂起的线程
                            }
                            System.out.println("Callback triggered for Job: " + jobName);
                        }
                    } else if ("MODIFIED".equals(eventType)) {
                        V1JobStatus status = item.object.getStatus();
                        if (status != null) {
                            if (status.getSucceeded() != null && status.getSucceeded() > 0) {
                                System.out.println("Job completed successfully: " + jobName);
                                Object lock = jobCallBackLocks.get(jobName);
                                if (lock != null) {
                                    synchronized (lock) {
                                        lock.notify(); // 唤醒挂起的线程
                                    }
                                    System.out.println("Callback triggered for Job: " + jobName);
                                }
                            } else if (status.getFailed() != null && status.getFailed() > 0) {
                                System.out.println("Job failed: " + jobName);
                            }
                        }
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 关闭监听线程
     */
    public void shutdown() {
        watchExecutor.shutdown();
    }

    @Override
    public void run(String... args) throws Exception {
        // 在 Spring 启动时自动启动监听
        startWatching();
    }

    /**
     * 删除指定的 Job，并等待回调触发
     *
     * @param jobName 要删除的 Job 名称
     * @throws ApiException 如果 Kubernetes API 调用失败
     * @throws InterruptedException 如果线程被中断
     */
    public void deleteJobAndWait(String jobName) throws ApiException, InterruptedException {
        try {
            // 尝试读取指定命名空间中的 Job
            api.readNamespacedJob(jobName, namespace, null);
            // 如果 Job 存在，则删除
            api.deleteNamespacedJob(jobName, namespace, null, null, null, null, null, null);

            System.out.println("Deleting job: " + jobName);


            // 创建锁对象
            Object lock = new Object();
            jobDeleteLocks.put(jobName, lock);

            synchronized (lock) {
                try {
                    System.out.println("Waiting for lock to release job: " + jobName);
                    // 设置超时时间为30秒
                    lock.wait(30000);
                    System.out.println("Timeout waiting for Job deletion callback: " + jobName);
                } catch (InterruptedException e) {
                    // 如果线程被中断，说明等待被中断，而不是超时
                    e.printStackTrace();
                    throw (e);
                }
            }
        } catch (ApiException e) {
            // 如果读取 Job 时抛出异常
            if (e.getCode() != 404) {
                // 如果不是 404 错误（即 Job 不存在），说明有其他问题，抛出异常
                throw e;
            }
            // 如果是 404 错误（Job 不存在），则忽略异常，直接继续执行
            System.out.println("Job " + jobName + " does not exist, skipping deletion.");
            return;
        }


        System.out.println("Job deletion thread resumed for Job: " + jobName);
    }

    public void runJobWait(String jobName) throws ApiException, InterruptedException {
        try {
            // 尝试读取指定命名空间中的 Job
            api.readNamespacedJob(jobName, namespace, null);
            // 创建锁对象
            Object lock = new Object();
            jobCallBackLocks.put(jobName, lock);

            synchronized (lock) {
                try {
                    System.out.println("Waiting for job completion callback: " + jobName);
                    // 设置超时时间为30秒
                    lock.wait(30000);
                    System.out.println("Timeout waiting for Job completion callback: " + jobName);
                } catch (InterruptedException e) {
                    // 如果线程被中断，说明等待被中断，而不是超时
                    e.printStackTrace();
                    throw (e);
                }
            }
        } catch (ApiException e) {
            // 如果读取 Job 时抛出异常
            if (e.getCode() != 404) {
                // 如果不是 404 错误（即 Job 不存在），说明有其他问题，抛出异常
                throw e;
            }
            // 如果是 404 错误（Job 不存在），则忽略异常，直接继续执行
            System.out.println("Job " + jobName + " does not exist, skipping wait.");
            return;
        }

        System.out.println("Job completion thread resumed for Job: " + jobName);
    }
}
