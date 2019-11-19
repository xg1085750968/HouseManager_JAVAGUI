package edu.fzu.house.gui.Seller.panel;

import com.sorm.po.House;
import com.sorm.util.ReflectionUtil;
import edu.fzu.house.core.ActionInterfaces.ActionInterface;
import edu.fzu.house.core.Manager.ManagerFunc;
import edu.fzu.house.core.columnInterface.HouseColumImpl;
import edu.fzu.house.core.columnInterface.SellerHouseColumImpl;
import edu.fzu.house.core.confirmInterface.*;
import edu.fzu.house.core.login.MysqlQuery;

import edu.fzu.house.gui.Manager.ManagerFrame;
import edu.fzu.house.gui.Manager.panel.HousePanel;
import edu.fzu.house.gui.Manager.panel.modelPanel;
import edu.fzu.house.gui.Manager.panel.msgPanel;
import edu.fzu.house.gui.Manager.table.DataTablePanel;
import edu.fzu.house.gui.Seller.SellerFrame;
import edu.fzu.house.gui.login.panel.TextButton;
import edu.fzu.house.util.IOUtil;
import edu.fzu.house.util.ImageUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.List;

public class SellerSetting extends modelPanel {
    public  String Paramsql = "select hid,hname,htype,photo,addressid,hprice,hstate from house where uname=? order by hprice";

    public JLayeredPane layer_panel=new JLayeredPane();

    public static Color color1=new Color(0xC9DEFB); //单行文本背景色
    public static  Color color2=new Color(246,250,255);  //双行文本背景
    public static Color pagecolor=new Color(0xFFFFFF); //背景颜色

    //生成一栏个人信息项
    public JPanel generateSetting(String text, String fieldName, Color c, Color font, Confirm con) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(500, 40);
        panel.setBackground(c);
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
        label.setForeground(font);
        label.setBounds(30, -10, 100, 60);
        panel.add(label);

        //输入框 默认不可编辑 点击修改后可编辑
        JTextField textField = new JTextField();
        textField.setBounds(150, 5, 250, 30);
        textField.setOpaque(false); //透明
        textField.setBorder(null);  //无边框
        textField.setEditable(false);//不可编辑
        String body = (String) ReflectionUtil.invokeGet(fieldName, SellerFrame.user);
        if (body == null || body.equals(""))
            body = "(未设置)";
        textField.setText(body);
        //设置事件 失去焦点后修改并更新数据库
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!ManagerFunc.confirm(textField.getText(), con)) {
                    textField.requestFocus();
                    return;
                }
                ReflectionUtil.invokeSet(fieldName, SellerFrame.user, textField.getText());
                MysqlQuery.query.update(SellerFrame.user, new String[]{fieldName});
                textField.setEditable(false);//文本框不可编辑
                if (fieldName.equals("sname")) {
                    SellerFrame.id_label.setText(textField.getText());
                    SellerFrame.name_label.setText(textField.getText());
                }
            }
        });
        panel.add(textField);
        //修改按钮
        TextButton modify = new TextButton("修改", new Rectangle(420, 0, 70, 40));
        modify.addActionListener(e -> {
            textField.setEditable(true);
            textField.requestFocus();
        });
        panel.add(modify);

        //确认按钮
        TextButton confirm = new TextButton("确认", new Rectangle(380, 0, 70, 40));
        panel.add(confirm);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    confirm.requestFocus();
            }
        });
        return panel;
    }

    public SellerSetting() {

        super();
        layer_panel.setBounds(0,0,820,750);

        //   layer_panel.setBackground(Color.pink);
        JPanel bk=new JPanel();
        bk.setBounds(0,0,820,750);
        bk.setBackground(pagecolor);
        layer_panel.add(bk,JLayeredPane.DEFAULT_LAYER);
        //个人信息标签
        JLabel msg1 = new JLabel("个人信息");
        msg1.setFont(new Font(Font.SERIF, Font.BOLD, 12));
        msg1.setBounds(40, 23, 80, 80);
        layer_panel.add(msg1,new Integer(100));

        //开始创造用户信息表格 自动实现了修改 确定功能
        Color c= new Color(0xD5F3F5);
        JPanel panel = generateSetting("用户名:", "sname", c,  new Color(0x000000), null);
        panel.setBounds(40, 80, 500, 40);
        layer_panel.add(panel,new Integer(100));
        panel = generateSetting("性别:", "ssex",c, new Color(0x000000), new SexConfirm());
        panel.setBounds(40, 120, 500, 40);
        layer_panel.add(panel,new Integer(100));
        panel = generateSetting("邮箱:", "semail", c, new Color(0x000000), new EmialConfirm());
        panel.setBounds(40, 160, 500, 40);
        layer_panel.add(panel,new Integer(100));
        panel = generateSetting("身份证号:", "scardid",c, new Color(0x000000), new IDConfirm());
        panel.setBounds(40, 200, 500, 40);
        layer_panel. add(panel,new Integer(100));
        panel = generateSetting("手机号:", "sphone",c, new Color(0x000000), new PhoneConfirm());
        panel.setBounds(40, 240, 500, 40);
        layer_panel.add(panel,new Integer(100));

        //头像框
        JPanel photo = new JPanel();
        photo.setLayout(null);
        photo.setSize(200, 40);
        photo.setBackground(c);
        photo.setBorder(BorderFactory.createLineBorder(Color.gray));
        photo.setBounds(540, 80, 200, 200);
        //头像
        ImageIcon image = new ImageIcon(SellerFrame.user.getPhoto());
        image = ImageUtil.CutCircleImage(image, 100);
        JLabel label = new JLabel(image);
        label.setBounds(40, 20, 100, 100);
        photo.add(label);

        //修改头像按钮
        TextButton photo_button = new TextButton("修改头像", new Rectangle(30, 120, 120, 40));
        photo_button.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
        photo_button.setForeground(Color.gray);
        photo.add(photo_button);
        //功能
        photo_button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.jpg/png/jpeg", "jpg", "png", "jpeg");
            fileChooser.setFileFilter(filter);
            fileChooser.showOpenDialog(null);

            File file = fileChooser.getSelectedFile();
            BufferedInputStream bis = null;
            ByteArrayOutputStream bos = null;
            if (file == null)
                return;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                int len = -1;
                bos = new ByteArrayOutputStream();
                byte[] dates = new byte[1024 * 1024];
                while ((len = bis.read(dates)) != -1) {
                    bos.write(dates, 0, len);
                }
                bos.flush();
                SellerFrame.user.setPhoto(bos.toByteArray());
                int count = MysqlQuery.query.update(SellerFrame.user, new String[]{"photo"});
                if (count == 1) {
                    ImageIcon photo1 = new ImageIcon(SellerFrame.user.getPhoto());
                    photo1 = ImageUtil.CutCircleImage(photo1, 40);
                    SellerFrame.photo_label.setIcon(photo1);

                    ImageIcon photo2 = new ImageIcon(SellerFrame.user.getPhoto());
                    photo2 = ImageUtil.CutCircleImage(photo2, 100);
                    SellerFrame.photo_label2.setIcon(photo2);

                    ImageIcon photo3 = new ImageIcon(SellerFrame.user.getPhoto());
                    photo3 = ImageUtil.CutCircleImage(photo3, 100);
                    label.setIcon(photo3);
                } else {
                    System.out.println("文件过大");
                }

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                IOUtil.Close(bos, bis);
            }

        });
        // photo.setBounds();
        layer_panel.add(photo,new Integer(100));

        //*消息列表*//*
        JLabel msg2 = new JLabel("拥有房源");
        msg2.setFont(new Font(Font.SERIF, Font.BOLD, 12));
        msg2.setBounds(40, 260, 80, 80);
        layer_panel. add(msg2,new Integer(100));


        //生成数据库表格

        DataTablePanel datetable=new DataTablePanel(Paramsql, House.class ,new Object[]{SellerFrame.user.getUname()},
                new String[]{"序列号","房名","房型","照片","地址","售价","状态"},7, new SellerHouseColumImpl(),
                new Rectangle(10,320,650,400 ),layer_panel);
        datetable.addMap("查看", new ActionInterface() {
            @Override
            public void action(Object[] Keys) {
                java.util.List<Object> key= (List<Object>) Keys[0];
                SellerSetting m=new SellerSetting();
                HousePanel panel=new HousePanel((String)key.get(0),m, SellerFrame.user);
                layer_panel.add(panel,new Integer(300));
            }
        });
        datetable.setColor1(color1);
        datetable.setColor2(color2);
        datetable.setPagecolor(pagecolor);

        datetable.setSize(8);
        datetable.generateTable();
//        JPanel msgPanel=datetable.generateMsg();
//        layer_panel.add(msgPanel,new Integer(100));


        //转页

        add(layer_panel);

    }



}
