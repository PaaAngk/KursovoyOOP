package com.company.tableModel;

import com.company.DBWorker;
import com.company.MenuControl;
import com.company.GUI;
import com.company.model.Rent;
import com.company.model.Model;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RentTableModel extends AbstractTableModel {

    // Количество строк
    @Override
    public int getRowCount() {
        return Model.getRentList().size();
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

    @Override
    public void fireTableDataChanged() {
        try {
            DBWorker.getAllRent();
        } catch (SQLException | ParseException throwables) {
            GUI.errorInfoBox("Ошибка базы данных");
        }
        this.fireTableChanged(new TableModelEvent(this));
    }

    // Функция определения данных ячейки
    @Override
    public Object getValueAt(int row, int column)
    {
        String pattern = "MM/dd HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = MenuControl.getRent(row).getTime();
        String todayAsString = df.format(today);

        switch (column) {
            case 0: return MenuControl.getRent(row).getId();
            case 1: return todayAsString;
            case 2: return "Размер "+MenuControl.getRent(row).getSki().getSize() + MenuControl.getRent(row).getAdd();
            case 3: return MenuControl.getRent(row).getClient().getFio();
        }
        return "Не определена";
    }

    public static class TableInfoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            ArrayList<Integer> f = new ArrayList<>();
            for (Rent rent : MenuControl.dateRent){
                    f.add(rent.getId());
            }
            if(f.contains((int) table.getModel().getValueAt(row, 0)) ){
                c.setBackground(Color.yellow);

            }
            else{
                c.setBackground(new JLabel().getBackground());
            }

            Color fg = null;
            Color bg = null;

            if (isSelected) {
                super.setForeground(fg == null ? table.getSelectionForeground()
                        : fg);
                super.setBackground(bg == null ? table.getSelectionBackground()
                        : bg);
            }

            return c;
        }
    }
}
