package com.company;

import com.company.GUI;
import com.company.model.Client;
import com.company.model.Model;
import com.company.model.Rent;
import com.company.model.Ski;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBWorker{

    private static int GMT = 8;
    private final String PATH_TO_DB_FILE = "database.db";
    private final String URL = "jdbc:sqlite:" + PATH_TO_DB_FILE;
    public static Connection conn;

    public void initDB() {
        try {
            conn = DriverManager.getConnection(URL);
            if (conn != null) {
                //createDB();
                getAllSki();
                getAllClient();
                getAllRent();
            }
        } catch (SQLException | ParseException ex) {
            System.out.println("Ошибка подключения к БД: " + ex);
        }
    }

    public static void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    public static void createDB() throws SQLException {
        Statement statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'ski' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'size' INTEGER, 'quant' INTEGER, UNIQUE(id));");
        statmt.execute("CREATE TABLE if not exists 'client' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'fio' TEXT, 'documentName' TEXT, 'documentVal' TEXT, 'phoneNumber' TEXT, 'height' INTEGER, 'weight' INTEGER, UNIQUE(id));");
        statmt.execute("CREATE TABLE if not exists 'rent' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'dateTime' DATETIME, 'additional' text, 'ski_id' INTEGER NOT NULL, 'client_id' INTEGER NOT NULL, 'inRent' INTEGER , 'hourRent' INTEGER , FOREIGN KEY (ski_id) REFERENCES ski (id),  FOREIGN KEY (client_id) REFERENCES client (id));");
    }


    //Добавление проката
    public static void addRent(Client client, Ski ski, int hour, String add) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO rent(dateTime, additional,ski_id, client_id, inRent, hourRent) " +
                        "VALUES(datetime('now', '+"+(hour+GMT)+" hours'),?,?,?,?,?)");
        statement.setObject(1, add);
        statement.setObject(2, getIdSki(ski.getSize()));
        statement.setObject(3, client.getId());
        statement.setObject(4, 1);
        statement.setObject(5, hour);
        statement.execute();
        statement.close();
    }

    public static void getAllRent() throws SQLException, ParseException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM rent WHERE inRent='1'");
        ArrayList<Rent> ar = new ArrayList();
        while (resultSet.next()) {
            ar.add(new Rent(resultSet.getInt("id"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString("dateTime")), skiFromId(resultSet.getInt("ski_id")), clientFromId(resultSet.getInt("client_id")), resultSet.getString("additional"), resultSet.getInt("hourRent") ));
        }
        resultSet.close();
        Model.setRentList(ar);
    }

    public static ArrayList<Rent> getAllRentHave(int day) throws SQLException, ParseException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM rent WHERE datetime >= Datetime('now', '-"+((24*day)+GMT)+" hours') AND inRent='0'");
        ArrayList<Rent> ar = new ArrayList();
        while (resultSet.next()) {
            ar.add(new Rent(resultSet.getInt("id"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString("dateTime")), skiFromId(resultSet.getInt("ski_id")), clientFromId(resultSet.getInt("client_id")), resultSet.getString("additional"), resultSet.getInt("hourRent")));
        }
        resultSet.close();
        return ar;
    }

    public static boolean deleteRent(int id) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("UPDATE rent SET inRent = 0 WHERE rent.id ='"+id+"'");
        statement.close();
        return true;
    }

    public static Rent rentFromId(int id) throws SQLException, ParseException {
        PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM rent WHERE id = ?");
        statement.setObject(1, id);
        ResultSet resultSet = statement.executeQuery();
        Rent rent = new Rent(resultSet.getInt("id"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString("dateTime")), skiFromId(resultSet.getInt("ski_id")),  clientFromId(resultSet.getInt("client_id")), resultSet.getString("additional"), resultSet.getInt("hourRent"));
        statement.close();
        return rent;
    }


    //Добавление клиента
    public static void addClient(Client client) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO client(fio , documentName, documentVal, phoneNumber, height, weight) " +
                        "VALUES(?,?,?,?,?,?)");
        statement.setObject(1, client.getFio());
        statement.setObject(2, client.getDocumentName());
        statement.setObject(3, client.getDocumentVal());
        statement.setObject(4, client.getPhoneNumber());
        statement.setObject(5, client.getHeight());
        statement.setObject(6, client.getWeight());
        statement.execute();
        statement.close();
        getAllClient();
    }

    public static void updateClient(Client client) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "UPDATE client SET fio = ?,documentName = ?,documentVal = ?,phoneNumber = ?,height = ?,weight = ? WHERE documentVal = ?");
        statement.setObject(1, client.getFio());
        statement.setObject(2, client.getDocumentName());
        statement.setObject(3, client.getDocumentVal());
        statement.setObject(4, client.getPhoneNumber());
        statement.setObject(5, client.getHeight());
        statement.setObject(6, client.getWeight());
        statement.setObject(7, client.getDocumentVal());
        statement.execute();
        statement.close();
        getAllClient();
    }

    public static void getAllClient() throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM client");
        ArrayList<Client> clientArrayList = new ArrayList<>();
        while (resultSet.next()) {
            clientArrayList.add(new Client(resultSet.getInt("id"), resultSet.getString("fio"), resultSet.getString("documentName"), resultSet.getString("documentVal"), resultSet.getString("phoneNumber"), resultSet.getInt("height"), resultSet.getInt("weight")));
        }
        resultSet.close();
        Model.setClientList(clientArrayList);
    }

    public static Client clientFromId(int id) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM client WHERE id = ?");
        statement.setObject(1, id);
        ResultSet resultSet = statement.executeQuery();
        Client client = new Client(resultSet.getInt("id"), resultSet.getString("fio"), resultSet.getString("documentName"), resultSet.getString("documentVal"), resultSet.getString("phoneNumber"), resultSet.getInt("height"), resultSet.getInt("weight"));
        statement.close();
        return client;
    }
    

    //Добавление лыж
    public static void addSki(Ski ski) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO ski(size,quant) " +
                        "VALUES(?,?)");

        statement.setObject(1, ski.getSize());
        statement.setObject(2, ski.getQuant());
        statement.execute();
        statement.close();
        getAllSki();
    }

    public static void updateSki(Ski ski) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "UPDATE ski SET size = ?,quant = ? WHERE size = ?");
        statement.setObject(1, ski.getSize());
        statement.setObject(2, ski.getQuant());
        statement.setObject(3, ski.getSize());
        statement.execute();
        statement.close();
        getAllSki();
    }

    public static void getAllSki() throws SQLException {
        try (Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT ski.size, ski.quant FROM ski");
            ArrayList<Ski> skiArrayList = new ArrayList<Ski>();
            while (resultSet.next()) {
                skiArrayList.add(new Ski(resultSet.getInt("size"), resultSet.getInt("quant")));
            }
            resultSet.close();
            Model.setSkiList(skiArrayList);
        } catch (SQLException e) {
            GUI.errorInfoBox("Ошибка базы данных");
        }
    }

    public static Ski skiFromId(int id) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM ski WHERE id = ?");
        statement.setObject(1, id);
        ResultSet resultSet = statement.executeQuery();
        Ski ski = new Ski(resultSet.getInt("size"), resultSet.getInt("quant"));
        statement.close();
        return ski;
    }

    public static int getIdSki(int size) throws SQLException {
        int id;
        PreparedStatement statement = conn.prepareStatement(
                "SELECT id FROM ski WHERE size = ?");
        statement.setObject(1, size);
        ResultSet resultSet = statement.executeQuery();
        id = resultSet.getInt("id");
        statement.close();
        return id;
    }

    public static boolean deleteSki(int size) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("DELETE FROM ski WHERE ski.size ='"+size+"'");
        statement.close();
        return true;
    }
}
