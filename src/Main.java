import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.awt.Dimension;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class Main extends JFrame {
    public final String[] brands = new String[]{"Trek", "Santa Cruz", "YT", "Commencal"};
    private JTabbedPane mainPane = new JTabbedPane();
    private JPanel viewPanel = new JPanel();
    private JPanel addPanel = new JPanel();
    private JComboBox brandBox = new JComboBox(brands);
    private JTextField modelField = new JTextField(20);
    private JTextField priceField = new JTextField(8);
    private JButton confirmButton = new JButton("Confirm");
    private JPopupMenu clickMenu = new JPopupMenu();
    private JMenuItem editMenuItem = new JMenuItem("Edit Product");
    private JMenuItem deleteMenuItem = new JMenuItem("Delete Product");
    private DefaultTableModel viewModel = new DefaultTableModel(getTableContents(), new String[]{"ID", "Brand", "Model", "MSRP"}) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JTable viewTable = new JTable(viewModel);
    public Main() {
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    submit(brandBox.getSelectedItem().toString(), modelField.getText(), Integer.parseInt(priceField.getText()));
                    JOptionPane.showMessageDialog(null, "Product successfully added to inventory.");
                    brandBox.setSelectedIndex(0);
                    modelField.setText("");
                    priceField.setText("");
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Please make sure all fields have a value before proceeding.");
                }
                viewModel.setDataVector(getTableContents(), new String[]{"ID", "Brand", "Model", "MSRP"}); //update table locally
            }
        });
        editMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                edit(viewTable.getSelectedRow());
            }
        });
        deleteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (JOptionPane.showConfirmDialog(null, "You are about to permanently delete this product from inventory. Continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
                    try {
                        delete(Integer.parseInt(viewTable.getValueAt(viewTable.getSelectedRow(), 0).toString()));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error deleting product from database.");
                    }
                    viewModel.setDataVector(getTableContents(), new String[]{"ID", "Brand", "Model", "MSRP"}); //update table locally
                }
            }
        });
        viewTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
                if (SwingUtilities.isRightMouseButton(ev)) {
                    try {
                        int row = viewTable.rowAtPoint(ev.getPoint());
                        viewTable.setRowSelectionInterval(row, row);
                        clickMenu.show(ev.getComponent(), ev.getPoint().x, ev.getPoint().y);
                    }
                    catch (IllegalArgumentException ex) {
                        //do nothing, since pointer is right clicked not on a row we do not need a menu
                    }
                }
            }
        });
        setTitle("Database Test");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(500, 500));
        setMinimumSize(new Dimension(500, 500));
        setResizable(false);
        setLocationRelativeTo(null);
        setContentPane(mainPane);
        //right click menu
        clickMenu.add(editMenuItem);
        clickMenu.add(deleteMenuItem);
        //viewpanel
        viewTable.setPreferredSize(new Dimension(400, 400));
        viewTable.setMinimumSize(new Dimension(400,400));
        viewTable.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
        viewPanel.add(new JScrollPane(viewTable));
        mainPane.add(viewPanel, "View & Edit Inventory");
        //addpanel
        addPanel.add(brandBox);
        addPanel.add(modelField);
        addPanel.add(priceField);
        addPanel.add(confirmButton);
        mainPane.add(addPanel, "Add Products");
    }
    public Object[][] getTableContents() {
        List<Object[]> tableContents = new ArrayList<Object[]>();
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost/dbtest","root", "");
            ResultSet results = conn.createStatement().executeQuery("SELECT * FROM bikes");
            while (results.next()) {
                tableContents.add(new Object[]{results.getString(1), results.getString(2), results.getString(3), results.getString(4)});
            }
            conn.close();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        Object[][] contents = new Object[tableContents.size()][4];
        for (int i = 0; i < tableContents.size(); i ++) {
            contents[i] = tableContents.get(i);
        }
        return contents;
    }
    public void submit(String brand, String model, int price) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost/dbtest","root", "");
            conn.createStatement().execute("INSERT INTO bikes (brand, model, price) " + "VALUES ('" + brand + "', '" + model + "', " + price + ")");
            System.out.println("statement executed");
            conn.close();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void update(int id, String brand, String model, int price) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost/dbtest","root", "");
            conn.createStatement().execute("UPDATE bikes " + "SET brand = '" + brand + "', model = '" + model + "', price = " + price + " WHERE id = " + id);
            System.out.println("statement executed");
            conn.close();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void edit(int row) {
        JFrame editWindow = new JFrame("Edit Product");
        editWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editWindow.setPreferredSize(new Dimension(500, 110));
        editWindow.setMinimumSize(new Dimension(500, 110));
        editWindow.setResizable(false);
        editWindow.setLocationRelativeTo(null);
        JPanel editPanel = new JPanel();
        JComboBox brandBox = new JComboBox(brands);
        brandBox.setSelectedItem(viewModel.getValueAt(row, 1));
        JTextField modelField = new JTextField(viewModel.getValueAt(row, 2).toString(), 20);
        JTextField priceField = new JTextField(viewModel.getValueAt(row, 3).toString(), 8);
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    update(Integer.parseInt(viewModel.getValueAt(row, 0).toString()), brandBox.getSelectedItem().toString(), modelField.getText(), Integer.parseInt(priceField.getText()));
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Please make sure all fields have a value before proceeding.");
                    System.out.println(ex.getMessage());
                }
                viewModel.setDataVector(getTableContents(), new String[]{"ID", "Brand", "Model", "MSRP"}); //update table locally
                editWindow.dispose();
            }
        });
        editPanel.add(brandBox);
        editPanel.add(modelField);
        editPanel.add(priceField);
        editPanel.add(confirmButton);
        editWindow.add(editPanel);
        editWindow.setVisible(true);
    }
    public void delete(int id) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost/dbtest","root", "");
            conn.createStatement().execute("DELETE FROM bikes WHERE id = " + id);
            System.out.println("statement executed");
            conn.close();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main main = new Main();
                main.setVisible(true);
            }
        });
    }
}
