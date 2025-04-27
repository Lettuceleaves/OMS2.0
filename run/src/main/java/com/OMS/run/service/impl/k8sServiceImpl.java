package com.OMS.run.service.impl;

import com.OMS.run.tool.k8sConfig;
import com.OMS.run.service.k8sService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
public class k8sServiceImpl implements k8sService {

    @Autowired
    k8sConfig k8sConfig;

    @Override
    public String[] runJob(MultipartFile userFile, MultipartFile[] input, int timelimitms, String language) throws Exception {

        // 将配置好的 ApiClient 设置为默认客户端
        Configuration.setDefaultApiClient(k8sConfig.k8sConfigManager());

        // 创建 CoreV1Api 实例
        CoreV1Api api = new CoreV1Api();

        // 获取所有命名空间中的 Pod 列表
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);

        for (V1Pod item : list.getItems()) {
            System.out.println("Pod Name: " + item.getMetadata().getName());
            System.out.println("Namespace: " + item.getMetadata().getNamespace());
        }

        return new String[]{"Job started", "Language: " + language};
    }

    @Override
    public void test() throws ApiException, IOException {// 创建 ApiClient 实例
        // 将配置好的 ApiClient 设置为默认客户端
        Configuration.setDefaultApiClient(k8sConfig.k8sConfigManager());

        // the CoreV1Api loads default api-client from global configuration.
        CoreV1Api api = new CoreV1Api();

        // invokes the CoreV1Api client
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
        System.out.println("Listing all pods: ");
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
    }

    @Override
    public void jobTest () throws ApiException, IOException {

        // 创建 BatchV1Api 实例
        BatchV1Api batchV1Api = new BatchV1Api();

        System.out.println("Creating a Kubernetes Job...");

        // 创建 Job 的元数据
        V1ObjectMeta jobMetadata = new V1ObjectMeta()
                .name("example-job")
                .namespace("default");

        System.out.println("Created metedata success");

        // 创建 Pod 的容器
        V1Container container = new V1Container()
                .name("example-container")
                .image("busybox")
                .command(Collections.singletonList("echo"))
                .args(Collections.singletonList("Hello, Kubernetes!"));

        System.out.println("Creating container success");

        V1PodSpec podSpec = new V1PodSpec()
                .containers(java.util.Collections.singletonList(container))
                .restartPolicy("Never"); // 设置 Pod 模板的重启策略为 Never

        // 创建 Pod 的模板
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec()
                .metadata(new V1ObjectMeta().name("example-pod"))
                .spec(podSpec);

        System.out.println("Creating pod template");

        // 创建 Job 的规格
        V1JobSpec jobSpec = new V1JobSpec()
                .template(podTemplateSpec)
                .backoffLimit(4);

        System.out.println("Creating job spec success");

        // 创建 Job
        V1Job job = new V1Job()
                .metadata(jobMetadata)
                .spec(jobSpec);

        System.out.println("Creating job success");

        // 提交 Job 到 Kubernetes 集群
        try {
            // 提交 Job 到 Kubernetes 集群
            V1Job createdJob = batchV1Api.createNamespacedJob("default", job, null, null, null, null);
            System.out.println("Job created: " + createdJob.getMetadata().getName());
        } catch (ApiException e) {
            System.err.println("Failed to create Job: " + e.getResponseBody());
            e.printStackTrace();
        }

    }
}
