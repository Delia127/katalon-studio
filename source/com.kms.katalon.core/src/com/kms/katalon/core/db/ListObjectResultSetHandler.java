package com.kms.katalon.core.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * Transform <code>java.sql.ResultSet</code> data into <code>List&lt;List&lt;Object&gt;&gt;</code>
 * 
 * @see java.sql.ResultSet
 * @see org.apache.commons.dbutils.ResultSetHandler
 */
public class ListObjectResultSetHandler implements ResultSetHandler<List<List<Object>>> {

    @Override
    public List<List<Object>> handle(ResultSet rs) throws SQLException {
        List<List<Object>> result = new ArrayList<List<Object>>();
        List<Object> row;
        int cols = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            row = new ArrayList<Object>();
            for (int i = 0; i < cols; i++) {
                row.add(rs.getObject(i + 1));
            }
            result.add(row);
        }
        return result;
    }

}
