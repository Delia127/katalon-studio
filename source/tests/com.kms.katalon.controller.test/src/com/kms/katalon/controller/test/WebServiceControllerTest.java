package com.kms.katalon.controller.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.kms.katalon.controller.WebServiceController;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WebServiceControllerTest {
    private static final Random random = new Random();

    @Test
    public void getRequestObjectTimeoutTest() {
        // Given
        WebServiceRequestEntity requestEntity = new WebServiceRequestEntity();
        requestEntity.setServiceType(RandomStringUtils.random(8));
        requestEntity.setConnectionTimeout(random.nextInt());
        requestEntity.setSocketTimeout(random.nextInt());

        String projectDir = RandomStringUtils.random(8);
        Map<String, Object> variables = new HashMap<String, Object>();

        // When
        RequestObject request = WebServiceController.getRequestObject(requestEntity, projectDir, variables);

        // Then
        Assert.assertEquals(requestEntity.getConnectionTimeout(), request.getConnectionTimeout());
        Assert.assertEquals(requestEntity.getSocketTimeout(), request.getSocketTimeout());
    }
}
