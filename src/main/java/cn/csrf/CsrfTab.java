package cn.csrf;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CsrfTab extends JComponent {

    public Map<String, Object> config = new HashMap<String, Object>();

    private final Logging logging;

    private JPanel panel_main;
    private JPanel panel_config;
    private JPanel panel_method;
    private JLabel label_method;
    private JCheckBox checkBox_get;
    private JCheckBox checkBox_post;
    private JCheckBox checkBox_put;
    private JCheckBox checkBox_delete;
    private JCheckBox checkBox_update;
    private JCheckBox checkBox_head;
    private JCheckBox checkBox_options;
    private JPanel panel_domain;
    private JRadioButton radioButton_black;
    private JRadioButton radioButton_white;
    private JLabel label_domain;
    private JTextField textField_domain;
    private JScrollPane scrollPane_log;
    private JTable table_log;
    private JSplitPane splitPane_httpDetail;
    private HttpRequestEditor httpRequestEditor;
    private HttpResponseEditor httpResponseEditor;
    private TableColumn column;

    private JButton button_save;

    public CsrfTab(MontoyaApi montoyaApi) {

        this.logging = montoyaApi.logging();
        loadConfig();
//        Panel panel = new Panel();
//        panel.setLayout(new GridLayout(2, 8,5,5));
        setLayout(new BorderLayout());
//        panel_root = new JPanel();
//        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        panel_main = new JPanel();
        panel_config = new JPanel();
        panel_method = new JPanel();
        label_method = new JLabel();
        checkBox_get = new JCheckBox();
        checkBox_post = new JCheckBox();
        checkBox_put = new JCheckBox();
        checkBox_delete = new JCheckBox();
        checkBox_update = new JCheckBox();
        checkBox_head = new JCheckBox();
        checkBox_options = new JCheckBox();
        panel_domain = new JPanel();
        radioButton_black = new JRadioButton();
        radioButton_white = new JRadioButton();
        label_domain = new JLabel();
        textField_domain = new JTextField();
        splitPane_httpDetail = new JSplitPane();
        button_save = new JButton("保存配置");
        scrollPane_log = new JScrollPane() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(panel_main.getWidth(), (int) (panel_main.getHeight() * 0.3));
            }
        };
        ;
        table_log = new JTable();
        splitPane_httpDetail = new JSplitPane();
        httpRequestEditor = montoyaApi.userInterface().createHttpRequestEditor(EditorOptions.READ_ONLY);
        httpResponseEditor = montoyaApi.userInterface().createHttpResponseEditor(EditorOptions.READ_ONLY);


        //======== panel_config ========
        {
            panel_config.setLayout(new BorderLayout());

            //======== panel_method ========
            {
                panel_method.setLayout(new FlowLayout());

                //---- label_method ----
                label_method.setText("处理以下请求类型:");
                panel_method.add(label_method);

                //---- checkBox_get ----
                checkBox_get.setText("GET");
                checkBox_get.setSelected((Boolean) config.get("GET"));
                panel_method.add(checkBox_get);

                //---- checkBox_post ----
                checkBox_post.setText("POST");
                checkBox_post.setSelected((Boolean) config.get("POST"));
                panel_method.add(checkBox_post);

                //---- checkBox_put ----
                checkBox_put.setText("PUT");
                checkBox_put.setSelected((Boolean) config.get("PUT"));
                panel_method.add(checkBox_put);

                //---- checkBox_delete ----
                checkBox_delete.setText("DELETE");
                checkBox_delete.setSelected((Boolean) config.get("DELETE"));
                panel_method.add(checkBox_delete);

                //---- checkBox_update ----
                checkBox_update.setText("UPDATE");
                checkBox_update.setSelected((Boolean) config.get("UPDATE"));
                panel_method.add(checkBox_update);

                //---- checkBox_head ----
                checkBox_head.setText("HEAD");
                checkBox_head.setSelected((Boolean) config.get("HEAD"));
                panel_method.add(checkBox_head);

                //---- checkBox_options ----
                checkBox_options.setText("OPTIONS");
                checkBox_options.setSelected((Boolean) config.get("OPTIONS"));
                panel_method.add(checkBox_options);
            }
            panel_config.add(panel_method, BorderLayout.NORTH);

            //======== panel_domain ========
            {
                panel_domain.setLayout(new FlowLayout());

                //---- radioButton_black ----
                radioButton_black.setText("黑名单");
                radioButton_black.setSelected((Boolean) config.get("BLACK"));
                panel_domain.add(radioButton_black);

                //---- radioButton_white ----
                radioButton_white.setText("白名单");
                radioButton_white.setSelected((Boolean) config.get("WHITE"));
                panel_domain.add(radioButton_white);

                //---- label_domain ----
                label_domain.setText("域名(半角逗号分隔):");
                panel_domain.add(label_domain);

                //---- textField2 ----
                textField_domain.setColumns(60);
                textField_domain.setText((String) config.get("DOMAIN"));
                panel_domain.add(textField_domain);

                //---- button_save ----
                button_save.addActionListener(e -> {
                    writeConfig(checkBox_get.isSelected(), checkBox_post.isSelected(), checkBox_put.isSelected(), checkBox_delete.isSelected(), checkBox_update.isSelected(), checkBox_head.isSelected(), checkBox_options.isSelected(), radioButton_black.isSelected(), radioButton_white.isSelected(), textField_domain.getText());
                    JOptionPane.showMessageDialog(null, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                });
                panel_domain.add(button_save);
            }
            panel_config.add(panel_domain, BorderLayout.SOUTH);
        }
        panel_main.add(panel_config, BorderLayout.NORTH);
        //======== scrollPane_log ========
        {
            //======== table_log ========
            {
                String[] headers = {"#", "方法", "url", "疑似", "Cookie", "无Cookie", "随机Referer"};
                int[] columnWidth = {50, 100, 300, 50, 100, 100, 100};
                Object[][] cellData = null;
                DefaultTableModel tableModel = new DefaultTableModel(cellData, headers);

                table_log.setModel(tableModel);
                table_log.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);// 以下设置表格列宽
                for (int i = 0; i < headers.length; i++) {
                    column = table_log.getColumnModel().getColumn(i);
                    column.setPreferredWidth(columnWidth[i]);
                }
//                table_log.getColumnModel().getColumn(0).setPreferredWidth(50);
//                table_log.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
            scrollPane_log.setViewportView(table_log);
        }
        panel_main.add(scrollPane_log, BorderLayout.WEST);

        JPanel jPanel = new JPanel();

        panel_main.add(jPanel, BorderLayout.CENTER);

        //======== splitPane_httpDetail ========
        {
            splitPane_httpDetail.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            //---- button2 ----
            splitPane_httpDetail.setLeftComponent(httpRequestEditor.uiComponent());

            //---- button3 ----
            splitPane_httpDetail.setRightComponent(httpResponseEditor.uiComponent());
        }
        panel_main.add(splitPane_httpDetail, BorderLayout.SOUTH);
        //---- buttonGroup_blackOrWhite ----
        var buttonGroup_blackOrWhite = new ButtonGroup();
        buttonGroup_blackOrWhite.add(radioButton_black);
        buttonGroup_blackOrWhite.add(radioButton_white);


        add(panel_main);
        loadConfig();

    }

    public void writeConfig(boolean get, boolean post, boolean put, boolean delete, boolean update, boolean head, boolean options, boolean black, boolean white, String domain) {
        File file = new File("csrf4burp.conf");
        StringBuilder sb = new StringBuilder();
        sb.append(get);
        sb.append(",");
        sb.append(post);
        sb.append(",");
        sb.append(put);
        sb.append(",");
        sb.append(delete);
        sb.append(",");
        sb.append(update);
        sb.append(",");
        sb.append(head);
        sb.append(",");
        sb.append(options);
        sb.append("|");
        sb.append(black);
        sb.append(",");
        sb.append(white);
        sb.append("|");
        sb.append(domain);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(sb.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            logging.logToError(e);
        }

    }

    public void loadConfig() {
        try {
            File file = new File("csrf4burp.conf");
            if (file.exists()) {
                //读取配置文件
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String tmpstr = bufferedReader.readLine();
                fileReader.close();
                if (tmpstr == null || !tmpstr.contains("|")) {
                    //配置文件格式错误
                    writeConfig(false, true, false, false, false, false, false, true, false, "空");
                    tmpstr = "false,true,false,false,false,false,false|true,false|空";
                }
                //解析配置文件
                String[] configStr = tmpstr.split("\\|");
                String[] methods = configStr[0].split(",");
                String[] blackOrWhite = configStr[1].split(",");
                logging.logToOutput(Arrays.toString(configStr));
                String domain = configStr[2];
                config.put("GET", Boolean.parseBoolean(methods[0]));
                config.put("POST", Boolean.parseBoolean(methods[1]));
                config.put("PUT", Boolean.parseBoolean(methods[2]));
                config.put("DELETE", Boolean.parseBoolean(methods[3]));
                config.put("UPDATE", Boolean.parseBoolean(methods[4]));
                config.put("HEAD", Boolean.parseBoolean(methods[5]));
                config.put("OPTIONS", Boolean.parseBoolean(methods[6]));
                config.put("BLACK", Boolean.parseBoolean(blackOrWhite[0]));
                config.put("WHITE", Boolean.parseBoolean(blackOrWhite[1]));
                config.put("DOMAIN", domain);
            } else {
                config.put("GET", false);
                config.put("POST", true);
                config.put("PUT", false);
                config.put("DELETE", false);
                config.put("UPDATE", false);
                config.put("HEAD", false);
                config.put("OPTIONS", false);
                config.put("BLACK", true);
                config.put("WHITE", false);
                config.put("DOMAIN", "空");
                if (file.createNewFile()) {
                    writeConfig(false, true, false, false, false, false, false, true, false, "空");
                }
            }

        } catch (IOException e) {
            logging.logToError(e);
        }
    }

}
