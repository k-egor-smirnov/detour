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

    public void init() {
        open();

        try {
            String queryJoins = "CREATE TABLE IF NOT EXISTS `joins` (\n" +
                    "\t`uuid`\tCHAR ( 36 ) NOT NULL UNIQUE,\n" +
                    "\t`event`\tINT ( 4 ),\n" +
                    "\t`reviewer`\tBOOLEAN DEFAULT 0,\n" +
                    "\t`ignore`\tBOOLEAN DEFAULT 0,\n" +
                    "\t`location`\tTEXT\n" +
                    ");";

            String queryEvents = "CREATE TABLE IF NOT EXISTS `events` (\n" +
                    "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`active`\tBOOLEAN DEFAULT 1\n" +
                    ");";

            statement.addBatch(queryJoins);
            statement.addBatch(queryEvents);

            statement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        close();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        close();

        return eventId;
    }

    public void closeAllEvents() {
        open();

        try {
            String query = "UPDATE events SET" +
                    "active = false" ;

            statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        close();
    }
//
//    public static void createDuoTable(String table, String column) {
//        open();
//        try {
//            String query = "CREATE TABLE IF NOT EXISTS " + table + " (" +
//                    column + " VARCHAR(100), " +
//                     "uuid VARCHAR(50));";
//            statement.execute(query);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        close();
//    }
//
//    public static ArrayList<String> selectAllUuids(String table) {
//        open();
//        ArrayList<String> list = new ArrayList<>();
//        String query = "SELECT uuid " +
//                "FROM " + table;
//        try(ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                String name = rs.getString("uuid");
//                list.add(name);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//        close();
//        return list;
//    }
//
//    public static String selectById(String uuid, String table, String column) {
//        open();
//        String query = "SELECT "+ column +
//                " FROM " + table + " WHERE uuid='" + uuid + "';";
//        try {
//            ResultSet rs = statement.executeQuery(query);
//            if(rs.next()) {
//                String data = rs.getString(column);
//                rs.close();
//                return data;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        close();
//        return null;
//    }
//
//    public static HashMap<String, String> selectAllLocations(String table) { //получить мапу uuid:location
//        open();
//        HashMap<String, String> map = new HashMap<>();
//        String query = "SELECT uuid, location " +
//                "FROM " + table;
//        try(ResultSet rs = statement.executeQuery(query)) {
//            while (rs.next()) {
//                String loc = rs.getString("location");
//                String uuid = rs.getString("uuid");
//                map.put(uuid, loc);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        close();
//        return map;
//    }/*public static HashMap<String, Location> selectAllLocations(String table) { //получить мапу uuid:location
//        open();
//        HashMap<String, Location> map = new HashMap<>();
//        String query = "SELECT uuid, location " +
//                "FROM " + table;
//        try(ResultSet rs = statement.executeQuery(query)) {
//            World world;
//            double[] xyz = new double[3];
//            while (rs.next()) {
//                String[] loc = rs.getString("location").split("&");
//                world = Bukkit.getServer().getWorld(loc[0]);
//                for (int i = 1; i <= 3; i++) {
//                    xyz[i-1] = Double.parseDouble(loc[i]);
//                }
//                String uuid = rs.getString("uuid");
//                map.put(uuid, new Location(world, xyz[0], xyz[1], xyz[2]));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        close();
//        return map;
//    }*/
//
//    public static void insertUuidAndLocation(String uuid, Location location, String table) { //вставить запись uuid:location
//        open();
//        try {
//            String loc = location.getWorld().getName() + "&" + location.getX() + "&" + location.getY() + "&" + location.getZ();
//            String query = "INSERT INTO " + table + " (uuid, location) " +
//                    "VALUES('"+uuid+"', '" + loc + "');";
//            statement.executeUpdate(query);
//            statement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        close();
//    }
//
//    public static void insertUuid(String uuid, String table) { //вставить uuid ользователя в таблицу
//        open();
//        try {
//            String query = "INSERT INTO " + table + " (uuid) " +
//                    "VALUES('" + uuid + "');";
//            statement.executeUpdate(query);
//            statement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        close();
//    }
//
//    public static void insert(String data, String uuid, String table, String column) { //вставить запись uuid:something
//        open();
//        try {
//            String query = "INSERT INTO " + table + " (uuid, " + column + ") " +
//                    "VALUES('" + uuid + "', '" + data + "');";
//            statement.executeUpdate(query);
//            statement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        close();
//    }
//
//    public static boolean contains(String uuid, String table) { //проверить, есть uuid игрока в таблице
//        try {
//            ArrayList<String> list = selectAllUuids(table);
//            for (String s : list) {
//                if (uuid.equals(s)) return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public static void delete(String uuid, String table) { //удалить uudi из таблицы
//        open();
//        try {
//            String query = "DELETE FROM " + table +
//                    " WHERE uuid='" + uuid + "';";
//            statement.executeUpdate(query);
//            statement.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        close();
//    }
//
//    public static void deleteTable(String table) { //удалить таблицу из бд
//        open();
//        try {
//            String query = "DROP TABLE IF EXISTS " + table + ";";
//            statement.execute(query);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        close();
//    }
}