package com.company;

import com.company.model.*;
import com.company.tableModel.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

public class GUI extends JFrame {
    private MenuControl control = new MenuControl(new Model());
    private RentTableModel tableModelRent;
    private ReportRentTableModel reportRentTableModel;
    private EquipTableModel tableModelEquip;
    private JTable tableRent;
    private JTable tableEquip;
    private JTable tableReport;
    private JDialog popupNew = new JDialog(this, "Подбор и выдача инвентаря", true);
    private JDialog popupAddSki = new JDialog(this, "Добавление лыж", true);
    private JDialog popupDelete = new JDialog(this, "Удаление лыж", true);
    private JDialog popupEquip = new JDialog(this, "Инветарь", true);
    private JDialog popupReport = new JDialog(this, "Отчет", true);
    private JDialog popupSelect = new JDialog(this, "Подбор лыж", true);

    //Инициализация изменяющихся переменных
    private JTextField textHeight = new JTextField(5);
    private JTextField textWeight = new JTextField(5);
    private JComboBox comboBoxDocument = new JComboBox(new String[]{"Паспорт","Водительское удостоверение","Загранпаспорт","Военный билет","Пенсионное удостоверение"});
    private JTextField textVal = new JTextField(5);
    private JTextField textNumber = new JTextField(5);
    private JTextField textName = new JTextField(5);
    private JLabel sizeLabel = new JLabel();
    private JComboBox comboBoxSkiSize;
    private static Ski ski;
    private static int height;
    private static int weight;
    private static Client client = null;
    private static int count = 0;


    public GUI() {
        super("Прокат горных лыж");
        this.setPreferredSize(new Dimension(700,400));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonsPanel = new JPanel();
        JLabel  equipmentLabel = new JLabel("Инвентарь в прокате");
        JButton newRent = new JButton("Подбор и выдача  инвентаря");
        JButton equipmentView = new JButton("Инвентарь");
        JButton equipmentReturn = new JButton("Возврат проката");
        JButton clientInfo = new JButton("Информация о арендаторе");
        JButton report = new JButton("Отчет");

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    DBWorker.closeConnection();
                } catch (SQLException ex) {
                    GUI.errorInfoBox("Ошибка базы данных");
                }
            }
        });


        //Формирование таблицы
        tableModelRent = new RentTableModel();
        tableRent = new JTable(tableModelRent);
        tableRent.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        tableRent.getColumnModel().getColumn(0).setMaxWidth(50);
        tableRent.getColumnModel().getColumn(1).setMaxWidth(100);

        // Создание кнопки добавления сотрудника
        newRent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (count ==0){
                        try {
                            new addDialog();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        count++;
                    }

                    client = null;
                    String result = "";
                    result = JOptionPane.showInputDialog(
                            popupNew,
                            "<html><h3>Введите номер документа, <br>если клиент брал прокат ранее",
                            "Поиск клиента",JOptionPane.QUESTION_MESSAGE);
                    if (result != null && !result.equals("")){
                        while (!result.matches("-?\\d+(\\d+)?") ){
                            errorInfoBox("<i>Введите корректный номер или оставьте поле пустым!</i>");
                            result = JOptionPane.showInputDialog(
                                    popupNew,
                                    "<html><h3>Введите номер документа, <br>если клиент брал прокат ранее",
                                    "Поиск клиента",JOptionPane.QUESTION_MESSAGE);

                        }
                        if (result.matches("-?\\d+(\\d+)?")){
                            client = control.clientFromDocument(result);
                            if (client == null){
                                infoBox("Клиент не найден");
                            }
                        }
                    }


                    new SwingWorker<Void,Void>(){
                        @Override
                        protected Void doInBackground() throws SQLException {

                            if (client != null){
                                textHeight.setText(client.getHeight()+"");
                                textWeight.setText(client.getWeight()+"");
                            }
                            else {
                                textHeight.setText("");
                                textWeight.setText("");
                            }
                            popupNew.repaint();
                            popupNew.revalidate();
                            popupNew.setVisible(true);
                            return null;
                        }

                        @Override
                        protected void done() {
                            popupNew.setVisible(false);
                            System.out.println("asad");
                        }
                    }.execute();


                } catch (Exception throwables) {
                    throwables.printStackTrace();
                    GUI.errorInfoBox("Ошибка базы данных");
                }
            }
        });

        // Создание кнопки инвентаря
        equipmentView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                equipmentAddDialog equipmentAddDialog=new equipmentAddDialog();
            }
        });

        // удаление проката кнопкой прижения
        equipmentReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteListener();
            }
        });

        // удаление проката кнопкой delete
        tableRent.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_DELETE)
                {
                    deleteListener();
                }
            }
        });

        // Информация об клиенте
        clientInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //получение id из выбраной строки
                    int column = 0;
                    int row = tableRent.getSelectedRow();
                    int value = (int) tableRent.getModel().getValueAt(row, column);
                    Client client = DBWorker.rentFromId(value).getClient();
                    infoBox("<html><h2>ФИО: "+client.getFio()+"<br> Документ: "+client.getDocumentName()+"<br> Номер документа: "+client.getDocumentVal()+"<br> Номер телефона: "+client.getPhoneNumber()+"</h2></html>");
                }
                catch (IndexOutOfBoundsException ex) {
                    infoBox("Выберите ячейку");
                } catch (ParseException | SQLException parseException) {
                    GUI.errorInfoBox("Ошибка базы данных");
                }
            }
        });

        // Отчет
        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new reportDialog();
            }
        });

        // Формирование интерфейса
        buttonsPanel.setLayout(new GridLayout(5,1));
        buttonsPanel.add(newRent);
        buttonsPanel.add(equipmentView);
        buttonsPanel.add(equipmentReturn);
        buttonsPanel.add(clientInfo);
        buttonsPanel.add(report);
        getContentPane().add(buttonsPanel, BorderLayout.WEST);
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(equipmentLabel);
        contents.add(new JScrollPane(tableRent));
        getContentPane().add(contents);
        this.setLocationRelativeTo(null);
        this.pack();
    }

    public void updateColorTable() {
        tableRent.setDefaultRenderer(Object.class, new RentTableModel.TableInfoRenderer());
        tableRent.revalidate();
        tableRent.updateUI();
    }

    // Кнопка добавления проката ввод данных
    private class addDialog {
        private JPanel addPanel = new JPanel();
        private JLabel labelHeight = new JLabel("Рост:");
        private JLabel labelWeight = new JLabel("Вес:");

        private JButton selectButton = new JButton("Подобрать");

        public addDialog() throws SQLException {
            popupNew.setPreferredSize(new Dimension(350,130));
            addPanel.setLayout(new GridLayout(2,2));

            addPanel.add(labelHeight);
            addPanel.add(textHeight);
            addPanel.add(labelWeight);
            addPanel.add(textWeight);

            selectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (control.verifSkiSize(textHeight.getText()) && control.verifWeight(textWeight.getText()) ){
                        System.out.println(Integer.parseInt(textHeight.getText())+" "+ Integer.parseInt(textWeight.getText()));
                        ski = control.selectSki(Integer.parseInt(textHeight.getText()), Integer.parseInt(textWeight.getText()));
                        if ((ski.getSize() != 2) && ski.getSize() != 1){
                            if (count == 1){
                                new selectDialog();
                                count++;
                            }
                            height = Integer.parseInt(textHeight.getText());
                            weight = Integer.parseInt(textWeight.getText());
                            new SwingWorker<Void,Void>(){
                                @Override
                                protected Void doInBackground()  {
                                    sizeLabel.setText("Рекомендуемый размер имеющихся лыж: " + ski.getSize());
                                    if (client != null){
                                        textName.setText(client.getFio()+"");
                                        textVal.setText(client.getDocumentVal()+"");
                                        textNumber.setText(client.getPhoneNumber()+"");
                                        comboBoxDocument.setSelectedItem(client.getDocumentName());
                                    }
                                    else {
                                        textName.setText("");
                                        textVal.setText("");
                                        textNumber.setText("");

                                    }
                                    comboBoxSkiSize = new JComboBox(Model.getStringSizeFromList());
                                    comboBoxSkiSize.revalidate();
                                    comboBoxSkiSize.repaint();
                                    popupSelect.revalidate();
                                    popupSelect.repaint();
                                    popupSelect.setVisible(true);
                                    return null;
                                }
                                @Override
                                protected void done()  {
                                    popupSelect.setVisible(false);
                                }
                            }.execute();

                            textHeight.setText("");
                            textWeight.setText("");
                        }
                        else if(ski.getSize() == 2){
                            infoBox("Подходящих лыж не найдено");
                        }
                    }
                    System.out.println(textHeight.getText()+ "  " + textWeight.getText());
                }
            });

            popupNew.add(addPanel, BorderLayout.CENTER);
            popupNew.add(selectButton, BorderLayout.SOUTH);

            popupNew.setLocationRelativeTo(null);
            popupNew.setResizable(false);
            popupNew.pack();
            popupNew.setVisible(false);
            popupNew.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
    }

    //Подбор и выдача Рекомендация выдачи
    private class selectDialog  {
        private JPanel addPanel = new JPanel();
        private JPanel clientPanel = new JPanel();
        private JLabel labelAdd = new JLabel("Добавить:");
        private JLabel sizeLabelSelect = new JLabel("Выбор нужного размера:");
        private JLabel timeLabelSelect = new JLabel("Выбор времени проката, часов:");
        //private JCheckBox bootsBox = new JCheckBox("Ботинки");
        private JCheckBox sticksBox = new JCheckBox("Палки");
        private JButton selButton = new JButton("Взять в аренду");

        private JLabel labelName = new JLabel("ФИО:");
        private JLabel labelDoc = new JLabel("Документ:");
        private JLabel labelVal = new JLabel("Номер документа:");
        private JLabel labelNumber = new JLabel("Номер телефона:");
        public selectDialog(){
            popupSelect.setPreferredSize(new Dimension(300,400));
            popupSelect.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JComboBox comboBoxHour = new JComboBox(new String[]{"1","2","3","4","5","6"});
            comboBoxSkiSize = new JComboBox(Model.getStringSizeFromList());

            clientPanel.setLayout(new GridLayout(2,2));
            clientPanel.add(labelVal);
            clientPanel.add(textVal);
            clientPanel.add(labelNumber);
            clientPanel.add(textNumber);

            addPanel.setLayout(new GridLayout(11,1));
            addPanel.add(sizeLabel);
            addPanel.add(sizeLabelSelect);
            addPanel.add(comboBoxSkiSize);
            addPanel.add(timeLabelSelect);
            addPanel.add(comboBoxHour);
            addPanel.add(labelAdd);
            addPanel.add(sticksBox);
            //addPanel.add(bootsBox);
            addPanel.add(labelDoc);
            addPanel.add(comboBoxDocument);
            addPanel.add(labelName);
            addPanel.add(textName);

            popupSelect.add(addPanel, BorderLayout.NORTH);
            popupSelect.add(clientPanel, BorderLayout.CENTER);
            popupSelect.add(selButton, BorderLayout.SOUTH);

            selButton.addActionListener(e -> {
                String add = "";
                if ( sticksBox.isSelected()){
                    add = ", Палки";
                }
                /*else if (bootsBox.isSelected()){
                    add = ", Ботинки";
                }
                else if (sticksBox.isSelected()){
                    add = ", Палки";
                }*/

                if (control.verifName(textName.getText()) && control.verifPhoneNumber(textNumber.getText()) && control.verifInt(textVal.getText())){
                    client = control.addClient( textName.getText(), comboBoxDocument.getSelectedItem().toString(), textVal.getText(), textNumber.getText(),height, weight);
                    if (comboBoxSkiSize.getSelectedItem().toString().equals("Использовать подобранный")){
                        control.addRent(client,ski,Integer.parseInt(comboBoxHour.getSelectedItem().toString()), add);
                        textNumber.setText("");
                        textVal.setText("");
                        textName.setText("");
                        tableModelRent.fireTableDataChanged();
                        popupSelect.dispose();
                        popupNew.dispose();
                    }
                    else if (control.haveSkiSize(Integer.parseInt(comboBoxSkiSize.getSelectedItem().toString()))){
                        ski = control.getSkiFromSize(Integer.parseInt(comboBoxSkiSize.getSelectedItem().toString()));
                        control.addRent(client, ski, Integer.parseInt(comboBoxHour.getSelectedItem().toString()), add);
                        tableModelRent.fireTableDataChanged();
                        textNumber.setText("");
                        textVal.setText("");
                        textName.setText("");
                        popupSelect.dispose();
                        popupNew.dispose();
                    }
                    else {
                        errorInfoBox("Нет лыж такого размера");
                    }
                }
                popupSelect.repaint();
                popupSelect.revalidate();

            });
            popupSelect.repaint();
            popupSelect.revalidate();
            popupSelect.setLocationRelativeTo(null);
            popupSelect.setResizable(false);
            popupSelect.pack();
            popupSelect.setVisible(false);
        }
    }

    // Панель инвентаря
    private class equipmentAddDialog extends JDialog {
        private JPanel buttonsPanel = new JPanel();
        private JButton newEquipment = new JButton("Добавить лыжи");
        private JButton deletEquipment = new JButton("Удалить лыжи");

        public equipmentAddDialog() {
            popupEquip.setPreferredSize(new Dimension(500,350));
            popupEquip.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            tableModelEquip = new EquipTableModel(new Model());//new Group()
            tableEquip = new JTable(tableModelEquip);

            newEquipment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        addEquipmentDialog addEquipmentDialog=new addEquipmentDialog();
                    } catch (SQLException throwables) {
                        GUI.errorInfoBox("Ошибка базы данных");
                    }
                }
            });

            deletEquipment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deletEquipmentDialog deletEquipmentDialog=new deletEquipmentDialog();
                }
            });

            // Формирование интерфейса
            buttonsPanel.setLayout(new GridLayout(4,1));
            buttonsPanel.add(newEquipment);
            buttonsPanel.add(deletEquipment);
            Box contents = new Box(BoxLayout.Y_AXIS);
            contents.add(new JScrollPane(tableEquip));

            popupEquip.add(buttonsPanel, BorderLayout.WEST);
            popupEquip.add(contents);

            popupEquip.setLocationRelativeTo(null);
            popupEquip.setResizable(false);
            popupEquip.pack();
            popupEquip.setVisible(true);
        }
    }

    //Добавление инвентаря
    private class addEquipmentDialog extends JDialog {
        private JPanel staffPanel = new JPanel();
        private JLabel labelQuant = new JLabel("Количество:");
        private JLabel labelSize = new JLabel("Размер:");

        private JLabel labelWork = new JLabel("Обязанность:");
        private JLabel labelProf = new JLabel("Профессия:");
        private JTextField textQuant = new JTextField(5);
        private JTextField textSize = new JTextField(5);
        private String size, quant;

        public addEquipmentDialog() throws SQLException {
            JButton subButton = new JButton("Добавить");
            popupAddSki.setPreferredSize(new Dimension(200,150));

            staffPanel.setLayout(new GridLayout(2,2));
            staffPanel.add(labelSize);
            staffPanel.add(textSize);
            staffPanel.add(labelQuant);
            staffPanel.add(textQuant);

            subButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    size = textSize.getText();
                    quant = textQuant.getText();

                    //Проверка размера
                    boolean sizeTrue = MenuControl.verifSkiSize(size);
                    //Проверка количества
                    boolean quantTrue = MenuControl.verifSkiQuant(quant);

                    /*try{
                        profSelect = groupProf.getSelection().getActionCommand();
                        workSelect = groupWork.getSelection().getActionCommand();
                    }
                    catch (NullPointerException ex){
                        infoBox("Выберите профессия и/или обязанность");
                    }*/

                    if (sizeTrue && quantTrue) {
                        String[] ski = new String[]{size,quant};

                        // Добавление нового сотрудника
                        try {
                            control.addSki(ski);
                        } catch (SQLException throwables) {
                            GUI.errorInfoBox("Ошибка базы данных");
                        }

                        tableModelEquip.fireTableDataChanged();
                        textSize.setText("");
                        textQuant.setText("");
                        size = "";
                        quant = "";
                    }
                }
            });

            //popupNew.add(new JScrollPane(addPanel), BorderLayout.NORTH);
            popupAddSki.add(staffPanel, BorderLayout.CENTER);
            popupAddSki.add(subButton, BorderLayout.SOUTH);

            popupAddSki.setLocationRelativeTo(null);
            popupAddSki.setResizable(false);
            popupAddSki.pack();
            popupAddSki.setVisible(true);
            popupAddSki.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        }
    }

    // Удаление лыж
    private class deletEquipmentDialog {
        private JPanel deletePanel = new JPanel();
        private JLabel labelDelete = new JLabel("Удалить по размеру:");
        private JTextField textSize = new JTextField(5);
        private JButton deleteButton = new JButton("Удалить");

        public deletEquipmentDialog() {
            popupDelete.setPreferredSize(new Dimension(200,150));
            deletePanel.setLayout(new GridLayout(3,1));
            deletePanel.add(labelDelete);
            deletePanel.add(textSize);
            deletePanel.add(deleteButton);
            popupDelete.add(deletePanel, BorderLayout.CENTER);

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //Удаление лыж по размеру
                    if(MenuControl.verifSkiSize(textSize.getText())){
                        try {
                            control.deleteSki(Integer.parseInt(textSize.getText()));
                        } catch (SQLException throwables) {
                            GUI.errorInfoBox("Ошибка базы данных");
                        }
                    }

                    tableModelEquip.fireTableDataChanged();
                    textSize.setText("");
                }
            });

            popupDelete.setLocationRelativeTo(null);
            popupDelete.setResizable(false);
            popupDelete.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            popupDelete.pack();
            popupDelete.setVisible(true);
        }
    }

    // Панель отчета
    private class reportDialog {
        private JPanel buttonsPanel = new JPanel();
        private JButton reportDay = new JButton("Отчет за день");
        private JButton reportMonths = new JButton("Отчет за месяц");
        private JButton report = new JButton("<html>Отчет");
        private JButton clientInfo = new JButton("Информация об арендаторе");
        private JLabel dayLabel = new JLabel("<html>Ввод количества дней <br>для составления отчета");
        private JTextField textDay = new JTextField(5);
        private JLabel priceLabel = new JLabel("Стоимость часа проката");
        private JTextField textPrice = new JTextField(5);

        public reportDialog() {
            popupReport.setPreferredSize(new Dimension(600,350));
            popupReport.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            reportRentTableModel = new ReportRentTableModel(new ArrayList<Rent>());
            tableReport = new JTable(reportRentTableModel);
            tableReport.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
            tableReport.getColumnModel().getColumn(0).setMaxWidth(50);
            tableReport.getColumnModel().getColumn(1).setMaxWidth(100);

            reportDay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (control.verifPrice(textPrice.getText())){
                            int sum = 0;
                            reportRentTableModel.setRentList(DBWorker.getAllRentHave(1));
                            reportRentTableModel.fireTableDataChanged();
                            for (Rent rent : DBWorker.getAllRentHave(1)){
                                sum += rent.getHourRent()*Integer.parseInt(textPrice.getText());

                            }
                            infoBox("Прибыль : "+sum);
                        }
                    } catch (SQLException | ParseException throwables) {
                        errorInfoBox("Ошибка базы данных!");
                    }
                }
            });

            reportMonths.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (control.verifPrice(textPrice.getText())){
                            int sum = 0;
                            reportRentTableModel.setRentList(DBWorker.getAllRentHave(30));
                            reportRentTableModel.fireTableDataChanged();
                            for (Rent rent : DBWorker.getAllRentHave(30)){
                                sum += rent.getHourRent()*Integer.parseInt(textPrice.getText());
                            }
                            infoBox("Прибыль : "+sum);
                        }

                    } catch (SQLException | ParseException throwables) {
                        errorInfoBox("Ошибка базы данных!");
                    }
                }
            });

            report.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (control.verifDay(textDay.getText()) && control.verifPrice(textPrice.getText())){
                            int sum = 0;
                            reportRentTableModel.setRentList(DBWorker.getAllRentHave(Integer.parseInt(textDay.getText())));
                            reportRentTableModel.fireTableDataChanged();
                            for (Rent rent : DBWorker.getAllRentHave(Integer.parseInt(textDay.getText()))){
                                sum += rent.getHourRent()*Integer.parseInt(textPrice.getText());
                            }
                            infoBox("Прибыль : "+sum);
                        }

                    } catch (SQLException | ParseException throwables) {
                        errorInfoBox("Ошибка базы данных!");
                    }
                }
            });

            clientInfo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        //получение id из выбраной строки
                        int column = 0;
                        int row = tableReport.getSelectedRow();
                        int value = (int) tableReport.getModel().getValueAt(row, column);
                        Client client = DBWorker.rentFromId(value).getClient();
                        infoBox("<html><h2>ФИО: "+client.getFio()+"<br> Документ: "+client.getDocumentName()+"<br> Номер документа: "+client.getDocumentVal()+"<br> Номер телефона: "+client.getPhoneNumber()+"</h2></html>");
                    }
                    catch (IndexOutOfBoundsException ex) {
                        infoBox("Выберите ячейку");
                    } catch (ParseException | SQLException parseException) {
                        errorInfoBox("Ошибка базы данных");
                    }
                }
            });

            // Формирование интерфейса
            buttonsPanel.setLayout(new GridLayout(8,1));
            buttonsPanel.add(priceLabel);
            buttonsPanel.add(textPrice);
            buttonsPanel.add(reportDay);
            buttonsPanel.add(reportMonths);
            buttonsPanel.add(dayLabel);
            buttonsPanel.add(textDay);
            buttonsPanel.add(report);
            buttonsPanel.add(clientInfo);

            Box contents = new Box(BoxLayout.Y_AXIS);
            contents.add(new JScrollPane(tableReport));
            popupReport.add(buttonsPanel, BorderLayout.WEST);
            popupReport.add(contents);

            popupReport.setLocationRelativeTo(null);
            popupReport.setResizable(false);
            popupReport.pack();
            popupReport.setVisible(true);
        }
    }

    // Вывод сообщения ошибки
    public static void errorInfoBox(String infoMessage) {
        JOptionPane.showMessageDialog(null, "<html><h2>Ошибка!</h2>" + infoMessage,"Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    //вывод ифнормационного сообщения
    public static void infoBox(String infoMessage) {
        JOptionPane.showMessageDialog(null,  infoMessage);//"<html><h2>Внимание!</h2>" +
    }

    //Окно подтверждения удаления
    private void deleteListener(){
        try {
            //получение id из выбраной строки
            int column = 0;
            int row = tableRent.getSelectedRow();
            int value = (int) tableRent.getModel().getValueAt(row, column);
            Rent rent = DBWorker.rentFromId((int) tableRent.getModel().getValueAt(row, 0));

            //Подтверждение удаления или отклон
            int result = JOptionPane.showConfirmDialog(
                    getContentPane(),
                    "<html><h3>Вернуть лыжи размером: "+ rent.getSki().getSize() +"<br> Арендатор: "+ rent.getClient().getFio() +
                            "<br> Документ : " + rent.getClient().getDocumentName() + "<br> Номер: "+rent.getClient().getDocumentVal(),
                    "Подтверждение возврата",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION){
                control.deleteRent(value);
                tableModelRent.fireTableDataChanged();
            }

        }
        catch (IndexOutOfBoundsException ex) {
            infoBox("Выберите ячейку");
        } catch (ParseException | SQLException e) {
            errorInfoBox("Ошибка базы данных!");
        }
    }

    static class timer extends TimerTask {
        private GUI gui;
        public timer(GUI gui) {
            this.gui = gui;
        }
        @Override
        public void run() {
            Date dateNow = new Date();
            int count = 0;
            for (Rent rent : Model.getRentList()){
                if (rent.getTime().before(dateNow)){//Если дата проката меньше даты сейчас
                    if (!MenuControl.dateRent.contains(rent)){//Если нет такого rent закончившегося проката
                        MenuControl.dateRent.add(rent);
                        gui.updateColorTable();
                        gui.infoBox("Закончилась аренда лыж размером: "+ rent.getSki().getSize() +" Арендатор: "+ rent.getClient().getFio());
                    }
                }
                count++;
            }
        }
    }

    //Сообщение с ответом, узнать стоит ла ждать возврат аренды
    public static boolean between15MinuteInfoBox(int size){
        //Подтверждение удаления или отклон
        int result = JOptionPane.showConfirmDialog(
                null,
                "В течении 15 минут могут вернуть лыжи размером:"+ size +". Подождать?",
                "В ближайшее время могут вернуть.",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION){
            return true;
        }
        else {return false;}
    }

}
