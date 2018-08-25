package ru.alphach1337.detour.sqlite;

import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import ru.alphach1337.detour.Settings;
import ru.alphach1337.detour.managers.DetourManager;
import ru.alphach1337.detour.models.EventParticipant;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {
    private static Database instance;

    private static Connection co;
    private static Statement statement;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }

        return instance;
    }

    private void open() {
        try {
            Class.forName("org.sqlite.JDBC");
            co = DriverManager.getConnection("jdbc:sqlite:detour.db");
            statement = co.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            statement.close();
            co.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int init() {
        open();

        try {
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

            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        close();

        return -1;
    }

    public ArrayList<EventParticipant> getPlayers(int eventId, boolean includeIgnored, boolean reviewer) {
        open();

        ArrayList<EventParticipant> participants = new ArrayList<EventParticipant>();

        String query = "SELECT * " +
                "FROM " + Settings.joinsTable + " " +
                "WHERE event = " + eventId + " " +
                (includeIgnored ? "" : " AND ignore = false") +
                " AND reviewer = " + reviewer;

        try (ResultSet rs = statement.executeQuery(query)) {
            double[] coordinates = new double[3];

            while (rs.next()) {
                EventParticipant participant = new EventParticipant(rs);

                participants.add(participant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        close();

        return participants;
    }

    public void setPlayer(EventParticipant eventParticipant) {
        open();

        try {
            statement.execute(eventParticipant.getSQLUpdateQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        close();
    }

    public EventParticipant getPlayerInEvent(int event, UUID uuid) {
        open();

        EventParticipant participant = null;

        try {
            String query = "SELECT * FROM " + Settings.joinsTable + " " +
                    "WHERE event = " + event + " AND uuid = '" + uuid + "'";

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                participant = new EventParticipant(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        close();

        return participant;
    }

    public boolean addPlayerInEvent (int event, EventParticipant participant) {
        EventParticipant existParticipant = getPlayerInEvent(event, participant.getUUID());

        if (existParticipant != null) return false;

        open();

        try {
            String query = participant.getSQLInsertQuery();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        close();

        return true;
    }

    public void removePlayerFromEvent(int event, EventParticipant participant) {
        open();

        try {
            String query = participant.getSQLDeleteQuery();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        close();
    }

    public int startEvent() {
        open();

        int eventId = -1;
        try {
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

        close();

        return eventId;
    }

    public void closeAllEvents() {
        open();

        try {
            String query = "UPDATE events SET " +
                    "active = false" ;

            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        close();
    }
}