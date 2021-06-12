package com.company.tableModel;

import com.company.model.Rent;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReportRentTableModel extends AbstractTableModel {
    private static ArrayList<Rent> rentList;
    public ReportRentTableModel(ArrayList<Rent> rentsList){
        rentList = rentsList;
    }

    public void setRentList(ArrayList<Rent> rentsList){
        rentList = rentsList;
    }

    // Количество строк
    @Override
    public int getRowCount() {
        return rentList.size();
    }
    // Количество столбцов
    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Номер";
            case 1: return "Окончание";
            case 2: return "В прокате";
            default: return "Арендатор";
        }
    }

    // Функция определения данных ячейки
    @Override
    public Object getValueAt(int row, int column)
    {
        String pattern = "MM/dd HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = rentList.get(row).getTime();
        String todayAsString = df.format(today);

        switch (column) {
            case 0: return rentList.get(row).getId();
            case 1: return todayAsString;
            case 2: return "Размер "+rentList.get(row).getSki().getSize() + rentList.get(row).getAdd();
            case 3: return rentList.get(row).getClient().getFio();
        }
        return "Не определена";
    }

}
