package com.kms.katalon.dal.fileservice;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.kms.katalon.entity.file.FileEntity;

public class EntityCache {

	private Map<String, FileEntity> entities = new ConcurrentHashMap<String, FileEntity>();

	public FileEntity get(String pk) {
		return entities.get(pk.trim());
	}

	public void put(String pk, FileEntity entity) {
		if (pk != null) {
			entities.put(pk.trim(), entity);
		}
	}

	public void remove(FileEntity entity, boolean deteleFile) {
		// TODO: Clear all references of entity, to make it's eligible for
		// garbage collected
		for (String key : entities.keySet()) {
			FileEntity ent = entities.get(key);
			if (ent == entity) {
				entities.remove(key);
				if (deteleFile) {
					File file = new File(key);
					file.delete();
				}
				break;
			}
		}
	}

	public void remove(String key) {
		// TODO: Clear all references of entity, to make it's eligible for
		// garbage collected
		entities.remove(key.trim());
	}

	public boolean contains(String pk) {
		return entities.containsKey(pk.trim());
	}

	public boolean contains(FileEntity entity) {
		return entities.values().contains(entity);
	}

	public String getKey(FileEntity entity) {
		for (String key : entities.keySet()) {
			FileEntity ent = entities.get(key);
			if (ent == entity) {
				return key;
			}
		}
		return null;
	}

	public void replaceKeys(String oldKey, String newKey) {
		FileEntity value = entities.get(oldKey);
		if (value != null) {
			entities.remove(oldKey);
			entities.put(newKey, value);
		}

		for (String key : entities.keySet()) {
			if (key.startsWith(oldKey + File.separator)) {
				FileEntity childValue = entities.get(key);
				entities.remove(key);
				entities.put(newKey + File.separator + key.substring((oldKey + File.separator).length()), childValue);
			}
		}
	}

	// Serialize entities cache to file
	public void saveCache() {

	}

	// De-serialize cached entities from file
	public void loadCacheFromFile(String filePath) {

	}

	public void clear() {
		entities.clear();
	}
}
