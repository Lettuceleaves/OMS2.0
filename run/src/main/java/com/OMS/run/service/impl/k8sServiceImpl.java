package com.OMS.run.service.impl;

import com.OMS.run.tool.k8sConfig;
import com.OMS.run.service.k8sService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
