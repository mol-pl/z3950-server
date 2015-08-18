package org.jzkit.client.bsh;

import bsh.Interpreter;
import bsh.util.JConsole;
import java.io.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class jzsh {

  public ApplicationContext app_context = null;
  public static Log log = LogFactory.getLog(jzsh.class);

  public static void main(String[] args) throws bsh.EvalError {
    ApplicationContext app_context = new ClassPathXmlApplicationContext( "DefaultApplicationContext.xml" );
    jzsh new_shell = new jzsh(app_context);
    new_shell.start();
  }


  public jzsh(ApplicationContext app_context) {
    this.app_context = app_context;

    Runtime.getRuntime().addShutdownHook(
      new Thread() {
        public void run() {
        stopServer();
      }
    });
  }

  public void start() throws bsh.EvalError {
    String welcomeMessage = "Welcome to the jzsh - The JZKit Shell for testing and debugging JZKit search problems\nPlease use jzHelp();<cr> for help, jzStatus();<cr> for status reports";
    Interpreter i = new Interpreter(new InputStreamReader(System.in),System.out, System.err, true );

    i.set("bsh.prompt","jzsh > ");
    i.set("jzctx",app_context);
    i.set("jzcfg",app_context.getBean("JZKitConfig"));
    i.println(welcomeMessage);
    i.eval("importCommands(\"org.jzkit.client.bsh.commands\");");
    i.eval("jzHelp();");
    i.println("");
    i.run();
  }

  public void stopServer() {
    log.debug("Shutting down embedded tomcat...");
  }
}

