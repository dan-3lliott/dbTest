import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.awt.Dimension;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class Main extends JFrame {
    private JTabbedPane mainPane = new JTabbedPane();
    private JPanel viewPanel = new JPanel();
    private JPanel addPanel = new JPanel();
    private JTextField idField = new JTextField(2);
    private JComboBox brandBox = new JComboBox(new String[]{"Trek", "Santa Cruz", "YT", "Commencal"});
    private JTextField modelField = new JTextField(20);
    private JTextField priceField = new JTextField(8);
    private JButton confirmButton = new JButton("Confirm");
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
                    submit(Integer.parseInt(idField.getText()), brandBox.getSelectedItem().toString(), modelField.getText(), Integer.parseInt(priceField.getText()));
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Please make sure all fields have a value before proceeding.");
                }
            }
        });
        viewTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ev) {
                if (SwingUtilities.isRightMouseButton(ev)) {
                    int row = viewTable.rowAtPoint(ev.getPoint());
                    viewTable.setRowSelectionInterval(row, row);
                    edit(row);
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
        //viewpanel
        viewTable.setPreferredSize(new Dimension(400, 400));
        viewTable.setMinimumSize(new Dimension(400,400));
        viewTable.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
        viewPanel.add(new JScrollPane(viewTable));
        mainPane.add(viewPanel, "View Inventory");
        //addpanel
        addPanel.add(idField);
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
    public void submit(int id, String brand, String model, int price) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost/dbtest","root", "");
            conn.createStatement().execute("INSERT INTO bikes " + "VALUES (" + id + ", '" + brand + "', '" + model + "', " + price + ")");
            System.out.println("statement executed");
            conn.close();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void edit(int row) {
        System.out.println(row);
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
