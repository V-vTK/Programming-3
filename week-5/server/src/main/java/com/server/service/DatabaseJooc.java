package com.server.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.server.model.UserMessage;

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

public class DatabaseJooc { 
    private final String DATABASE_URL;
    private Connection connection; 
    private DSLContext dao;   

    public DatabaseJooc(String DatabaseURL) {
        this.DATABASE_URL = DatabaseURL;
        try {
            this.connection = DriverManager.getConnection(DATABASE_URL);
            this.dao = DSL.using(connection, SQLDialect.SQLITE);
        } catch (SQLException e) {
            this.connection = null;
            e.printStackTrace();
        }
    }

    public void createMessagesTable() throws SQLException {
        try {
            dao.createTableIfNotExists("messages")
                    .column("locationID", SQLDataType.INTEGER.identity(true))
                    .column("locationName", SQLDataType.VARCHAR(255).nullable(false))
                    .column("locationDescription", SQLDataType.VARCHAR(255).nullable(false))
                    .column("locationCity", SQLDataType.VARCHAR(255).nullable(false))
                    .column("locationCountry", SQLDataType.VARCHAR(255).nullable(false))
                    .column("locationStreetAddress", SQLDataType.VARCHAR(255).nullable(false))
                    .column("originalPostingTime", SQLDataType.BIGINT.nullable(false))
                    .column("originalPoster", SQLDataType.VARCHAR(255).nullable(false))
                    .column("latitude", SQLDataType.DOUBLE)
                    .column("longitude", SQLDataType.DOUBLE)
                    .column("weather", SQLDataType.VARCHAR(255))
                    .column("modified", SQLDataType.BIGINT)
                    .column("updatereason", SQLDataType.VARCHAR(255))
                    .column("timesVisited", SQLDataType.INTEGER)
                    .constraint(constraint("PK_MESSAGES").primaryKey("locationID"))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DSLContext getDslContext() {
        return dao;
    }

    public void createUsersTable() {
        try {
            dao.createTableIfNotExists("users")
                    .column("id", SQLDataType.INTEGER.identity(true))
                    .column("username", SQLDataType.VARCHAR(32).nullable(false))
                    .column("password", SQLDataType.VARCHAR(32).nullable(false))
                    .column("email", SQLDataType.VARCHAR(32).nullable(false))
                    .column("userNickname", SQLDataType.VARCHAR(32).nullable(false))
                    .constraint(constraint("PK_MESSAGES").primaryKey("id"))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addMessage(UserMessage message) {
        try {
        dao.insertInto(table("messages"),
        field("locationName"), field("locationDescription"), field("locationCity"),
        field("locationCountry"), field("locationStreetAddress"), field("originalPostingTime"),
        field("originalPoster"), field("latitude"), field("longitude"))
        .values(message.getLocationName(),message.getLocationDescription(), message.getLocationCity(),
        message.getLocationCountry(), message.getLocationStreetAddress(), message.getOriginalPostingTime(),
        message.getOriginalPoster(), message.getLatitude(), message.getLongitude())
        .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public synchronized void addUser(String username, String password, String email, String nickname) {
        dao.insertInto(table("users"), field("username"), field("password"), field("email"), field("userNickname"))
        .values(username, password, email, nickname)
        .execute();
    }

    public synchronized void deleteMessage(int messageId) {
        dao.deleteFrom(table("messages"))
        .where(field("id").eq(messageId))
        .execute();
    }


    public synchronized void deleteUsers(int UserID) {
        dao.deleteFrom(table("users"))
        .where(field("id").eq(UserID))
        .execute();
    }

    public synchronized boolean checkUsername(String username) {
        Result<Record1<Integer>> result = dao.selectCount()
            .from(table("users"))
            .where(field("username").eq(username))
            .fetch();

        return result.get(0).component1() > 0;
    }

    public synchronized boolean checkPassword(String username, String password) {
        Result<Record1<Integer>> result = dao.selectCount()
                .from(table("users"))
                .where(field("username").eq(username))
                .and(field("password").eq(password))
                .fetch();
    
        return result.get(0).component1() > 0;
    }

    public String getNickname(String username) {
        Record1<String> result = dao.select(field("userNickname", String.class))
        .from(table("users"))
        .where(field("username").eq(username))
        .fetchOne();
        System.out.println(result.component1());
        return result.component1();  
    }

    public void deleteTableContents(String tableName) {
        dao.delete(table(tableName))
                .execute();
    }

    public void fetchMessages(String tableName) {
        Result<Record> result = dao.select().from(table(tableName)).fetch();
        for (Record r : result) {
            int id = r.getValue(field("id", Integer.class));
            String locationName = r.getValue(field("locationName", String.class));
            System.out.println("Message ID: " + id + ", Location Name: " + locationName);
        }
    }

    public void fetchAll(String tableName) {
        Result<Record> result = dao.select().from(table(tableName)).fetch();
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
            dao.dropTableIfExists(tableName).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private synchronized String unixToISODateTime(long unixTimestamp) {
        Instant instant = Instant.ofEpochMilli(unixTimestamp);
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }


    public JSONArray exportTableToJSON(String tableName) {
            Result<Record> result = dao.select().from(table(tableName)).fetch();
            List<String> columns = getTableColumns(tableName);
            columns.remove("locationID");
            JSONArray jsonArray = new JSONArray();
            try {
            for (Record record : result) {
                JSONObject jsonObject = new JSONObject();
                for (String column : columns) {
                    if (column.equals("originalPostingTime")) {
                        Long value = record.getValue(column, Long.class);
                        String date = unixToISODateTime(value);
                        System.out.println(date);
                        jsonObject.put(column, date);
                    } else if (column.equals("latitude") || column.equals("longitude")) {
                        Double value = record.getValue(column, Double.class);
                        jsonObject.put(column, value);
                    } else {
                        Object value = record.getValue(column);
                        jsonObject.put(column, value);
                    }
                };
                jsonArray.put(jsonObject);
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonArray;
    }


}
