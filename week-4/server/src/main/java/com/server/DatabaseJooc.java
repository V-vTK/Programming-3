package com.server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static org.jooq.impl.DSL.*;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.*;
import org.jooq.Result;
import org.jooq.Record;
import org.jooq.Record1;


public class DatabaseJooc { //JOOQ Object Oriented Querying, ORM utilized
    // Basic queries are located here but more advanced queries should be exported to other classes
    private final String DATABASE_URL;
    private Connection connection; 
    private DSLContext database;   
    private Hashtable<String, String> jsonNames = new Hashtable<String, String>();

    public DatabaseJooc(String DatabaseURL) {
        this.DATABASE_URL = DatabaseURL;
        try {
            this.connection = DriverManager.getConnection(DATABASE_URL);
            this.database = DSL.using(connection, SQLDialect.SQLITE);
        } catch (SQLException e) {
            this.connection = null;
            e.printStackTrace();
        } finally {
            jsonNames.put("location_name", "locationName");
            jsonNames.put("location_city", "locationCity");
            jsonNames.put("location_description", "locationDescription");
        }
    }

    public void createMessagesTable() throws SQLException {
        try {
            database.createTableIfNotExists("messages")
                    .column("id", SQLDataType.INTEGER.identity(true))
                    .column("location_name", SQLDataType.VARCHAR(255).nullable(false))
                    .column("location_description", SQLDataType.VARCHAR(255).nullable(false))
                    .column("location_city", SQLDataType.VARCHAR(255).nullable(false))
                    .column("original_posting_time", SQLDataType.BIGINT.nullable(false)) 
                    .constraint(constraint("PK_MESSAGES").primaryKey("id"))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DSLContext getDslContext() {
        return database;
    }

    public void createUsersTable() {
        try {
            database.createTableIfNotExists("users")
                    .column("id", SQLDataType.INTEGER.identity(true))
                    .column("username", SQLDataType.VARCHAR(32).nullable(false))
                    .column("password", SQLDataType.VARCHAR(32).nullable(false))
                    .column("email", SQLDataType.VARCHAR(32).nullable(false))
                    .constraint(constraint("PK_MESSAGES").primaryKey("id"))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String locationName, String locationDescription, String locationCity, long originalPostingTime) {
        database.insertInto(table("messages"), field("location_name"), field("location_description"), field("location_city"), field("original_posting_time"))
        .values(locationName, locationDescription, locationCity, originalPostingTime)
        .execute();
    }

    public void addUser(String username, String password, String email) {
        database.insertInto(table("users"), field("username"), field("password"), field("email"))
        .values(username, password, email)
        .execute();
    }

    public void deleteMessage(int messageId) {
        database.deleteFrom(table("messages"))
        .where(field("id").eq(messageId))
        .execute();
    }

    public void deleteUsers(int UserID) {
        database.deleteFrom(table("users"))
        .where(field("id").eq(UserID))
        .execute();
    }

    public boolean checkUsername(String username) {
        Result<Record1<Integer>> result = database.selectCount()
            .from(table("users"))
            .where(field("username").eq(username))
            .fetch();

        return result.get(0).component1() > 0;
    }

    public boolean checkPassword(String username, String password) {
        Result<Record1<Integer>> result = database.selectCount()
                .from(table("users"))
                .where(field("username").eq(username))
                .and(field("password").eq(password))
                .fetch();
    
        return result.get(0).component1() > 0;
    }


    public void deleteTableContents(String tableName) {
        database.delete(table(tableName))
                .execute();
    }

    public void fetchMessages(String tableName) {
        Result<Record> result = database.select().from(table(tableName)).fetch();
        for (Record r : result) {
            int id = r.getValue(field("id", Integer.class));
            String locationName = r.getValue(field("location_name", String.class));
            System.out.println("Message ID: " + id + ", Location Name: " + locationName);
        }
    }

    public void fetchAll(String tableName) {
        Result<Record> result = database.select().from(table(tableName)).fetch();
        List<String> columns = getTableColumns(tableName);
        for (Record r : result) {
            for (String column : columns) {
                System.out.print(r.getValue(field(column)) + "  ");
            }
            System.out.println();
        }
    }

    private List<String> getTableColumns(String tableName) {
        List<String> columnNames = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                columnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnNames;
    }


    public void dropTable(String tableName) {
        try {
            database.dropTableIfExists(tableName).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String unixToISODateTime(long unixTimestamp) {
        Instant instant = Instant.ofEpochMilli(unixTimestamp);
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    public JSONArray exportTableToJSON(String tableName) {
        Result<Record> result = database.select().from(table(tableName)).fetch();
        List<String> columns = getTableColumns(tableName);
        JSONArray jsonArray = new JSONArray();
        for (Record record : result) {
            JSONObject jsonObject = new JSONObject();
            for (String column : columns) {
                if (column.equals("original_posting_time")) {
                    Long value = record.getValue(column, Long.class);
                    jsonObject.put("originalPostingTime", unixToISODateTime(value));
                } else if(jsonNames.containsKey(column)) {
                    Object value = record.getValue(column);
                    jsonObject.put(jsonNames.get(column), value);
                }
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }


}
