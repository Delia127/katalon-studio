package com.kms.katalon.entity.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class ClonableObject implements Cloneable, Serializable {
    
    private static final long serialVersionUID = -8924641400357975601L;

    @Override
    public Object clone() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);

            byte[] bytes = bos.toByteArray();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));

            return (Object) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            return null;
        }
    }
}
