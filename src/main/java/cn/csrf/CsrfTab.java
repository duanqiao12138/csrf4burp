package cn.csrf;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class CsrfTab extends JComponent {

    public HashMap<String, Object> config = new HashMap<String, Object>();
    //    private HashMap<Integer,Integer> MessageId2Row = new HashMap<>();
    private HashMap<Integer, CsrfMessage> hashMap = new HashMap<>();

    private final Logging logging;
    private JPanel panel_main;
    private JPanel panel_config;
    private JPanel panel_method;
    private JLabel label_method;
    private JLabel label_Referer;
    private JTextField textField_Referer;
    private JCheckBox checkBox_get;
    private JCheckBox checkBox_post;
    private JCheckBox checkBox_put;
    private JCheckBox checkBox_delete;
    private JCheckBox checkBox_update;
    private JCheckBox checkBox_head;
    private JCheckBox checkBox_options;
    private JPanel panel_domain;
    private JPanel panel_suffix;
    private JRadioButton radioButton_domain_black;
    private JRadioButton radioButton_domain_white;
    private JRadioButton radioButton_suffix_black;
    private JRadioButton radioButton_suffix_white;
    private JLabel label_domain;
    private JTextField textField_domain;
    private JLabel label_suffix;
    private JTextField textField_suffix;
    private JScrollPane scrollPane_log;
    private JTable table_log;
    private JPanel panel_httpDetail;
    private HttpRequestEditor httpRequestEditor;
    private HttpResponseEditor httpResponseEditorBase;
    private HttpResponseEditor httpResponseEditorNoCookie;
    private HttpResponseEditor httpResponseEditorRandomRef;
    //    private HttpResponseEditor httpResponseEditorRandomRef;
    private TableColumn column;
    private JButton button_save;

    private MontoyaApi Api;

    public CsrfTab(MontoyaApi montoyaApi) {
        this.Api = montoyaApi;

        this.logging = Api.logging();
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
        label_Referer = new JLabel();
        checkBox_get = new JCheckBox();
        checkBox_post = new JCheckBox();
        checkBox_put = new JCheckBox();
        checkBox_delete = new JCheckBox();
        checkBox_update = new JCheckBox();
        checkBox_head = new JCheckBox();
        checkBox_options = new JCheckBox();
        panel_domain = new JPanel();
        panel_suffix = new JPanel();
        radioButton_domain_black = new JRadioButton();
        radioButton_domain_white = new JRadioButton();
        radioButton_suffix_black = new JRadioButton();
        radioButton_suffix_white = new JRadioButton();
        label_domain = new JLabel();
        textField_domain = new JTextField();
        label_suffix = new JLabel();
        textField_suffix = new JTextField();
        panel_httpDetail = new JPanel();
        textField_Referer = new JTextField();
        button_save = new JButton("保存配置");
        httpRequestEditor = montoyaApi.userInterface().createHttpRequestEditor(EditorOptions.READ_ONLY);
        httpResponseEditorBase = montoyaApi.userInterface().createHttpResponseEditor(EditorOptions.READ_ONLY);
        httpResponseEditorNoCookie = montoyaApi.userInterface().createHttpResponseEditor(EditorOptions.READ_ONLY);
        httpResponseEditorRandomRef = montoyaApi.userInterface().createHttpResponseEditor(EditorOptions.READ_ONLY);
        Component httpRequestEditorComponent = httpRequestEditor.uiComponent();
        Component httpResponseEditorBaseComponent = httpResponseEditorBase.uiComponent();
        Component httpResponseEditorNoCookieComponent = httpResponseEditorNoCookie.uiComponent();
        Component httpResponseEditorRandomRefComponent = httpResponseEditorRandomRef.uiComponent();

        scrollPane_log = new JScrollPane() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(panel_main.getWidth(), (int) (panel_main.getHeight() * 0.3));
            }
        };

        table_log = new JTable() {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
//                montoyaApi.proxy().history();
//                logging.logToOutput((String) table_log.getValueAt(rowIndex, 0));
//                logging.logToOutput(montoyaApi.proxy().history().get((Integer) table_log.getValueAt(rowIndex, 0)).toString());
//                httpRequestEditor.setRequest(montoyaApi.proxy().history().get((Integer) table_log.getValueAt(rowIndex, 0)).request()); //下标和messageId对应不上，不知道为什么
                CsrfMessage csrfMessage = hashMap.get(Integer.parseInt((String) table_log.getValueAt(rowIndex, 0)));
                httpRequestEditor.setRequest(csrfMessage.baseRequest);
                httpResponseEditorBase.setResponse(csrfMessage.baseResponse);
                httpResponseEditorNoCookie.setResponse(csrfMessage.noCookieResponse);
                httpResponseEditorRandomRef.setResponse(csrfMessage.randomRefererResponse);
            }
        };

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

                label_Referer.setText("Referer:");
                panel_method.add(label_Referer);

                textField_Referer.setColumns(25);
                textField_Referer.setText((String) config.get("REFERER"));
                panel_method.add(textField_Referer);
            }
            panel_config.add(panel_method, BorderLayout.NORTH);

            //======== panel_domain ========
            {
                panel_domain.setLayout(new FlowLayout());

                //---- radioButton_domain_black ----
                radioButton_domain_black.setText("黑名单");
                radioButton_domain_black.setSelected((Boolean) config.get("DOMAIN_BLACK"));
                panel_domain.add(radioButton_domain_black);

                //---- radioButton_domain_white ----
                radioButton_domain_white.setText("白名单");
                radioButton_domain_white.setSelected((Boolean) config.get("DOMAIN_WHITE"));
                panel_domain.add(radioButton_domain_white);

                //---- label_domain ----
                label_domain.setText("域名(半角逗号分隔):");
                panel_domain.add(label_domain);

                //---- textField2 ----
                textField_domain.setColumns(60);
                textField_domain.setText((String) config.get("DOMAIN"));
                panel_domain.add(textField_domain);
            }
            panel_config.add(panel_domain, BorderLayout.CENTER);
            //======== panel_suffix ========
            {
                panel_suffix.setLayout(new FlowLayout());

                //---- radioButton_suffix_black ----
                radioButton_suffix_black.setText("黑名单");
                radioButton_suffix_black.setSelected((Boolean) config.get("SUFFIX_BLACK"));
                panel_suffix.add(radioButton_suffix_black);

                //---- radioButton_suffix_white ----
                radioButton_suffix_white.setText("白名单");
                radioButton_suffix_white.setSelected((Boolean) config.get("SUFFIX_WHITE"));
                panel_suffix.add(radioButton_suffix_white);

                //---- label_suffix ----
                label_suffix.setText("后缀(半角逗号分隔):");
                panel_suffix.add(label_suffix);

                //---- textField_suffix ----
                textField_suffix.setColumns(60);
                textField_suffix.setText((String) config.get("SUFFIX"));
                panel_suffix.add(textField_suffix);
                //---- button_save ----
                button_save.addActionListener(e -> {
                    writeConfig(checkBox_get.isSelected(), checkBox_post.isSelected(), checkBox_put.isSelected(), checkBox_delete.isSelected(), checkBox_update.isSelected(), checkBox_head.isSelected(), checkBox_options.isSelected(), radioButton_domain_black.isSelected(), radioButton_domain_white.isSelected(), textField_domain.getText(), radioButton_suffix_black.isSelected(), radioButton_suffix_white.isSelected(), textField_suffix.getText(), textField_Referer.getText());
                    JOptionPane.showMessageDialog(null, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                    loadConfig();
                });
                panel_suffix.add(button_save);
            }
            panel_config.add(panel_suffix, BorderLayout.SOUTH);
        }
        panel_main.add(panel_config, BorderLayout.NORTH);
        //======== scrollPane_log ========
        {
            //======== table_log ========
            {
                String[] headers = {"#", "URL", "方法", "疑似", "原始长度", "无Cookie", "随机Referer"};
                int[] columnWidth = {50, 300, 100, 50, 80, 80, 80};
                Object[][] cellData = null;
                DefaultTableModel tableModel = new DefaultTableModel(cellData, headers) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }

                };

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
            panel_httpDetail.setLayout(new FlowLayout());

//            panel_httpDetail.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            //---- button2 ----
            httpRequestEditorComponent.setPreferredSize(new Dimension(panel_httpDetail.getWidth() / 4, panel_httpDetail.getHeight()));
            panel_httpDetail.add(httpRequestEditorComponent);

            //---- button3 ----
            httpResponseEditorBaseComponent.setPreferredSize(new Dimension(panel_httpDetail.getWidth() / 4, panel_httpDetail.getHeight()));
            panel_httpDetail.add(httpResponseEditorBaseComponent);

            //---- button3 ----
            httpResponseEditorNoCookieComponent.setPreferredSize(new Dimension(panel_httpDetail.getWidth() / 4, panel_httpDetail.getHeight()));
            panel_httpDetail.add(httpResponseEditorNoCookieComponent);

            //---- button4 ----
            httpResponseEditorRandomRefComponent.setPreferredSize(new Dimension(panel_httpDetail.getWidth() / 4, panel_httpDetail.getHeight()));
            panel_httpDetail.add(httpResponseEditorRandomRefComponent);
        }
        panel_main.add(panel_httpDetail, BorderLayout.SOUTH);
        //---- buttonGroup_domain_blackOrWhite ----
        var buttonGroup_domain_blackOrWhite = new ButtonGroup();
        buttonGroup_domain_blackOrWhite.add(radioButton_domain_black);
        buttonGroup_domain_blackOrWhite.add(radioButton_domain_white);

        var buttonGroup_suffix_BlackOrWhite = new ButtonGroup();
        buttonGroup_suffix_BlackOrWhite.add(radioButton_suffix_black);
        buttonGroup_suffix_BlackOrWhite.add(radioButton_suffix_white);


        add(panel_main);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
//                montoyaApi.logging().logToOutput("Windows resize" + e.getComponent().getWidth() + " " + e.getComponent().getHeight());
                httpRequestEditorComponent.setPreferredSize(new Dimension(e.getComponent().getWidth() / 4, (int) (e.getComponent().getHeight() * 0.6)));
                httpResponseEditorBaseComponent.setPreferredSize(new Dimension(e.getComponent().getWidth() / 4, (int) (e.getComponent().getHeight() * 0.6)));
                httpResponseEditorNoCookieComponent.setPreferredSize(new Dimension(e.getComponent().getWidth() / 4, (int) (e.getComponent().getHeight() * 0.6)));
                httpResponseEditorRandomRefComponent.setPreferredSize(new Dimension(e.getComponent().getWidth() / 4, (int) (e.getComponent().getHeight() * 0.6)));
            }
        });


    }

    public void writeConfig(boolean get, boolean post, boolean put, boolean delete, boolean update, boolean head, boolean options, boolean domain_black, boolean domain_white, String domain, boolean suffix_black, boolean suffix_white, String suffix, String referer) {
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
        sb.append(domain_black);
        sb.append(",");
        sb.append(domain_white);
        sb.append("|");
        sb.append(domain);
        sb.append("|");
        sb.append(suffix_black);
        sb.append(",");
        sb.append(suffix_white);
        sb.append("|");
        sb.append(suffix);
        sb.append("|");
        sb.append(referer);
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
                    writeConfig(false, true, false, false, false, false, false, true, false, "空", true, false, ".js,.css,.png,.gif,.jpg,.svg,.ico,.woff,.woff2,.ttf,.mp3,.mp4,.ico,.txt", "http://www.c.test/");
                    tmpstr = "false,true,false,false,false,false,false|true,false|空|true,false|.js,.css,.png,.gif,.jpg,.svg,.ico,.woff,.woff2,.ttf,.mp3,.mp4,.ico,.txt|http://www.c.test/";
                }
                //解析配置文件
                String[] configStr = tmpstr.split("\\|");
                String[] methods = configStr[0].split(",");
                String[] domainBlackOrWhite = configStr[1].split(",");
                String[] suffixBlackOrWhite = configStr[3].split(",");
                String domain = configStr[2];
                config.put("GET", Boolean.parseBoolean(methods[0]));
                config.put("POST", Boolean.parseBoolean(methods[1]));
                config.put("PUT", Boolean.parseBoolean(methods[2]));
                config.put("DELETE", Boolean.parseBoolean(methods[3]));
                config.put("UPDATE", Boolean.parseBoolean(methods[4]));
                config.put("HEAD", Boolean.parseBoolean(methods[5]));
                config.put("OPTIONS", Boolean.parseBoolean(methods[6]));
                config.put("DOMAIN_BLACK", Boolean.parseBoolean(domainBlackOrWhite[0]));
                config.put("DOMAIN_WHITE", Boolean.parseBoolean(domainBlackOrWhite[1]));
                config.put("DOMAIN", domain);
                config.put("SUFFIX_BLACK", Boolean.parseBoolean(suffixBlackOrWhite[0]));
                config.put("SUFFIX_WHITE", Boolean.parseBoolean(suffixBlackOrWhite[1]));
                config.put("SUFFIX", configStr[4]);
                config.put("REFERER", configStr[5]);
            } else {
                config.put("GET", false);
                config.put("POST", true);
                config.put("PUT", false);
                config.put("DELETE", false);
                config.put("UPDATE", false);
                config.put("HEAD", false);
                config.put("OPTIONS", false);
                config.put("DOMAIN_BLACK", true);
                config.put("DOMAIN_WHITE", false);
                config.put("DOMAIN", "空");
                config.put("SUFFIX_BLACK", true);
                config.put("SUFFIX_WHITE", false);
                config.put("SUFFIX", ".js,.css,.png,.gif,.jpg,.svg,.ico,.woff,.woff2,.ttf,.mp3,.mp4,.ico,.txt");
                config.put("REFERER", "http://www.c.test/");
                if (file.createNewFile()) {
                    writeConfig(false, true, false, false, false, false, false, true, false, "空", true, false, ".js,.css,.png,.gif,.jpg,.svg,.ico,.woff,.woff2,.ttf,.mp3,.mp4,.ico,.txt", "http://www.c.test/");
                }
            }

        } catch (Exception e) {
            writeConfig(false, true, false, false, false, false, false, true, false, "空", true, false, ".js,.css,.png,.gif,.jpg,.svg,.ico,.woff,.woff2,.ttf,.mp3,.mp4,.ico,.txt", "http://www.c.test/");
        }
    }

    public void requestHandler(InterceptedRequest interceptedRequest) throws MalformedURLException {

        if (
                (boolean) config.get(interceptedRequest.method())//请求方法
                        && domainFilter(interceptedRequest.url()) //域名过滤
                        && (interceptedRequest.hasHeader("Cookie") || interceptedRequest.hasHeader("Referer")) // 请求头中包含Cookie或者Referer
                        && (suffixFilter(interceptedRequest.url())) //后缀过滤
        ) {
            hashMap.put(interceptedRequest.messageId(), new CsrfMessage(table_log.getRowCount(), interceptedRequest));
            addLog(String.valueOf(interceptedRequest.messageId()), interceptedRequest.url(), interceptedRequest.method(), "", String.valueOf(interceptedRequest.hasHeader("Cookie")), "", "", "");


        }

    }

    public void responseHandler(InterceptedResponse interceptedResponse) throws MalformedURLException {
        if (hashMap.containsKey(interceptedResponse.messageId())) {
            String notice = "";
            int responseBodyLength = interceptedResponse.body().length();
//            InterceptedRequest interceptedRequest = interceptedRequestHashMap.get(interceptedResponse.messageId());
            CsrfMessage csrfMessage = hashMap.get(interceptedResponse.messageId());
            csrfMessage.baseResponse = interceptedResponse;
            table_log.setValueAt(String.valueOf(interceptedResponse.body().length()), csrfMessage.row, 4);
            if (csrfMessage.baseRequest.hasHeader("Cookie")) {
                HttpRequest httpRequestDeleteCookie = csrfMessage.baseRequest.withRemovedHeader("Cookie");
                HttpRequestResponse deleteCookieRequestResponse = Api.http().sendRequest(httpRequestDeleteCookie);
                int deleteCookieResponseBodyLength = deleteCookieRequestResponse.response().body().length();
                csrfMessage.noCookieResponse = deleteCookieRequestResponse.response();
                table_log.setValueAt(String.valueOf(deleteCookieResponseBodyLength), csrfMessage.row, 5);
                if (responseBodyLength == deleteCookieResponseBodyLength) {
                    notice += "缺少Cookie校验 ";
                }
            } else {
                csrfMessage.noCookieResponse = null;
                table_log.setValueAt("请求不存在Cookie", csrfMessage.row, 5);
            }
            String ref = (String) config.get("REFERER");
            HttpRequest httpRequestRandomReferer = csrfMessage.baseRequest.withRemovedHeader("Referer");
            httpRequestRandomReferer = httpRequestRandomReferer.withHeader("Referer", ref);
            HttpRequestResponse randomRefererRequestResponse = Api.http().sendRequest(httpRequestRandomReferer);
            csrfMessage.randomRefererResponse = randomRefererRequestResponse.response();
            URL url = new URL(ref);

            int randomRefererResponseBodyLength = randomRefererRequestResponse.response().body().length();

            table_log.setValueAt(String.valueOf(randomRefererResponseBodyLength), csrfMessage.row, 6);


            if (responseBodyLength == randomRefererResponseBodyLength) {
                notice += "缺少Referer校验 ";
            }
            if (httpRequestRandomReferer.bodyToString().contains(url.getHost())) {
                notice += "返回中包含随机Referer ";
            }
            if (notice.isEmpty()) {
                notice = "无";
            }
            table_log.setValueAt(notice, csrfMessage.row, 3);
//            table_log.setValueAt(String.valueOf(interceptedResponse.headerValue("Content-Length")), MessageId2Row.get(interceptedResponse.messageId()), 4);

        }
    }

    public boolean domainFilter(String u) throws MalformedURLException {
        URL url = new URL(u);
        return (boolean) config.get("DOMAIN_BLACK") && !((String) config.get("DOMAIN")).contains(url.getHost()) //域名黑名单
                || (boolean) config.get("DOMAIN_WHITE") && ((String) config.get("DOMAIN")).contains(url.getHost());//域名白名单
    }

    public boolean suffixFilter(String u) {
        String url = u;
        String[] suffixs = ((String) config.get("SUFFIX")).split(",");
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        for (String suffix : suffixs) {
            if (url.endsWith(suffix)) {
                return (boolean) config.get("SUFFIX_WHITE");
            }
        }
        return (boolean) config.get("SUFFIX_BLACK");
    }

    public void addLog(String id, String url, String method, String suspicious, String hasCookie, String rawLength, String noCookie, String randomReferer) {
        DefaultTableModel tableModel = (DefaultTableModel) table_log.getModel();
//        String[] headers = {"#", "方法", "url", "疑似", "Cookie"，‘’, "无Cookie", "随机Referer"};
        String[] log = {id, url, method, suspicious, hasCookie, rawLength, noCookie, randomReferer};
        tableModel.addRow(log);
    }
}
