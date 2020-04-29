package com.kms.katalon.composer.webservice.viewmodel;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class HttpBodyEditorCompositeViewModelTest {

    private HttpBodyEditorCompositeViewModel compositeVm;

    private HttpBodyEditorViewModel editorVm;

    @Before
    public void prepare() {
        compositeVm = new HttpBodyEditorCompositeViewModel();
        editorVm = new HttpBodyEditorViewModel();
    }

    @Test
    public void testUserAllowsAutoUpdateByDefault() {
        assertEquals(true, editorVm.doesUserAllowAutoUpdateContentType());
    }

    @Test
    public void testShouldNotCreateContentTypeHeaderWhenUserDoesNotAllowAutoUpdate() {
        WebServiceRequestEntity rq = new WebServiceRequestEntity();
        compositeVm.setModel(rq);
        editorVm.setContentTypeUpdated(true);
        editorVm.setUserAllowsAutoUpdateContentType(false);

        compositeVm.updateContentTypeByEditorViewModel(editorVm);

        List<WebElementPropertyEntity> actual1 = compositeVm.getModel().getHttpHeaderProperties();
        assertEquals(0, actual1.size());
    }

    @Test
    public void testShouldCreateContentTypeHeaderWhenUserAllowsAutoUpdate() {
        WebServiceRequestEntity rq = new WebServiceRequestEntity();
        compositeVm.setModel(rq);
        editorVm.setContentTypeUpdated(true);
        editorVm.setUserAllowsAutoUpdateContentType(true);

        compositeVm.updateContentTypeByEditorViewModel(editorVm);

        List<WebElementPropertyEntity> actual1 = compositeVm.getModel().getHttpHeaderProperties();
        assertEquals(1, actual1.size());
    }

    @Test
    public void testShouldNotUpdateContentTypeHeaderWhenUserDoesNotAllowAutoUpdate() {
        WebServiceRequestEntity rq = new WebServiceRequestEntity();
        WebElementPropertyEntity actualContentType = new WebElementPropertyEntity("Content-Type", "text/plain");
        rq.getHttpHeaderProperties().add(actualContentType);
        compositeVm.setModel(rq);
        editorVm.setContentTypeUpdated(true);
        editorVm.setContentType("application/json");
        editorVm.setUserAllowsAutoUpdateContentType(false);

        compositeVm.updateContentTypeByEditorViewModel(editorVm);

        List<WebElementPropertyEntity> actual1 = compositeVm.getModel().getHttpHeaderProperties();
        assertEquals(1, actual1.size());
        assertNotEquals("application/json", actual1.get(0).getValue());
    }

    @Test
    public void testShouldUpdateContentTypeHeaderWhenUserAllowsAutoUpdate() {
        WebServiceRequestEntity rq = new WebServiceRequestEntity();

        WebElementPropertyEntity actualContentType = new WebElementPropertyEntity("Content-Type", "text/plain");
        rq.getHttpHeaderProperties().add(actualContentType);
        compositeVm.setModel(rq);
        editorVm.setContentTypeUpdated(true);
        editorVm.setContentType("application/json");
        editorVm.setUserAllowsAutoUpdateContentType(true);

        compositeVm.updateContentTypeByEditorViewModel(editorVm);
        List<WebElementPropertyEntity> actual1 = compositeVm.getModel().getHttpHeaderProperties();

        assertEquals(1, actual1.size());
        assertEquals("application/json", actual1.get(0).getValue());
    }

}
