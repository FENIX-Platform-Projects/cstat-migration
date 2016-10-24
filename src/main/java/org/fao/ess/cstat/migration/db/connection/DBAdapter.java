package org.fao.ess.cstat.migration.db.connection;


import org.fao.fenix.commons.utils.database.DatabaseUtils;

import javax.inject.Inject;
import java.sql.*;

public class DBAdapter {

    private static final String HOST = "127.0.0.1";
    private static final String DB = "amis-cbs-v3";
    private static final String USR = "postgres";
    private static final String PSW = "postgres";

    @Inject DatabaseUtils dbUtils;
    private static String url;


    //Connection
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        this.url = (this.url == null) ? "jdbc:postgresql://" + HOST + ":5432" + '/' + DB : this.url;
        Connection connection = DriverManager.getConnection(url, USR, PSW);
        return connection;
    }


    //business
    public boolean createTable(String uid, String[] columns, String[] types) throws Exception {

        if (columns == null || types == null || columns.length != types.length || columns.length == 0)
            throw new Exception("Error into the creation table step");

        translateDatatype(types);
        StringBuilder query = new StringBuilder("CREATE UNLOGGED TABLE cstat." + uid + " ( ");
        for (int i = 0; i < columns.length; i++)
            query.append(columns[i] + " " + types[i] + " , ");
        query.setLength(query.length() - 2);
        query.append(" )");
        PreparedStatement statement = getConnection().prepareStatement(query.toString());
        return statement.execute();
    }

    public boolean deleteTable(String uid) throws Exception {

        StringBuilder query = new StringBuilder("DROP TABLE cstat." + uid);
        PreparedStatement statement = getConnection().prepareStatement(query.toString());
        return statement.execute();
    }


    // Utils
    private void translateDatatype(String[] types) {

        for (int i = 0; i < types.length; i++) {
            switch (types[i]) {
                case "text":
                case "code":
                case "customCode":
                    types[i] = "text";
                    break;
                case "number":
                    types[i] = "numeric";
                    break;

                case "year":
                    types[i] = "integer";
                    break;
            }
        }
    }
}
