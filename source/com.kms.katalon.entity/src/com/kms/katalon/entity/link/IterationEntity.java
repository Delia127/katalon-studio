package com.kms.katalon.entity.link;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;

public class IterationEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String value;
    private IterationType iterationType;

    public IterationEntity() {
        setIterationType(IterationType.ALL);
    }

    @Override
    public IterationEntity clone() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);

            byte[] bytes = bos.toByteArray();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));

            return (IterationEntity) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            return null;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IterationEntity)) {
            return false;
        }
        IterationEntity that = (IterationEntity) object;
        return new EqualsBuilder().append(this.getIterationType(), that.getIterationType())
                .append(this.getValue(), that.getValue()).isEquals();
    }

    public String getValue() {
        if (value == null) {
            value = "";
        }
        return value.replace("..", "-");
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSpecificValue(String value) {
        this.value = value;
    }

    public int getFrom() {
        String stringFrom = getValue().split("-")[0];
        return Integer.valueOf(stringFrom);
    }

    public int getTo() {
        String stringTo = getValue().split("-")[1];
        return Integer.valueOf(stringTo);
    }

    public void setRangeValue(int from, int to) {
        setSpecificValue(Integer.toString(from) + "-" + Integer.toString(to));
    }

    public IterationType getIterationType() {
        return iterationType;
    }

    public void setIterationType(IterationType iterationType) {
        this.iterationType = iterationType;
    }
    
    public String getDisplayString() {
        return iterationType == IterationType.ALL ? iterationType.toString() : getValue();
    }
}
