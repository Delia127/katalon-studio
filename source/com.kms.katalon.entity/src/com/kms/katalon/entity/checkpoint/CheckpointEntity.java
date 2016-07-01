package com.kms.katalon.entity.checkpoint;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.file.FileEntity;

public class CheckpointEntity extends FileEntity {

    private static final long serialVersionUID = -7729866738452365898L;

    private CheckpointSourceInfo sourceInfo;
    
    private List<String> columnNames;

    /** Checkpoint data (a snapshot of given data source) */
    private List<List<CheckpointCell>> checkpointData;

    /** Checkpoint data date taken */
    private Date takenDate;

    public static String getCheckpointFileExtension() {
        return ".cpt";
    }

    public CheckpointSourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(CheckpointSourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<List<CheckpointCell>> getCheckpointData() {
        return checkpointData;
    }

    public void setCheckpointData(List<List<CheckpointCell>> data) {
        this.checkpointData = data;
    }

    public Date getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(Date takenDate) {
        this.takenDate = takenDate;
    }

    @Override
    public String getFileExtension() {
        return getCheckpointFileExtension();
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEquals = super.equals(obj);
        if (!(obj instanceof CheckpointEntity)) {
            return false;
        }
        CheckpointEntity that = (CheckpointEntity) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder().append(this.getCheckpointData(), that.getCheckpointData())
                .append(this.getTakenDate(), that.getTakenDate());
        return isEquals && equalsBuilder.isEquals();
    }

}
