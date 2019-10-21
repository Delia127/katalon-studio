package com.kms.katalon.controller;

import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.testdata.DBData;
import com.kms.katalon.entity.checkpoint.DatabaseCheckpointSourceInfo;

public class DatabaseCheckpointSourceController implements CheckpointSourceController<DatabaseCheckpointSourceInfo> {

    private static DatabaseCheckpointSourceController instance;

    public static DatabaseCheckpointSourceController getInstance() {
        if (instance == null) {
            instance = new DatabaseCheckpointSourceController();
        }
        return instance;
    }

    @Override
    public DBData getSourceData(DatabaseCheckpointSourceInfo sourceInfo) throws Exception {
        DatabaseConnection dbConnection = DatabaseController.getInstance().getDatabaseConnection(
                sourceInfo.isUsingGlobalDBSetting(), sourceInfo.isSecureUserAccount(), sourceInfo.getUser(),
                sourceInfo.getPassword(), sourceInfo.getSourceUrl(), sourceInfo.getDriverClassName());
        if (dbConnection == null) {
            throw new IllegalArgumentException(StringConstants.CTRL_EXC_DB_CONNECTION_SETTINGS_ARE_EMPTY);
        }
        return new DBData(dbConnection, sourceInfo.getQuery());
    }

}
