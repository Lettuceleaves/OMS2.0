package com.OMS.run.service.impl;

import com.OMS.run.service.k8sService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class k8sServiceImpl implements k8sService {

    @Override
    public String[] runJob(MultipartFile userFile, MultipartFile[] input, int timelimitms, String language) throws Exception {
        ApiClient client = new ClientBuilder().build();

        // 配置 Kubernetes API Server 的地址
        client.setBasePath("https://your-kubernetes-api-server");

        // 设置是否验证 SSL 证书（在开发环境中可以设置为 false，但在生产环境中建议设置为 true 并配置正确的证书）
        client.setVerifyingSsl(false);

        // 将配置好的 ApiClient 设置为默认客户端
        Configuration.setDefaultApiClient(client);

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
}
