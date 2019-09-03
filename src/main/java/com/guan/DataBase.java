package com.guan;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class DataBase {
    private static String bakPath = "F:\\test";

    private static Connection getConnection(Properties p) throws SQLException {
        String driverClassName = p.getProperty("jdbc.driverClassName");
        String url = p.getProperty("jdbc.url");
        String userName = p.getProperty("jdbc.username");
        String pass = p.getProperty("jdbc.password");
        Connection con = null;
        try {
            Class.forName(driverClassName);
            con = java.sql.DriverManager.getConnection(url, userName, pass);
        } catch (Exception e) {
            return null;
        }
        return con;
    }


    private static HashMap<String, Integer> getHashMap()
            throws Exception {
        HashMap<String, Integer> h = new HashMap<String, Integer>();
        File file = new File(bakPath);
        String[] filelist = file.list();
        for (int i = 0; i < filelist.length; i++) {
            String fileName = filelist[i];
            if (fileName.toUpperCase().indexOf("TXT") != -1)
                h.put(NewTXTName(fileName), new Integer(1));
        }
        System.out.println(h.size());
        return h;
    }

    public static List<String> getNotEntryFiles(String SourceFilter) throws Exception {
        List<String> filelist = new java.util.ArrayList<String>();
        File f = new File(SourceFilter);
        String[] files = f.list();
        HashMap<String, Integer> h = getHashMap();
        for (int i = 0; i < files.length; i++) {
            if (h.get(NewFileName(files[i]).substring(0, 22)) == null)
                filelist.add(files[i]);
        }
        return filelist;
    }

    private static String NewTXTName(String filename) {
        if (filename.length() == 22) {
            return filename.toUpperCase();
        }
        String str = filename.substring(0, 2) + "_" + filename.substring(2, filename.length());
        return str.toUpperCase();
    }

    private static String NewFileName(String filename) {
        if (filename.length() == 24)
            return filename.toUpperCase();
        String str = filename.substring(0, 2) + "_" + filename.substring(2, filename.length());
        return str.toUpperCase();
    }

    public static boolean getCall(Properties properties, boolean isOther) throws Exception {
        boolean flag = true;
        System.out.println("======getCall()=======begin");
        Connection con = getConnection(properties);

        String callStatment = isOther ? "{call P_TCN_ENTRY_OTHER(?)}" : "{call P_TCN_ENTRY(?)}";
        CallableStatement cs = con.prepareCall(callStatment);

        cs.registerOutParameter(1, 12);
        cs.execute();
        String str = cs.getString(1);
        cs.close();
        con.close();
        cs.close();
        System.out.println("======getCall()=======end");
        if (str.equals("false")) {
            flag = false;
        }
        return flag;
    }
}

