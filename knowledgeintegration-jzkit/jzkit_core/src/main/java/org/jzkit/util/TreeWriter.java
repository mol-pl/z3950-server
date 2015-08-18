package org.jzkit.util;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.PrintStream;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TreeWriter {
    
    public class TreeNode implements Comparable {
        int type;
        String name;
        Object value;
        
        public int compareTo(Object o) {
            return new Integer(type).compareTo(new Integer(((TreeNode)o).type));
        }
        
    }

    static final int NULL_TYPE = 0;
    static final int JAVA_TYPE = 1;
    static final int CLASS_TYPE = 2;
    static final int COLLECTION_TYPE = 3;
    
    PrintStream pout;
    String lineBuffer = "";
    private static Log log = LogFactory.getLog(TreeWriter.class);
    
    public TreeWriter(PrintStream out) {
        this.pout = out;
    }
    
    public void write(Object o) {
      TreeNode node = new TreeNode();
      node.type = classify(o);
      node.value = o;
      writeNode(node, 0);
    }
        
    public void write(Object o, String rootNode) {
      TreeNode node = new TreeNode();
      node.type = classify(o);
      node.name = rootNode;
      node.value = o;
      writeNode(node, 0);
    }

    private void writeNode(TreeNode node, int indent) {
        switch (node.type) {
            case NULL_TYPE:
                break;
            case JAVA_TYPE:
                for(int j=0;j<indent;j++,print(" "));
                print(node.name + ": ");
                if (node.value.getClass().getName().equals("java.util.GregorianCalendar")) {
                    String msg;
                    Date theDate = new Date();
                    theDate.setTime(((Calendar)node.value).getTimeInMillis());
                    msg = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT).format(theDate);        
                    println(msg);
                } else {
                    println(node.value.toString());
                }
                break;
            case CLASS_TYPE:
                for(int j=0;j<indent;j++,print(" "));
                println(node.name);
                writeClass(node.value, indent+2);
                break;
            case COLLECTION_TYPE:
                if (node.name != null) {
                    for(int j=0;j<indent;j++,print(" "));
                    println(node.name);
                    indent += 2;
                }
                Collection col = (Collection)node.value;
                for (Iterator it=col.iterator();it.hasNext(); ) {
                    Object item = it.next();
                    writeClass(item, indent);
                }
                break;
            default:
                System.out.println("UNEXPECTED TYPE");
        }
    }
    
    private void writeClass(Object o, int indent) {
        for(int j=0;j<indent;j++,print(" "));
        println("Class: " + o.getClass().getName());

        ArrayList nodes = new ArrayList();
        
        Method[] methods = o.getClass().getMethods();
        for (int i=0;i<methods.length;i++) {
            Method method=methods[i];
            String declaringClassName = method.getDeclaringClass().getName();
            if (declaringClassName.startsWith("org.jzkit")) {
                if (method.getName().startsWith("get")) {
                    Object res = null;
                    try {
                        res = method.invoke(o,(Object[])null);
                        
                        TreeNode node = new TreeNode();
                        node.type = classify(res);
                        node.name = method.getName().substring(3);
                        node.value = res;
                        nodes.add(node);
                        
                    } catch (IllegalAccessException iae) {
                        println(iae.toString());
                    } catch (InvocationTargetException ite) {
                        for(int j=0;j<indent+2;j++,print(" "));
                        print(method.getName().substring(3) + ": ");
                        println("Exception");
                    }
                }
            }
        }

        Collections.sort(nodes);

        for (Iterator i=nodes.iterator(); i.hasNext(); ) {
            TreeNode node = (TreeNode)i.next();
            
            writeNode(node, indent+2);
        }
    }

    private int classify(Object o) {
        if (o == null) return NULL_TYPE;
        Collection col = null;

        try {
            col = (Collection)o;
            return COLLECTION_TYPE;
        } catch (ClassCastException cce) {
            String className = o.getClass().getName();
            if (className.startsWith("java")) {
                return JAVA_TYPE;
            } else {
                return CLASS_TYPE;
            }
        }
    }
    
    private void print(String str) {
        lineBuffer += str;
    }

  private void println(String str) {
    if (this.pout != null) {
      pout.println(lineBuffer + str);
    }
    log.debug(lineBuffer + str);
    lineBuffer = "";
  }

}
