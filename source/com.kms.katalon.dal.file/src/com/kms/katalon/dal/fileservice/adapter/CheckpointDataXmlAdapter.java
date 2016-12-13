package com.kms.katalon.dal.fileservice.adapter;

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.checkpoint.CheckpointCell;

/**
 * Checkpoint data adapter.<br>
 * This adapter converts checkpoint data from {@code List<List<CheckpointCell>>>} into JSON format vice versa.
 */
public class CheckpointDataXmlAdapter extends XmlAdapter<String, List<List<CheckpointCell>>> {

    @Override
    public String marshal(List<List<CheckpointCell>> checkpointData) throws Exception {
        return JsonUtil.toJson(checkpointData);
    }

    @Override
    public List<List<CheckpointCell>> unmarshal(String checkpointJson) throws Exception {
        return JsonUtil.fromJson(checkpointJson, new TypeToken<List<List<CheckpointCell>>>() {}.getType());
    }

}
