package org.fao.ess.cstat.migration.db.connection;


import org.fao.ess.cstat.migration.logic.business.Transcode;
import org.fao.fenix.commons.utils.database.DatabaseUtils;

import javax.inject.Inject;
import java.sql.*;
import java.util.*;

public class DBAdapter {

    private static final String HOST = "127.0.0.1";
    private static final String DB = "amis-cbs-v3";
    private static final String USR = "postgres";
    private static final String PSW = "postgres";

    @Inject DatabaseUtils dbUtils;
    private static String url;
    private List<String> columns;
    private List<String> types;


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
    public void createTable(String uid, List<String> columns, List<String> types) throws Exception {

        // validation
        this.columns = columns;
        this.types = types;
        if (columns == null || types == null || columns.size() != types.size() || columns.size() == 0)
            throw new Exception("Error into the creation table step");

        // query
        List<String> dbDatatypes = createDBDataType(types);
        StringBuilder query = new StringBuilder("CREATE TABLE cstat." + '"' + uid + '"' +  " ( ");
        for (int i = 0; i < columns.size(); i++)
            query.append(columns.get(i) + " " + dbDatatypes.get(i) + " , ");
        query.setLength(query.length() - 2);
        query.append(" )");

        //connection
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(query.toString());
        try {
            statement.execute();
        }catch (SQLException e) {

            System.out.println(e.getMessage());
        }

        finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void insertValues (String uidDataset, Collection<Object[]> data) throws Exception {

        Connection connection = getConnection();
        // build string
        StringBuilder insertQuery = new StringBuilder("INSERT INTO cstat." + '"' + uidDataset + '"' + " ( ");
        for (int i = 0; i < columns.size(); i++)
            insertQuery.append(columns.get(i) + " , ");
        insertQuery.setLength(insertQuery.length() - 2);
        insertQuery.append(" )");
        insertQuery.append(" VALUES (");
        for (int i = 0; i < columns.size(); i++)
            insertQuery.append("? , ");
        insertQuery.setLength(insertQuery.length() - 2);
        insertQuery.append(" )");


        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement(insertQuery.toString());

        try{

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < ((List<Object[]>) data).get(i).length; j++)
                statement.setObject(j+1, ((List<Object[]>) data).get(i)[j]);
            statement.addBatch();
        }
        statement.executeBatch();

        connection.commit();
    }
        catch (SQLException e) {

            deleteTable(uidDataset);

            System.out.println(e.getMessage());
            System.out.println(e.getNextException().getMessage());

            connection.rollback();
        }

        finally {

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }

        }

        System.out.println("here");
    }

    public void deleteTable(String uid) throws Exception {
        Connection connection = getConnection();

        StringBuilder query = new StringBuilder("DROP TABLE cstat." + '"' + uid + '"');
        PreparedStatement statement =connection.prepareStatement(query.toString());

        try {
            statement.execute();
        }catch (SQLException e) {

            System.out.println(e.getMessage());
        }

        finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void transcodeData(String uid,String column, String joinDB) throws Exception {

        StringBuilder updateQuery = new StringBuilder("UPDATE cstat." + '"' + uid + '"' + " AS V ");
        updateQuery.append(" SET " + column + " = " + joinDB+".new , ");
        updateQuery.setLength(updateQuery.length() - 2);
        updateQuery.append(" FROM cstat."+joinDB );
        updateQuery.append(" WHERE V."+column+ " =  cstat."+joinDB+".old");
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(updateQuery.toString());
        try {
            statement.execute();
        }catch (SQLException e) {

            System.out.println(e.getMessage());
        }

        finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        System.out.println("here");


        }



     public Collection<Object[]> getData(String uid) throws Exception {

         Collection<Object[]> data = null;
        StringBuilder getQuery = new StringBuilder("SELECT * from cstat." + '"' + uid + '"');
         Connection connection = getConnection();


         ResultSet rs= null;
         PreparedStatement statement = connection.prepareStatement(getQuery.toString());
        try {
             rs = statement.executeQuery();
            data = dbUtils.getDataCollection(rs);
        }catch (SQLException e) {

            System.out.println(e.getMessage());
        }

        finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return data;
    }





    // Utils
    private List<String>  createDBDataType(List<String> types) {

        ArrayList<String> dbDatatypes = new ArrayList<>();

        for (int i = 0; i < types.size(); i++) {
            switch (types.get(i)) {
                case "text":
                case "code":
                case "customCode":
                    dbDatatypes.add("text");
                    break;
                case "number":
                    dbDatatypes.add("numeric");
                    break;

                case "year":
                    dbDatatypes.add("integer");
                    break;
            }
        }
        return dbDatatypes;
    }

}
