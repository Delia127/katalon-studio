package com.kms.katalon.execution.collector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.webui.common.internal.BrokenTestObject;
import com.kms.katalon.core.webui.common.internal.BrokenTestObjects;
import com.kms.katalon.core.webui.common.internal.SelfHealingController;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;
import com.kms.katalon.logging.LogUtil;

public class SelfHealingExecutionReportCollector {

    private static SelfHealingExecutionReportCollector _instance;

    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();

    public static SelfHealingExecutionReportCollector getInstance() {
        if (_instance == null) {
            _instance = new SelfHealingExecutionReportCollector();
        }
        return _instance;
    }

    public SelfHealingExecutionReport collect(IRunConfiguration runConfig, File reportFolder) {
        boolean isSelfHealingEnabled = (boolean) runConfig.getExecutionSetting()
                .getGeneralProperties()
                .get(RunConfiguration.SELF_HEALING_ENABLE);
        return collect(isSelfHealingEnabled, reportFolder);
    }

    public SelfHealingExecutionReport collect(boolean isSelfHealingEnabled, File reportFolder) {
        boolean canUseSelfHealing = featureService.canUse(KSEFeature.SELF_HEALING);
        boolean isEnabled = isSelfHealingEnabled && canUseSelfHealing;

        List<File> selfHealingDataFiles = new ArrayList<>();
        try {
            Files.walk(Paths.get(reportFolder.getAbsolutePath()))
                .filter(Files::isRegularFile)
                .forEach(selfHealingDataFile -> {
                    String fileName = selfHealingDataFile.getFileName().toString();
                    if (StringUtils.equals(fileName, SelfHealingController.SELF_HEALING_DATA_FILE_NAME)) {
                        selfHealingDataFiles.add(selfHealingDataFile.toFile());
                    }
                });
        } catch (IOException error) {
            LogUtil.logError(error);
        }

        boolean isTriggered = selfHealingDataFiles != null && !selfHealingDataFiles.isEmpty();

        Set<BrokenTestObject> brokenTestObjects = new HashSet<BrokenTestObject>();
        selfHealingDataFiles.stream().forEach(dataFile -> {
            Set<BrokenTestObject> pratialBrokenTestObjects = collectBrokenTestObjects(dataFile.getParentFile());
            if (pratialBrokenTestObjects != null) {
                brokenTestObjects.addAll(pratialBrokenTestObjects);
            }
        });

        return new SelfHealingExecutionReport(isEnabled, isTriggered, brokenTestObjects);
    }

    public Set<BrokenTestObject> collectBrokenTestObjects(File reportFolder) {
        String selfHealingDataFilePath = SelfHealingController
                .getSelfHealingDataFilePath(reportFolder.getAbsolutePath());
        boolean isTriggered = new File(selfHealingDataFilePath).exists();

        Set<BrokenTestObject> brokenTestObjects = null;
        if (isTriggered) {
            BrokenTestObjects brokenTestObjectsWrapper = SelfHealingController
                    .readExistingBrokenTestObjects(selfHealingDataFilePath);
            if (brokenTestObjectsWrapper != null) {
                brokenTestObjects = brokenTestObjectsWrapper.getBrokenTestObjects();
            }
        }

        return brokenTestObjects;
    }
}
