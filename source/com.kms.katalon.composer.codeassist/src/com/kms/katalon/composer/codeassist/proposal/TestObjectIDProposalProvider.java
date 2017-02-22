package com.kms.katalon.composer.codeassist.proposal;
import static com.kms.katalon.groovy.constant.GroovyConstants.FIND_TEST_OBJECT_METHOD_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;

import com.kms.katalon.composer.codeassist.constant.ImageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectIDProposalProvider extends ArtifactIDProposalProvider {

    @Override
    protected String getMethodName() {
        return FIND_TEST_OBJECT_METHOD_NAME;
    }

    @Override
    protected List<IGroovyProposal> getArtifactProposals(ContentAssistContext context) {
        List<IGroovyProposal> artifactIDProposals = new ArrayList<>();
        try {
            for (String testObjectID : getIndexingUtil()
                    .getIndexedEntityIds(WebElementEntity.getWebElementFileExtension())) {
                artifactIDProposals.add(new ArtifactIDProposal(testObjectID, ImageConstants.IMG_16_TEST_OBJECT));
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return artifactIDProposals;
    }

}
