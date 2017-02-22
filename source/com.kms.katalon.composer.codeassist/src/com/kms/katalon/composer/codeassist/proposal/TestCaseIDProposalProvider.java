package com.kms.katalon.composer.codeassist.proposal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kms.katalon.groovy.constant.GroovyConstants.FIND_TEST_CASE_METHOD_NAME;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;

import com.kms.katalon.composer.codeassist.constant.ImageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseIDProposalProvider extends ArtifactIDProposalProvider {

    @Override
    protected String getMethodName() {
        return FIND_TEST_CASE_METHOD_NAME;
    }

    @Override
    protected List<IGroovyProposal> getArtifactProposals(ContentAssistContext context) {
        List<IGroovyProposal> artifactIDProposals = new ArrayList<>();
        try {
            for (String testCaseID : getIndexingUtil().getIndexedEntityIds(TestCaseEntity.getTestCaseFileExtension())) {
                artifactIDProposals.add(new ArtifactIDProposal(testCaseID, ImageConstants.IMG_16_TEST_CASE));
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return artifactIDProposals;
    }

}
