package com.kms.katalon.composer.codeassist.proposal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kms.katalon.groovy.constant.GroovyConstants.FIND_CHECKPOINT_METHOD_NAME;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;

import com.kms.katalon.composer.codeassist.constant.ImageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;

public class CheckpointIDProposalProvider extends ArtifactIDProposalProvider {

    @Override
    protected String getMethodName() {
        return FIND_CHECKPOINT_METHOD_NAME;
    }

    @Override
    protected List<IGroovyProposal> getArtifactProposals(ContentAssistContext context) {
        List<IGroovyProposal> artifactIDProposals = new ArrayList<>();
        try {
            for (String checkpointID : getIndexingUtil()
                    .getIndexedEntityIds(CheckpointEntity.getCheckpointFileExtension())) {
                artifactIDProposals.add(new ArtifactIDProposal(checkpointID, ImageConstants.IMG_16_CHECK_POINT));
            } 
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return artifactIDProposals;
    }

}
