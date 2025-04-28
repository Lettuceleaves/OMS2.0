package com.OMS.run;

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class K8sJobWatcher {
    private static final String namespace = "default"; // 替换为你的命名空间
    private final ApiClient client;
    private final BatchV1Api api;
    private final ExecutorService watchExecutor;

    private ApiClient k8sConfigManager() {
        String master = "https://127.0.0.1:53728";
        String oauthToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6InE3R3RQN2xvb1FFc191a3U1Z20xcXZDS3RzYjVNQzJHZllRa28zWGstMHcifQ.eyJhdWQiOlsiaHR0cHM6Ly9rdWJlcm5ldGVzLmRlZmF1bHQuc3ZjLmNsdXN0ZXIubG9jYWwiXSwiZXhwIjoxNzQ4MzU0ODM1LCJpYXQiOjE3NDU3NjI4MzUsImlzcyI6Imh0dHBzOi8va3ViZXJuZXRlcy5kZWZhdWx0LnN2Yy5jbHVzdGVyLmxvY2FsIiwianRpIjoiNGUzYzFiMmItODBlNC00NmFjLTgwMWUtODQ2ZjgwNjQwOGJjIiwia3ViZXJuZXRlcy5pbyI6eyJuYW1lc3BhY2UiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsInNlcnZpY2VhY2NvdW50Ijp7Im5hbWUiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsInVpZCI6IjVmOGQwZThmLWVlNGQtNDAzOS04Y2QyLTA5OGU2NTg0ZDQxMiJ9fSwibmJmIjoxNzQ1NzYyODM1LCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZXJuZXRlcy1kYXNoYm9hcmQ6a3ViZXJuZXRlcy1kYXNoYm9hcmQifQ.aVlKWlzp6cHA4ITupcTS3_O-3Xea4zAPtxn8rtMJcb1u1VWnzpIFxMLTnJ9m839t9UG_5IqXtKuYkUKRE6d76wo2sNsBwbvAodFzXK2xFvDff6mQ3b7aYXTwv6WMuzNvUcm-zSey7B0Fcs9fJV2f76sRGmhflY1N-l7mDLsQBv22YoAEyHECZyzHXDOXWfUwhYcrizGDafaKE40Fnxviup6mypaQEfCPHAHeMgSAJawJKU0W5Jx25QqraU10GkJFeHVMDVTV6D12YI13LhC49KACG0xWD-fvsZ7TiB-yu_gNMkmvbxo4hy7MMMQUCf-bAvNrtZCyhzFnEy7wWxOb_g";

        ApiClient apiClient = new ClientBuilder()
                .setBasePath(master)
                .setVerifyingSsl(false)
                .setAuthentication(new AccessTokenAuthentication(oauthToken))
                .build();
        Configuration.setDefaultApiClient(apiClient);

        return apiClient;
    }

    public K8sJobWatcher() throws IOException {
        this.client = k8sConfigManager();
        this.api = new BatchV1Api(client);
        this.watchExecutor = Executors.newSingleThreadExecutor();
    }

    public void startWatching() {
        watchExecutor.submit(() -> {
            try {
                V1JobList jobList = api.listNamespacedJob(namespace, null, null, null, null, null, null, null, null, null, null);
                String resourceVersion = null;
                if (jobList.getMetadata() != null) {
                    resourceVersion = jobList.getMetadata().getResourceVersion();
                }

                Watch<V1Job> watch = Watch.createWatch(
                        client,
                        api.listNamespacedJobCall(namespace, null, null, null, null, null, null, resourceVersion, null, null, true, null),
                        new TypeToken<Watch.Response<V1Job>>() {}.getType());

                for (Watch.Response<V1Job> item : watch) {
                    if (item.object == null) {
                        System.out.println("Received event with null object: " + item.type);
                        continue;
                    }

                    String jobName = item.object.getMetadata().getName();
                    String eventType = item.type;
                    System.out.printf("Watch event: Job %s, Event %s%n", jobName, eventType);

                    // 处理事件
                    if ("ADDED".equals(eventType)) {
                        System.out.println("Job added: " + jobName);
                    } else if ("MODIFIED".equals(eventType)) {
                        System.out.println("Job modified: " + jobName);
                    } else if ("DELETED".equals(eventType)) {
                        System.out.println("Job deleted: " + jobName);
                    } else if ("ERROR".equals(eventType)) {
                        System.out.println("Error event: " + jobName);
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    public void shutdown() {
        watchExecutor.shutdown();
    }

    public static void main(String[] args) throws IOException {
        K8sJobWatcher watcher = new K8sJobWatcher();
        watcher.startWatching();

        // 保持主线程运行，以便观察事件
        System.out.println("K8sJobWatcher started. Watching for Job events...");
        try {
            Thread.sleep(6000000); // 保持运行 60 秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            watcher.shutdown();
        }
        System.out.println("K8sJobWatcher stopped. Watching for Job events...");
    }
}
