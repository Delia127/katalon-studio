package com.kms.katalon.controller;

import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;

public interface CheckpointSourceController<T extends CheckpointSourceInfo> {

    public Object getSourceData(T checkpointSourceInfo) throws Exception;

}
