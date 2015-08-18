package org.jzkit.client.bsh.commands;

import bsh.*;

public class jzHelp {

  public jzHelp() {
    System.out.println("jzHelp constructor");
  }

  public static void invoke( Interpreter env, CallStack callstack ) {
    env.println("jzHelp() - This message");
    env.println("jzStatus() - List status");
    env.println("jzList([\"sessions\") - List active sessions");
    env.println("jzNewSession([\"sessions\") - Create new session");
    env.println("jzClose([\"session-ids\") - Close the identified session");
    env.println("jzList([\"sessions\")");
    env.println("jzTidy() - cleanup");
  }

}
