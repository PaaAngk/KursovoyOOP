package com.company.tableModel;

import com.company.DBWorker;
import com.company.GUI;
import com.company.model.Model;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;

public class EquipTableModel extends AbstractTableModel {
    private final Model data;
    public EquipTableModel(Model data)
    {
        this.data = data;
    }

    // Количество строк
    @Override
    public int getRowCount() {
        return data.getSkiListSize();
    }

    // Количество столбцов
    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Название";
            case 1: return "Размер";
            default: return "Количество";
        }
    }

    @Override
    public void fireTableDataChanged() {
        try {
            DBWorker.getAllSki();
        } catch (SQLException ex) {
            GUI.errorInfoBox("Ошибка базы данных");
        }
        this.fireTableChanged(new TableModelEvent(this));
    }

    // Функция определения данных ячейки
    @Override
    public Object getValueAt(int row, int column)
    {
        switch (column) {
            case 0: return data.getSkiFromList(row).getName();
            case 1: return data.getSkiFromList(row).getSize();
            case 2: return data.getSkiFromList(row).getQuant();
        }

        return "Не определена";
    }
}
