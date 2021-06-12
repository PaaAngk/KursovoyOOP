package com.company.model;


import com.company.MenuControl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Model {
    private static ArrayList<Ski> skiList = new ArrayList<Ski>();
    private static ArrayList<Rent> rentList = new ArrayList<Rent>();
    private static ArrayList<Client> clientList = new ArrayList<Client>();

    public boolean removeSkiInList(int size) throws SQLException {
        return (skiList.removeIf(nextSki -> nextSki.getSize() == size));
    }

    public static Ski getSkiFromList(int index){
        return skiList.get(index);
    }

    public int getSkiListSize(){
        return skiList.size();
    }

    //Получение массива строк с имеющимися лыжами
    public static String[] getStringSizeFromList(){
        ArrayList<String> ar = new ArrayList<>();
        ar.add("Использовать подобранный");
        int count = 1;
        for (Ski ski : skiList){
            if (ski.getQuant()>0){
                ar.add(String.valueOf(ski.getSize()));
            }
            count++;
        }
        String[] sizeStrings = ar.toArray(new String[0]);
        return sizeStrings;
    }

    //Подбор лыж по росту и весу
    public Ski selectSki(int height, int weight)  {
        int BMI = calcBMI(height, weight);
        ArrayList<Integer> listSkiSize = new ArrayList<Integer>(sortSkiSize());
        ArrayList<Integer> listSkiSizeFull = new ArrayList<Integer>();
        for (Ski ski : skiList){
            listSkiSizeFull.add(ski.getSize());
        }
        Ski resSki = new Ski(1,1);
        int count= 0;

        //Значение нужного размера
        int resultSize;
        if(BMI<17){
            resultSize = height - 20;
        }
        else if (BMI>27){
            resultSize = height + 5;
        }
        else {
            resultSize = height - 10;
            //System.out.println("bmi - ok");
        }
        System.out.println(resultSize);


        //Место в listSkiSize, на котором может стоять resultSize
        int bSRes = Math.abs(Collections.binarySearch(listSkiSize, resultSize)) - 1;
        //System.out.println("bSRes = " + bSRes);
        //System.out.println("listSkiSize.size() = " + listSkiSize.size());


        //Есть лыжи нужного размера
        if (listSkiSize.size() == 0){
            resSki.setSize(2);
            return resSki;
        }
        else if (listSkiSize.contains(resultSize) && getSkiFromSize(resultSize).getQuant() >0){
            resSki = getSkiFromSize(resultSize);
        }
        else if (listSkiSizeFull.contains(resultSize) && MenuControl.between15Minute(resultSize)){
            return resSki;
        }
        //лыжи рамером возле resultSize, если может стоять не первой и не последней
        else if (bSRes>0 && bSRes<listSkiSize.size()){
            //ближайший размер больше
            if ((listSkiSize.get(bSRes) - resultSize) <= (resultSize - listSkiSize.get(bSRes-1)) && getSkiFromSize(listSkiSize.get(bSRes)).getQuant() >0 && (listSkiSize.get(bSRes) - resultSize)<30){
                resSki = getSkiFromSize(listSkiSize.get(bSRes));
            }
            //ближайший размер меньше
            else if ((listSkiSize.get(bSRes) - resultSize) > (resultSize - listSkiSize.get(bSRes-1)) && getSkiFromSize(listSkiSize.get(bSRes-1)).getQuant() >0 && resultSize - listSkiSize.get(bSRes-1) < 30){
                resSki = getSkiFromSize(listSkiSize.get(bSRes-1));
            }
            else if ((listSkiSize.get(bSRes) - resultSize) <= (resultSize - listSkiSize.get(bSRes-1)) && getSkiFromSize(listSkiSize.get(bSRes)).getQuant() == 0 &&  MenuControl.between15Minute(listSkiSize.get(bSRes))){
                resSki = getSkiFromSize(listSkiSize.get(bSRes));
            }
            else if ((listSkiSize.get(bSRes) - resultSize) > (resultSize - listSkiSize.get(bSRes-1)) && getSkiFromSize(listSkiSize.get(bSRes)).getQuant() == 0 && MenuControl.between15Minute(listSkiSize.get(bSRes-1))){
                resSki = getSkiFromSize(listSkiSize.get(bSRes-1));
            }
        }
        //Может быть только больший размер
        else if (bSRes==0){
            if ((listSkiSize.get(bSRes) - resultSize) <= 25 && getSkiFromSize(listSkiSize.get(bSRes)).getQuant() > 0){
                resSki = getSkiFromSize(listSkiSize.get(bSRes));
            }
        }
        //Может быть только меньший размер
        else if (bSRes==listSkiSize.size()){
            if ((resultSize - listSkiSize.get(bSRes-1)) <= 25 && getSkiFromSize(listSkiSize.get(bSRes-1)).getQuant() > 0){
                resSki = getSkiFromSize(listSkiSize.get(bSRes-1));
            }
        }

        if (resSki.getSize()!= 1){
            return resSki;
        }
        else {
            resSki.setSize(2);
            return resSki;
        }
    }

    //Расчет индекса массы тела
    public int calcBMI(float height, int weight) {
        return (int) Math.round((weight / ((double)(height*height)/10000)));
    }

    //Сортированный массив с имеющимеся размерами
    public ArrayList<Integer> sortSkiSize() {
        ArrayList<Integer> listSkiSize = new ArrayList<Integer>();
        for(Ski ski : skiList){
            if(ski.getQuant()>0){
                listSkiSize.add(ski.getSize());
            }
        }
        listSkiSize.sort(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        System.out.println(listSkiSize.toString());
        return listSkiSize;
    }

    //Получение лыж по размеру
    public Ski getSkiFromSize(int size) {
        for (Ski ski : skiList){
            if (ski.getSize() == size){
                return ski;
            }
        }
        return null;
    }

    //Проверка наличия лыж нужного размера
    public boolean haveSkiSize(int size) {
        if (sortSkiSize().contains(size)){return true;}
        return false;
    }

    public static ArrayList<Ski> getSkiList() {
        return skiList;
    }

    public static void setSkiList(ArrayList<Ski> skiList) {
        Model.skiList = skiList;
    }

    public static ArrayList<Rent> getRentList() {
        return rentList;
    }

    public static void setRentList(ArrayList<Rent> rentList) {
        Model.rentList = rentList;
    }

    public static ArrayList<Client> getClientList() {
        return clientList;
    }

    public static void setClientList(ArrayList<Client> clientList) {
        Model.clientList = clientList;
    }


}
