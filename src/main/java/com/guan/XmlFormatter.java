package com.guan;

import java.io.StringReader;
import java.io.StringWriter;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
 
public class XmlFormatter {
 
    public static String format(String str) throws Exception {
        SAXReader reader = new SAXReader();
        StringReader in = new StringReader(str);
        org.dom4j.Document doc = reader.read(in);
        OutputFormat formater = OutputFormat.createPrettyPrint();
        formater.setEncoding("utf-8");
        StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter(out, formater);
        writer.write(doc);
 
        writer.close();
        //System.out.println(out.toString());
        return out.toString();
    }


}
