package com.OMS.run.service.impl;

import com.OMS.run.tool.impl.k8sWatcher;
import com.OMS.run.tool.k8sConfig;
import com.OMS.run.service.k8sService;
import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.proto.V1Batch;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.CountDownLatch;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public void test() throws ApiException, IOException {
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

        // 创建 Job 的元数据
        V1ObjectMeta jobMetadata = new V1ObjectMeta()
                .name("example-job")
                .namespace("default");

        // 创建 Pod 的容器
        V1Container container = new V1Container()
                .name("example-container")
                .image("busybox")
                .command(Collections.singletonList("echo"))
                .args(Collections.singletonList("Hello, Kubernetes!"));

        V1PodSpec podSpec = new V1PodSpec()
                .containers(Collections.singletonList(container))
                .restartPolicy("Never"); // 设置 Pod 模板的重启策略为 Never

        // 创建 Pod 的模板
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec()
                .metadata(new V1ObjectMeta().name("example-pod"))
                .spec(podSpec);

        // 创建 Job 的规格
        V1JobSpec jobSpec = new V1JobSpec()
                .template(podTemplateSpec)
                .backoffLimit(4);

        // 创建 Job
        V1Job job = new V1Job()
                .metadata(jobMetadata)
                .spec(jobSpec);

        System.out.println("Creating job and pod success");


        // 提交 Job 到 Kubernetes 集群
        try {
            Watch<V1Batch.Job> watch = Watch.createWatch(
                    batchV1Api.getApiClient(),
                    batchV1Api.listNamespacedJobCall("default", null, null, null, null, null, null, null, null, null, null, null),
                    new TypeToken<Watch.Response<V1Batch.Job>>() {}.getType()
            );
            // 提交 Job 到 Kubernetes 集群
            V1Job createdJob = batchV1Api.createNamespacedJob("default", job, null, null, null, null);
        } catch (ApiException e) {
            System.err.println("Failed to create Job: " + e.getResponseBody());
            e.printStackTrace();
        }

    }

    @Override
    public void jobFileTest () throws ApiException, IOException {

        try {

            ApiClient client = k8sConfig.k8sConfigManager();

            // 存储文件信息。
            createConfigMap(client, "#include <stdio.h>\n" +
                    "int main() {\n" +
                    "    printf(\"Hello, World!\\n\");\n" +
                    "    return 0;\n" +
                    "}");

            // 创建 BatchV1Api 对象，用于操作 Kubernetes 的 Job 资源。
            BatchV1Api batchV1Api = new BatchV1Api();

            // 定义 Job 的名称
            String jobName = "example-job";

            k8sWatcher k8sWatcher = new k8sWatcher(client);
            k8sWatcher.deleteJobAndWait(jobName);

            // 创建 Job 对象
            V1Job job = new V1Job();
            // 设置 Job 的 API 版本
            job.setApiVersion("batch/v1");
            // 设置 Job 的类型
            job.setKind("Job");
            // 设置 Job 的元数据，包括名称
            job.setMetadata(new V1ObjectMeta().name("example-job"));
            // 定义 Job 的 Spec（规范）
            V1JobSpec jobSpec = new V1JobSpec();

            // 定义 Pod 模板规范
            V1PodTemplateSpec templateSpec = new V1PodTemplateSpec();
            // 设置 Pod 模板的元数据，包括名称
            V1ObjectMeta templateMetadata = new V1ObjectMeta().name("example-job");
            templateSpec.setMetadata(templateMetadata);

            // 定义容器
            V1Container container = new V1Container()
                    .name("example-container") // 容器名称
                    .image("latest:c") // 使用 c 镜像
                    .command(Collections.singletonList("/bin/sh")) // 使用 shell 来执行命令
                    .args(Arrays.asList(
                            "-c",
                            "gcc /mnt/multifile/multifile.c -o /tmp/multifile && /tmp/multifile"));
            // 定义卷挂载
            V1Volume volume = new V1Volume()
                    .name("multifile-volume") // 卷的名称
                    .configMap(new V1ConfigMapVolumeSource().name("multifile-configmap")); // 使用 ConfigMap 作为卷源

            // 定义卷挂载到容器的路径
            V1VolumeMount volumeMount = new V1VolumeMount()
                    .name("multifile-volume") // 卷的名称
                    .mountPath("/mnt/multifile"); // 挂载到容器内的路径

            // 将卷挂载添加到容器
            container.addVolumeMountsItem(volumeMount);

            // 定义 Pod 的 Spec
            V1PodSpec podSpec = new V1PodSpec()
                    .addContainersItem(container) // 添加容器
                    .addVolumesItem(volume) // 添加卷
                    .restartPolicy("Never"); // 设置重启策略为 Never，即失败后不重启

            // 将 Pod 的 Spec 设置到 Pod 模板
            templateSpec.setSpec(podSpec);
            // 将 Pod 模板设置到 Job 的 Spec
            jobSpec.setTemplate(templateSpec);
            // 将 Job 的 Spec 设置到 Job
            job.setSpec(jobSpec);

            // 创建 Job，将其部署到 Kubernetes 的默认命名空间（default）
            batchV1Api.createNamespacedJob("default", job, null, null, null, null);

            k8sWatcher.runJobWait(jobName);

            // 获取 Job 的 Pod 日志
            CoreV1Api coreV1Api = new CoreV1Api();
            String namespace = "default";
            String podName = getPodNameForJob(coreV1Api, namespace, jobName);

            if (podName != null) {
                String log = coreV1Api.readNamespacedPodLog(podName, namespace, null, null, null, null, null, null, null, null, null);
                System.out.println("Pod Log: " + log);
            } else {
                System.out.println("No pod found for job: " + jobName);
            }

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private String getPodNameForJob(CoreV1Api coreV1Api, String namespace, String jobName) throws ApiException {
        V1PodList podList = coreV1Api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null, null);
        for (V1Pod pod : podList.getItems()) {
            if (pod.getMetadata().getLabels().containsKey("job-name") && pod.getMetadata().getLabels().get("job-name").equals(jobName)) {
                return pod.getMetadata().getName();
            }
        }
        return null;
    }

    private static void createConfigMap(ApiClient client, String file) throws ApiException, IOException {
        // 创建 ConfigMap API 实例
        CoreV1Api coreV1Api = new CoreV1Api(client);
        // 检查 ConfigMap 是否已经存在
        String configMapName = "multifile-configmap";
        try {
            V1ConfigMap existingConfigMap = coreV1Api.readNamespacedConfigMap(configMapName, "default", null);
            // 如果 ConfigMap 已经存在，更新它的内容
            existingConfigMap.getData().put("multifile.c", file);
            coreV1Api.replaceNamespacedConfigMap(configMapName, "default", existingConfigMap, null, null, null, null);
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                // 如果 ConfigMap 不存在，创建一个新的
                V1ConfigMap configMap = new V1ConfigMap();
                configMap.setApiVersion("v1");
                configMap.setKind("ConfigMap");
                configMap.setMetadata(new V1ObjectMeta().name("multifile-configmap"));
                Map<String, String> data = new HashMap<>();
                data.put("multifile.txt", file); // 将文件内容存储到 ConfigMap 中
                configMap.setData(data);

                // 创建 ConfigMap
                coreV1Api.createNamespacedConfigMap("default", configMap, null, null, null, null);
            } else {
                // 处理其他 API 异常
                System.err.println("Failed to create or update ConfigMap: " + e.getResponseBody());
                e.printStackTrace();
            }
        }
    }
}
