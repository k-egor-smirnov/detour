package ru.alphach1337.detour.sqlite;

import org.bukkit.Bukkit;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.models.EventParticipant;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {
    private static Database instance;

    private static Connection co;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    private void connect() {
        try {
            String url = "jdbc:sqlite:detour.db";
            co = DriverManager.getConnection(url);

            Bukkit.getLogger().info("Detour database initialized");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void close() {
        try {
            co.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int init() {
        connect();

        try {
            Statement statement = co.createStatement();

            String queryJoins = "CREATE TABLE IF NOT EXISTS `joins` (\n" +
                    "\t`uuid`\tCHAR ( 36 ) NOT NULL,\n" +
                    "\t`event`\tINT ( 4 ),\n" +
                    "\t`reviewer`\tBOOLEAN DEFAULT 0,\n" +
                    "\t`ignore`\tBOOLEAN DEFAULT 0,\n" +
                    "\t`location`\tTEXT\n" +
                    ");";

            String queryEvents = "CREATE TABLE IF NOT EXISTS `events` (\n" +
                    "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`active`\tBOOLEAN DEFAULT 1\n" +
                    ");";

            String getActiveEventQuery = "SELECT id FROM events WHERE active = 1";

            statement.addBatch(queryJoins);
            statement.addBatch(queryEvents);

            statement.executeBatch();
            ResultSet rs = statement.executeQuery(getActiveEventQuery);

            return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public ArrayList<EventParticipant> getPlayers(int eventId, boolean includeIgnored, boolean reviewer) {
        ArrayList<EventParticipant> participants = new ArrayList<>();

        String query = "SELECT * " +
                "FROM " + Settings.joinsTable + " " +
                "WHERE event = " + eventId + " " +
                (includeIgnored ? " " : " AND ignore = false ") +
                "AND reviewer = " + reviewer;

        try {
            Statement statement = co.createStatement();

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                EventParticipant participant = new EventParticipant(rs);
                
                participants.add(participant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return participants;
    }

    public void setPlayer(EventParticipant eventParticipant) {
        try {
            Statement statement = co.createStatement();
            statement.execute(eventParticipant.getSQLUpdateQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public EventParticipant getPlayerInEvent(int event, UUID uuid) {
        EventParticipant participant = null;

        try {
            Statement statement = co.createStatement();

            String query = "SELECT * FROM " + Settings.joinsTable + " " +
                    "WHERE event = " + event + " AND uuid = '" + uuid + "'";

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                participant = new EventParticipant(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participant;
    }

    public boolean addPlayerInEvent (int event, EventParticipant participant) {
        EventParticipant existParticipant = getPlayerInEvent(event, participant.getUUID());

        if (existParticipant != null) return false;

        try {
            Statement statement = co.createStatement();

            String query = participant.getSQLInsertQuery();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void removePlayerFromEvent(int event, EventParticipant participant) {
        try {
            Statement statement = co.createStatement();

            String query = participant.getSQLDeleteQuery();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int startEvent() {
        int eventId = -1;

        try {
            Statement statement = co.createStatement();

            String queryInsert = "INSERT INTO events DEFAULT VALUES;" ;
            String queryGetId = "SELECT last_insert_rowid() FROM events LIMIT 1";

            statement.execute(queryInsert);
            ResultSet rs = statement.executeQuery(queryGetId);

            eventId = rs.getInt(1);

            // Перенос зашедших заранее в новый обход
            String queryUpdate = "UPDATE joins SET event = " + eventId + " " +
                    "WHERE event = -1";

            statement.execute(queryUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventId;
    }

    public void closeAllEvents() {
        try {
            Statement statement = co.createStatement();

            String query = "UPDATE events SET " +
                    "active = false" ;

            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}