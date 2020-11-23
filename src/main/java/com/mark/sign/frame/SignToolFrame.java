package com.mark.sign.frame;

import com.android.apksigner.ApkSignerTool;
import com.mark.sign.Config;
import com.mark.sign.utils.StringUtils;
import com.mark.sign.utils.Utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignToolFrame extends JFrame {
    private JTextField apkPathField,keyStorePathField,apkOutputPathField;
    private JPasswordField keyPasswordField,aliasPasswordField;
    private JComboBox<String> comboBox;

    private String apkInPath,apkOutPath,keyStorePath,keyStorePassword,keyAliasPassword,keyAlias;

    private Map<String,String> lastConfigMap = new HashMap<String, String>();

    private final String DEFAULT_TIPS_APK_IN = "输入apk文件的路径";
    private final String DEFAULT_TIPS_KEYSTORE_IN = "输入KeyStore文件的路径";
    private final String DEFAULT_TIPS_APK_OUT = "输入apk文件的保存路径";

    private Box vBox = Box.createVerticalBox();

    public SignToolFrame() throws HeadlessException {
        initJFrame();
        createApkFileLine();
        createKeyStoreFileLine();
        createKeyPasswordLine();
        createAliasLine();
        createAliasPasswordLine();
        createOutputLine();
        createSignBtnLine();
        setContentPane(vBox);
        readConfig();
    }

    private void createSignBtnLine() {
        JPanel panel = new JPanel();
        JButton signBtn = new JButton("一键签名");
        signBtn.addActionListener(signActionListener);
        panel.add(signBtn);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        vBox.add(panel);
    }

    private void createOutputLine() {
        JPanel panel = new JPanel();
        JLabel labelApkOutput = new JLabel("APK保存路径：");
        apkOutputPathField = new JTextField(28);
        apkOutputPathField.setText(DEFAULT_TIPS_APK_OUT);
        setJTextCommonMargin(apkOutputPathField);
        panel.add(labelApkOutput);
        panel.add(apkOutputPathField);
        JButton apkOutputBtn = new JButton("选择");
        apkOutputBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectApkOutputAction();
            }
        });
        panel.add(apkOutputBtn);
        vBox.add(panel);
    }

    private void createAliasPasswordLine() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("KeyAlias密码：");
        aliasPasswordField = new JPasswordField(34);
        aliasPasswordField.setText("");
        setJTextCommonMargin(aliasPasswordField);
        panel.add(label);
        panel.add(aliasPasswordField);
        vBox.add(panel);
    }

    private void createAliasLine() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Key  Alias       ：");
//        JTextField KeyStoreField = new JTextField(34);
        comboBox = new JComboBox<String>();
        comboBox.addItemListener(itemListener);
        comboBox.addItem("--请选择--");
        comboBox.setPreferredSize(new Dimension(385,26));
//        KeyStoreField.setMargin(new Insets(4, 4, 4, 4));
        panel.add(label);
        panel.add(comboBox);
        vBox.add(panel);
    }

    private void createKeyPasswordLine() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("KeyStore密码：");
        keyPasswordField = new JPasswordField(34);
        keyPasswordField.setText("");
        keyPasswordField.getDocument().addDocumentListener(pwdListener);
        keyPasswordField.setMargin(new Insets(4, 4, 4, 4));
        panel.add(label);
        panel.add(keyPasswordField);
        vBox.add(panel);
    }

    private void createKeyStoreFileLine() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("KeyStore路径：");
        keyStorePathField = new JTextField(28);
        keyStorePathField.setText(DEFAULT_TIPS_KEYSTORE_IN);
        keyStorePathField.getDocument().addDocumentListener(keyStoreFieldListener);
        setJTextCommonMargin(keyStorePathField);
        panel.add(label);
        panel.add(keyStorePathField);
        JButton button = new JButton("选择");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectKeyStoreAction();
            }
        });
        panel.add(button);
        vBox.add(panel);
    }

    private void initJFrame() {
        setTitle("APK签名工具 create by xq");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        Toolkit toolkit=Toolkit.getDefaultToolkit();
        Image icon = toolkit.getImage("icon/apk_sign.png");
        setIconImage(icon);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createApkFileLine() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("APK文件路径：");
        apkPathField = new JTextField(28);
        apkPathField.setText(DEFAULT_TIPS_APK_IN);
        setJTextCommonMargin(apkPathField);
        panel.add(label);
        panel.add(apkPathField);
        JButton choseBtn = new JButton("选择");
        choseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectApkAction();
            }
        });
        panel.add(choseBtn);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 0, 50));
        vBox.add(panel);
    }

    private void selectApkAction() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("APK文件(*.apk)", "apk");
        apkInPath = choseFileDialog(filter);
        if (apkInPath != null){
            apkPathField.setText(apkInPath);
        }
    }

    private void selectApkOutputAction() {
        apkOutPath = choseFileDialog(null,JFileChooser.DIRECTORIES_ONLY);
        if (apkOutPath != null){
            apkOutputPathField.setText(apkOutPath);
        }
    }

    private String joinOutputPath() {
        if (StringUtils.notEmpty(apkInPath) && StringUtils.notEmpty(apkOutPath)){
            String apkName = apkInPath.substring(apkInPath.lastIndexOf(File.separator) + 1,apkInPath.lastIndexOf("."));
            String path = apkOutPath .concat(File.separator).concat(apkName).concat("_signed.apk") ;
            return path;
        }
        return "";
    }

    private void selectKeyStoreAction() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("keystore文件(*.key/*.jks)", "key","jks");
        keyStorePath = choseFileDialog(filter);
        if (keyStorePath != null){
            keyStorePathField.setText(keyStorePath);
        }
    }

    private String choseFileDialog(FileNameExtensionFilter filter,int selectionMode){
        JFileChooser fd = new JFileChooser();
        fd.setCurrentDirectory(new File("."));
        fd.setFileSelectionMode(selectionMode);//文件模式
        fd.setMultiSelectionEnabled(false);
        if (filter != null){
            fd.setFileFilter(filter);
        }
        int result = fd.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION){
            File selectedFile = fd.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

    private String choseFileDialog(FileNameExtensionFilter filter){
        return choseFileDialog(filter,JFileChooser.FILES_ONLY);
    }

    private void setJTextCommonMargin(JTextField field){
        if (field != null)
            field.setMargin(new Insets(4, 4, 4, 4));
    }

    private ItemListener itemListener = new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
            // 只处理选中的状态
            if (e.getStateChange() == ItemEvent.SELECTED) {
                int selectedIndex = comboBox.getSelectedIndex();
                keyAlias = selectedIndex != 0 ? (String) comboBox.getSelectedItem() : null;
            }
        }
    };

    private void getPassword(DocumentEvent e){
        Document doc = e.getDocument();
        try {
            String s = doc.getText(0, doc.getLength()); //返回文本框输入的内容
            keyStorePassword = s;
            if (s != null && s.length() > 6){
                readAlias();
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void readAlias() {
        if (StringUtils.notEmpty(keyStorePath) && StringUtils.notEmpty(keyStorePassword)){
            String alias = Utils.readAlias(keyStorePath, keyStorePassword);
            if (StringUtils.notEmpty(alias)){
                comboBox.addItem(alias);
                comboBox.setSelectedIndex(comboBox.getItemCount() -1);
            }else {
                comboBox.setSelectedIndex(0);
                if (comboBox.getItemCount() >= 2){
                    comboBox.removeItemAt(comboBox.getItemCount()-1);
                }
            }
        }
    }


    private DocumentListener pwdListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            getPassword(e);
        }

        public void removeUpdate(DocumentEvent e) {
            getPassword(e);
        }

        public void changedUpdate(DocumentEvent e) {}
    };

    private DocumentListener keyStoreFieldListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            readAlias();
        }

        public void removeUpdate(DocumentEvent e) {
            readAlias();;
        }

        public void changedUpdate(DocumentEvent e) {}
    };

    private ActionListener signActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            keyAliasPassword = new String(aliasPasswordField.getPassword());
            if (verifyInput()){
                saveConfig();
                startSign();
            }
        }
    };

    private void saveConfig() {
        lastConfigMap.clear();
        lastConfigMap.put(Config.KEY_STORE_PATH,keyStorePath);
        lastConfigMap.put(Config.KEY_STORE_PASSWORD,keyStorePassword);
        lastConfigMap.put(Config.KEY_ALIAS_PASSWORD,keyAliasPassword);
        lastConfigMap.put(Config.APK_OUTPUT_PATH,apkOutPath);
        lastConfigMap.put(Config.KEY_ALIAS,keyAlias);

        Config.init().saveConfig(lastConfigMap);
    }

    private void readConfig(){
        Map<String, String> config = Config.init().getConfig();
        if (config != null){
            String outputPath = config.get(Config.APK_OUTPUT_PATH);
            String keyAliasPwd = config.get(Config.KEY_ALIAS_PASSWORD);
            String keyStore = config.get(Config.KEY_STORE_PATH);
            String keyStorePwd = config.get(Config.KEY_STORE_PASSWORD);

            if (apkOutputPathField != null && outputPath != null){
                apkOutPath = outputPath;
                apkOutputPathField.setText(outputPath);
            }

            if (aliasPasswordField != null && keyAliasPwd != null){
                keyAliasPassword = keyAliasPwd;
                aliasPasswordField.setText(keyAliasPwd);
            }

            if (keyStorePathField != null && keyStore != null){
                keyStorePath = keyStore;
                keyStorePathField.setText(keyStore);
            }

            if (keyPasswordField != null && keyStorePwd != null){
                keyStorePassword = keyStorePwd;
                keyPasswordField.setText(keyStorePwd);
            }

        }
    }

    private void startSign() {
        String command = String.format("sign;--ks;%s;--ks-key-alias;%s;--ks-pass;pass:%s;--key-pass;pass:%s;--out;%s;%s", keyStorePath, keyAlias, keyStorePassword, keyAliasPassword, joinOutputPath(), apkInPath);
        String[] commands = command.split(";");
        String s = Arrays.toString(commands);
        System.out.println(s);

        try {
            ApkSignerTool.main(commands);
        } catch (Exception e) {
            showDialog("签名出错，请检查alias密码是否有误");
            return;
//            e.printStackTrace();
        }
        showDialog("签名成功！",JOptionPane.INFORMATION_MESSAGE);
    }


    private int getOutputFileCount() {
        String path = apkOutputPathField.getText().trim();
        File file = new File(path);
        if (file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            if (files != null)
                return files.length;
        }
        return -1;
    }

    private boolean verifyInput() {
        if (StringUtils.isEmpty(apkInPath)){
            showDialog("请选择APK文件的存放路径");
            return false;
        }else if(StringUtils.isEmpty(keyStorePath)){
            showDialog("请选择KeyStore文件");
            return false;
        }else if(StringUtils.isEmpty(keyStorePassword)){
            showDialog("请输入KeyStore文件的密码");
            return false;
        }else if(StringUtils.isEmpty(keyAliasPassword)){
            showDialog("Key Alias密码不能为空");
            return false;
        }else if(StringUtils.isEmpty(keyAlias)){
            showDialog("请选择Alias");
            return false;
        }else if(StringUtils.isEmpty(apkOutPath)){
            showDialog("请选择保存路径");
            return false;
        }
        return true;
    }

    private void showDialog(String title,String msg){
        showDialog(title,msg,JOptionPane.WARNING_MESSAGE);
    }

    private void showDialog(String title,String msg,int type){
        JOptionPane.showMessageDialog(
                this,
                msg,
                title,
                type
        );
    }

    private void showDialog(String msg){
        showDialog("提示",msg);
    }

    private void showDialog(String msg,int type){
        showDialog("提示",msg,type);
    }

}
