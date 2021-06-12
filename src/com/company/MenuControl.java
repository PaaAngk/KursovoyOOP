package com.company;

import com.company.model.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

public class MenuControl {
    public static ArrayList<Rent> dateRent = new ArrayList<Rent>();
    private Model data;

    public MenuControl(Model model){
        this.data = model;
        DBWorker db = new DBWorker();
        db.initDB();
    }

    public static Rent getRent(int index){
        return Model.getRentList().get(index);
    }

    // Метод добавления лыж
    public void addSki (String[] list) throws SQLException {
        boolean isHaveSize = false;
        //проверка наличия лыж такого размера
        for (Ski ski : Model.getSkiList()) {
            if(ski.getSize() == Integer.parseInt(list[0])) {
                isHaveSize = true;
            }
        }
        if (!isHaveSize){
            DBWorker.addSki(new Ski(Integer.parseInt(list[0]),Integer.parseInt(list[1])));
        }
        else {
            DBWorker.updateSki(new Ski(Integer.parseInt(list[0]),Integer.parseInt(list[1])));
            GUI.infoBox("Размер обновлен!");
        }
        DBWorker.getAllSki();
    }

    //Удаление лыж
    public boolean delete(int size) throws SQLException {
        if (data.removeSkiInList(size) && DBWorker.deleteSki(size)){
            return true;
        }
        return false;
    }

    //Проверка наличия лыж размера
    public boolean haveSkiSize(int size) {
        return data.haveSkiSize(size);
    }

    public Client clientFromDocument(String number) {
        for (Client client : Model.getClientList()) {
            if(client.getDocumentVal().equals(number)) {
                return client;
            }
        }
        return null;
    }

    // Метод добавления клиента
    public Client addClient (String fio, String documentName, String documentVal, String phoneNumber, int height, int weight)  {
        boolean isHaveClient = false;
        Client client = null;
        //Проверка на наличие такого клиента
        for (Client clients : Model.getClientList()) {
            if(clients.getFio().equals(fio) && clients.getDocumentVal().equals(documentVal) && clients.getDocumentName().equals(documentName)) {
                isHaveClient = true;
                client = clients;
            }
        }
        try {
            //Создать клиента, если его нет
            if (!isHaveClient) {
                DBWorker.addClient(new Client(1, fio, documentName, documentVal, phoneNumber, height, weight));
            }
            DBWorker.getAllClient();
        } catch (SQLException throwables) {
            GUI.errorInfoBox("Ошибка базы данных");
        }
        //Выбор клиента
        for (Client clients : Model.getClientList()) {
            if(clients.getFio().equals(fio) && clients.getDocumentVal().equals(documentVal) && clients.getPhoneNumber().equals(phoneNumber) && clients.getDocumentName().equals(documentName)) {
                client = clients;
            }
        }
        //обновить вес и рост клиента, если он отличается
        if (client.getHeight() != height || client.getWeight() != weight || !client.getPhoneNumber().equals(phoneNumber)){
            client.setHeight(height);
            client.setWeight(weight);
            client.setPhoneNumber(phoneNumber);
            try {
                DBWorker.updateClient(client);
            }catch (SQLException throwables) {
                GUI.errorInfoBox("Ошибка базы данных");
            }
        }

        return client;
    }

    //Добавление проката
    public void addRent(Client client, Ski ski, int hour, String add) {
        try {
            ski.setQuant(ski.getQuant()-1);
            if (ski.getSize() != 0) {
                DBWorker.addRent(client, ski, hour, add);
                DBWorker.updateSki(ski);
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.toString());
            GUI.errorInfoBox("Ошибка базы данных");
        }
    }

    //Возврат проката
    public void deleteRent(int value) {
        try {

            Ski ski = DBWorker.rentFromId(value).getSki();
            ski.setQuant(ski.getQuant()+1);
            DBWorker.deleteRent(value);
            DBWorker.updateSki(ski);
        } catch (SQLException | ParseException throwables) {
            GUI.errorInfoBox("Ошибка базы данных");
        }
    }

    // Метод удаление лыж
    public void deleteSki(int size) throws SQLException {
        int i = -1;
        boolean skiInRent = false;
        for(Rent rent : Model.getRentList()){
            if (rent.getSki().getSize() == size){
                skiInRent = true;
            }
        }
        if (skiInRent){
            GUI.errorInfoBox("Лыжи используются в прокате!");
        }
        else if (delete(size)){
            i++;
        }
        else if (i == -1) {
            GUI.errorInfoBox("Размер не найден!");
        }
    }

    //Проверка числа
    public static boolean verifInt(String size) {
        if (size.matches("-?\\d+(\\d+)?") ) {
            return true;
        }
        else {
            GUI.errorInfoBox("<i>Введите корректный номер !</i>");}
        return false;
    }

    //Проверка дней
    public boolean verifDay(String size) {
        if (size.matches("-?\\d+(\\d+)?") ) {
            if (Integer.parseInt(size)>=0){
                return true;
            }
            else {
                GUI.errorInfoBox("<i>Введите корректное количество дней!</i>");}
        }
        else {
            GUI.errorInfoBox("<i>Введите корректное количество дней!</i>");}
        return false;
    }

    //Проверка стоимости часа проката
    public boolean verifPrice(String size) {
        if (size.matches("-?\\d+(\\d+)?") ) {
            if (Integer.parseInt(size)>=0 && Integer.parseInt(size)<=5000){
                return true;
            }
            else {
                GUI.errorInfoBox("<i>Введите корректную сумму!</i>");}
        }
        else {
            GUI.errorInfoBox("<i>Введите корректную сумму!</i>");}
        return false;
    }

    //Проверка номера телефона
    public static boolean verifPhoneNumber(String size) {
        if (size.matches("[8]\\d{10}") ) {
            return true;
        }
        else {
            GUI.errorInfoBox("<i>Введите корректный номер телефона!</i>");}
        return false;
    }

    //Проверка имени
    public static boolean verifName(String size) {
        if (size.matches("^([A-Za-zА-Яа-я])+\\s([A-Za-zА-Яа-я])+\\s([A-Za-zА-Яа-я])+$") ) {
            return true;
        }
        else {
            GUI.errorInfoBox("<i>Введите корректное ФИО !</i>");}
        return false;
    }

    //Проверка размера лыж
    public static boolean verifSkiSize(String size) {
        if (size.matches("-?\\d+(\\d+)?") ) {
            if (Integer.parseInt(size) >= 100 && Integer.parseInt(size) <= 230 ){return true;}
            else {
                GUI.errorInfoBox("<i>Размер должен быть больше 100 см и меньше 230 см.</i>");}
        }
        else {
            GUI.errorInfoBox("<i>Неправильно введен размер</i>");}
        return false;
    }

    //Проверка количества лыж
    public static boolean verifSkiQuant (String quant) {
        if (quant.matches("-?\\d+(\\d+)?") ) {
            if (Integer.parseInt(quant)>0){
                return true;
            }
            else {
                GUI.errorInfoBox("<i>Количество должно быть больше нуля</i>");
            }
        }
        else {
            GUI.errorInfoBox("<i>Неправильно введено количество</i>");}
        return false;
    }

    //Проверка веса человека
    public static boolean verifWeight (String quant) {
        if (quant.matches("-?\\d+(\\d+)?") ) {
            if (Integer.parseInt(quant)>=30 && Integer.parseInt(quant)<=150){
                return true;
            }
            else {
                GUI.errorInfoBox("<i>Вес должен быть больше 30 и меньше 150</i>");
            }
        }
        else {
            GUI.errorInfoBox("<i>Введите правильный вес!</i>");}
        return false;
    }

    public Ski getSkiFromSize(int size) {
        return data.getSkiFromSize(size);
    }

    public Ski selectSki(int height, int weight)  {return data.selectSki(height,weight);}

    //Может ли вернуться прокат с заданным размером в течении 15 минут. Если стоит ждать возврата в течении 15 минут - true
    public static boolean between15Minute(int size){
        Date dateAfter = new Date();
        Date dateNow = new Date();
        dateAfter.setMinutes(dateAfter.getMinutes()+15);

        for (Rent rent : Model.getRentList()){
            if (rent.getSki().getSize() == size){
                if (rent.getTime().before(dateAfter) && rent.getTime().after(dateNow)){
                    return GUI.between15MinuteInfoBox(size);
                }
            }
        }
        return false;
    }


    /* Создания строки имеющихся размеров
    public  String[] getStringSizeFromList(){
        ArrayList<String> ar = new ArrayList<>();
        ar.add("Использовать подобранный");
        int count = 1;
        for (Ski ski : Model.getSkiList()){
            if (ski.getQuant()>0){
                ar.add(String.valueOf(ski.getSize()));
            }
            count++;
        }
        String[] sizeStrings = ar.toArray(new String[0]);
        return sizeStrings;
    }*/
}

