import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DynamicTextFieldExample extends JFrame {
    private JPanel mainPanel;
    private JButton addButton;
    private double totalProbability; // 合計確率
    private int numberOfFields; // 追加する項目の数

    // 保存するテキストのリスト
    private List<String> savedTexts;

    public DynamicTextFieldExample() {
        super("Dynamic Text Field Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        addButton = new JButton("項目を追加");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numberOfFields++; // ボタンが押されるたびに1ずつ増やす
                saveTextFields();
                removeAllFields();
                addFields(numberOfFields);
            }
        });

        mainPanel.add(addButton);
        add(mainPanel);

        numberOfFields = 1; // 初期値
        totalProbability = 0.0; // 初期化
        savedTexts = new ArrayList<>(); // 保存するテキストのリストを初期化
        setVisible(true);
    }

    private void saveTextFields() {
        savedTexts.clear();
        // 保存するテキストを取得
        Component[] components = mainPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel entryPanel = (JPanel) component;
                Component[] entryComponents = entryPanel.getComponents();
                for (Component entryComponent : entryComponents) {
                    if (entryComponent instanceof JTextField) {
                        JTextField textField = (JTextField) entryComponent;
                        savedTexts.add(textField.getText());
                    }
                }
            }
        }
    }

    private void removeAllFields() {
        // ボタン以外の項目だけを削除
        Component[] components = mainPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                mainPanel.remove(component);
            }
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void addFields(int numberOfFields) {
        for (int i = 0; i < numberOfFields; i++) {
            JPanel entryPanel = new JPanel();
            JTextField textField = new JTextField(15);

            // 保存しておいたテキストがあれば設定
            if (i < savedTexts.size()) {
                textField.setText(savedTexts.get(i));
            }

            JTextField probabilityField = new JTextField(5);
            entryPanel.add(new JLabel("項目: "));
            entryPanel.add(textField);
            entryPanel.add(new JLabel("確率: "));
            entryPanel.add(probabilityField);
            mainPanel.add(entryPanel);

            // 初期値を設定（等確率）
            double initialProbability = 1.0 / numberOfFields;
            DecimalFormat df = new DecimalFormat("#.##");
            probabilityField.setText(df.format(initialProbability));

            // 確率の変更を検知して合計を更新
            probabilityField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateTotalProbability();
                }
            });
        }

        mainPanel.revalidate(); // 更新
    }

    private void updateTotalProbability() {
        totalProbability = 0.0;

        // パネル内のすべての項目の確率を合計
        Component[] components = mainPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel entryPanel = (JPanel) component;
                Component[] entryComponents = entryPanel.getComponents();
                for (Component entryComponent : entryComponents) {
                    if (entryComponent instanceof JTextField) {
                        JTextField probabilityField = (JTextField) entryComponent;
                        try {
                            double probability = Double.parseDouble(probabilityField.getText());
                            totalProbability += probability;
                        } catch (NumberFormatException ignored) {
                            // 数値以外の入力は無視
                        }
                    }
                }
            }
        }

        // 合計が1.0を超えないように調整
        if (totalProbability > 1.0) {
            JOptionPane.showMessageDialog(this, "合計確率は1.0以下である必要があります。", "エラー", JOptionPane.ERROR_MESSAGE);
            totalProbability = 0.0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DynamicTextFieldExample();
            }
        });
    }
}
