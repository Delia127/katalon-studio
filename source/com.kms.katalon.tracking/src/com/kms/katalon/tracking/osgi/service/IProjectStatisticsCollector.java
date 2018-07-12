package com.kms.katalon.tracking.osgi.service;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.tracking.model.ProjectStatistics;

public interface IProjectStatisticsCollector {

    ProjectStatistics collect(ProjectEntity project) throws Exception;
}
