package com.kms.katalon.composer.codeassist.proposal;

import static com.kms.katalon.groovy.constant.GroovyConstants.FIND_TEST_DATA_METHOD_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;

import com.kms.katalon.composer.codeassist.constant.ImageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataIDProposalProvider extends ArtifactIDProposalProvider {

    @Override
    protected String getMethodName() {
        return FIND_TEST_DATA_METHOD_NAME;
    }

    @Override
    protected List<IGroovyProposal> getArtifactProposals(ContentAssistContext context) {
        List<IGroovyProposal> artifactIDProposals = new ArrayList<>();
        try {
            for (String testDataID : getIndexingUtil()
                    .getIndexedEntityIds(DataFileEntity.getTestDataFileExtension())) {
                artifactIDProposals.add(new ArtifactIDProposal(testDataID, ImageConstants.IMG_16_TEST_DATA));
            } 
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return artifactIDProposals;
    }

}
