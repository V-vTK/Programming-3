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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.jooq.impl.DSL.*;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.*;
import org.jooq.Result;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;

public class DatabaseJooc { //JOOQ Object Oriented Querying, ORM utilized
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
                    .column("id", SQLDataType.INTEGER.identity(true))
                    .column("locationID", SQLDataType.INTEGER)
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
                    .column("modified", SQLDataType.VARCHAR(255))
                    .column("updatereason", SQLDataType.VARCHAR(255))
                    .column("timesVisited", SQLDataType.INTEGER)
                    .constraint(constraint("PK_MESSAGES").primaryKey("id"))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer getMessageAmmount() {
        return dao.fetchCount(table("messages"));
    }

    public DSLContext getDslContext() {
        return dao;
    }

    public JSONArray getMostVisitedPlaces() throws SQLException {
        JSONArray jsonArray = new JSONArray();
        try {
            Result<Record3<Integer, String, Integer>> result = dao
                .select(field(name("locationID"), Integer.class), field(name("locationName"), String.class), field(name("timesVisited"), Integer.class))
                .from(table(name("messages")))
                .orderBy(field(name("timesVisited")).desc())
                .limit(5)
                .fetch();
    
            for (Record3<Integer, String, Integer> record : result) {
                Integer locationID = record.get(field(name("locationID"), Integer.class));
                String locationName = record.get(field(name("locationName"), String.class));
                Integer timesVisited = record.get(field(name("timesVisited"), Integer.class));
                System.out.println("LocationID: " + locationID + "Location: " + locationName + ", Times Visited: " + timesVisited);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("locationID", locationID);
                jsonObject.put("locationName", locationName);
                jsonObject.put("timesVisited", timesVisited);
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
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
        field("locationID"), field("locationName"), field("locationDescription"), field("locationCity"),
        field("locationCountry"), field("locationStreetAddress"), field("originalPostingTime"),
        field("originalPoster"), field("latitude"), field("longitude"), field("weather"), field("timesVisited"))
        .values(getMessageAmmount()+1, message.getLocationName(),message.getLocationDescription(), message.getLocationCity(),
        message.getLocationCountry(), message.getLocationStreetAddress(), message.getOriginalPostingTime(),
        message.getOriginalPoster(), message.getLatitude(), message.getLongitude(), message.getWeather(), 0)
        .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessageModifiedDate(UserMessage newUserMessage) {
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                                         .withZone(ZoneId.of("UTC"));
        String formattedDate = formatter.format(now);
        newUserMessage.setModified(formattedDate);
    }

    public synchronized void updateMessage(UserMessage message) {
        try {
        handleMessageModifiedDate(message);
        deleteMessageByLocationID(message.getLocationID());
        dao.insertInto(table("messages"),
        field("locationID"), field("locationName"), field("locationDescription"), field("locationCity"),
        field("locationCountry"), field("locationStreetAddress"), field("originalPostingTime"),
        field("originalPoster"), field("latitude"), field("longitude"), field("weather"), field("modified"), field("updatereason"))
        .values(message.getLocationID(), message.getLocationName(),message.getLocationDescription(), message.getLocationCity(),
        message.getLocationCountry(), message.getLocationStreetAddress(), message.getOriginalPostingTime(),
        message.getOriginalPoster(), message.getLatitude(), message.getLongitude(), message.getWeather(), message.getModified(), message.getUpdateReason())
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

    public synchronized String getMessagePoster(int messageID) {
        Result<Record> result = dao.selectFrom(table("messages")).where(field("locationID").eq(messageID)).fetch();
        System.out.println(result);
        if (!result.isEmpty()) {
            Record record = result.get(0);
            String messagePoster = record.get("originalPoster", String.class);
            return messagePoster;
        }
        return null;
    }

    public synchronized void deleteMessageByLocationID(int messageId) {
        dao.deleteFrom(table("messages"))
        .where(field("locationID").eq(messageId))
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

    public Integer getVisits(Integer locationID) {
        Record result = dao.select()
        .from(table("messages"))
        .where(field("locationID").eq(locationID))
        .fetchOne();
        System.out.println(result);
        return result.get("timesVisited", Integer.class);
    }

    public boolean addVisit(Integer locationID) {
        try {
            int visits = getVisits(locationID);
            int updatedRows = dao.update(table("messages"))
            .set(field("timesVisited"), visits+1) 
            .where(field("locationID").eq(locationID))
            .execute();
            if (updatedRows == 1) {return true;}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private synchronized String unixToISODateTime(long unixTimestamp) {
        Instant instant = Instant.ofEpochMilli(unixTimestamp);
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }


    public JSONArray exportTableToJSON(String tableName) {
            Result<Record> result = dao.select().from(table(tableName)).fetch();
            List<String> columns = getTableColumns(tableName);
            columns.remove("id");
            JSONArray jsonArray = new JSONArray();
            try {
            for (Record record : result) {
                JSONObject jsonObject = new JSONObject();
                for (String column : columns) {
                    if (column.equals("originalPostingTime")) {
                        Long value = record.getValue(column, Long.class);
                        String date = unixToISODateTime(value);
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
