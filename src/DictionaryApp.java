import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class DictionaryApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel panelNorth;
    private JPanel panelCenter;
    private JLabel labelWord;
    private JTextField textFieldWord;
    private JTable tableResult;
    private JButton buttonLookupVnToEn;
    private JButton buttonLookupEnToVn;
    private JButton buttonAdd;
    private JButton buttonEdit;
    private JButton buttonDelete;
    private DefaultTableModel tableModel;

    private static final String FILE_PATH = "tudien.dat";
    private List<String> wordList = new ArrayList<>();

    public DictionaryApp() {
        super("Dictionary Application");

        loadData();

        panelNorth = new JPanel();
        panelNorth.setLayout(new BorderLayout());
        labelWord = new JLabel("Word: ");
        textFieldWord = new JTextField(30);
        panelNorth.add(labelWord, BorderLayout.WEST);
        panelNorth.add(textFieldWord, BorderLayout.CENTER);
        buttonLookupVnToEn = new JButton("Lookup VN-EN");
        buttonLookupEnToVn = new JButton("Lookup EN-VN");
        panelNorth.add(buttonLookupVnToEn, BorderLayout.EAST);
        panelNorth.add(buttonLookupEnToVn, BorderLayout.SOUTH);

        panelCenter = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"Vietnamese", "English"}, 0);
        tableResult = new JTable(tableModel);
        panelCenter.add(new JScrollPane(tableResult), BorderLayout.CENTER);
        buttonAdd = new JButton("Add");
        buttonEdit = new JButton("Edit");
        buttonDelete = new JButton("Delete");
        JPanel panelControl = new JPanel();
        panelControl.add(buttonAdd);
        panelControl.add(buttonEdit);
        panelControl.add(buttonDelete);
        panelCenter.add(panelControl, BorderLayout.SOUTH);

        getContentPane().add(panelNorth, BorderLayout.NORTH);
        getContentPane().add(panelCenter, BorderLayout.CENTER);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        buttonLookupVnToEn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dictionaryWord = getDictionaryWord(textFieldWord.getText(), true);
                if (dictionaryWord == null) {
                    JOptionPane.showMessageDialog(null, "Word not found!");
                } else {
                    JOptionPane.showMessageDialog(null, dictionaryWord);
                }
            }
        });

        buttonLookupEnToVn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dictionaryWord = getDictionaryWord(textFieldWord.getText(), false);
                if (dictionaryWord == null) {
                    JOptionPane.showMessageDialog(null, "Word not found!");
                } else {
                    JOptionPane.showMessageDialog(null, dictionaryWord);
                }
            }
        });

        buttonAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String vietnamese = JOptionPane.showInputDialog("Input Vietnamese word: ");
                if (vietnamese != null && !vietnamese.trim().isEmpty()) {
                    String english = JOptionPane.showInputDialog("Input English word: ");
                    if (english != null && !english.trim().isEmpty()) {
                        addDictionaryWord(Arrays.asList(vietnamese, english));
                    }
                }
            }
        });

        buttonEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = tableResult.getSelectedRow();
                if (index == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to edit!");
                } else {
                    String vietnamese = JOptionPane.showInputDialog(
                            "Input Vietnamese word: ", tableModel.getValueAt(index, 0));
                    if (vietnamese != null && !vietnamese.trim().isEmpty()) {
                        String english = JOptionPane.showInputDialog(
                                "Input English word: ", tableModel.getValueAt(index, 1));
                        if (english != null && !english.trim().isEmpty()) {
                            editDictionaryWord(index, Arrays.asList(vietnamese, english));
                        }
                    }
                }
            }
        });

        buttonDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = tableResult.getSelectedRow();
                if (index == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to delete!");
                } else {
                    deleteDictionaryWord(index);
                }
            }
        });

        refreshTable();
    }

    private String getDictionaryWord(String input, boolean vnToEn) {
        String result = null;
        for (String word : wordList) {
            List<String> parts = Arrays.asList(word.split(":"));
            if (vnToEn) {
                if (parts.get(0).equals(input)) {
                    result = parts.get(1) + "\n" + parts.get(2);
                    break;
                }
            } else {
                if (parts.get(1).equals(input)) {
                    result = parts.get(0);
                    break;
                }
            }
        }
        return result;
    }

    private void addDictionaryWord(List<String> word) {
        wordList.add(String.join(":", word));
        saveData();
        refreshTable();
    }

    private void editDictionaryWord(int index, List<String> word) {
        wordList.set(index, String.join(":", word));
        saveData();
        refreshTable();
    }

    private void deleteDictionaryWord(int index) {
        int option = JOptionPane.showConfirmDialog(null, "Do you want to delete this word?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            wordList.remove(index);
            saveData();
            refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (String word : wordList) {
            List<String> parts = Arrays.asList(word.split(":"));
            tableModel.addRow(parts.toArray());
        }
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String word : wordList) {
                writer.println(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DictionaryApp();
    }
}
