// Title:       SearchAgentComponent
// @version:    $Id: PrefixQueryLexer.java,v 1.1.1.1 2004/06/18 06:38:17 ibbo Exp $
// Copyright:   Copyright (C) 1999, Knowledge Integration Ltd.
// @author:     Ian Ibbotson ( ibbo@k-int.com )
// Company:     KI
// Description: Your description
//

//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2.1 of
// the license, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite
// 330, Boston, MA  02111-1307, USA.
// 

package org.jzkit.search.util.QueryModel.PrefixString;

/**
  PrefixQueryParser : Parse a prefix string
                      based on "Little Language pattern (Patterns in Java, Mark Grand, 1998)
  * @author Ian Ibbotson
  * @version $Id: PrefixQueryLexer.java,v 1.1.1.1 2004/06/18 06:38:17 ibbo Exp $ 
  */
 

// Import auxillary Java classes
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

public class PrefixQueryLexer
{
  StreamTokenizer input = null;

  static final int ERROR=-1;
  static final int AND=1;
  static final int OR=2;
  static final int NOT=3;
  static final int ATTR=4;
  static final int ATTRSET=5;
  static final int TERM=6;
  static final int SPACE=7;
  static final int EQUALS=8;
  static final int EOL=9;
  static final int EOF=10;
  static final int NUMBER=11;
  static final int ELEMENTNAME=12;
  static final int PARAM=13;
  // static final int ATTSET=13;

  public PrefixQueryLexer(Reader r)
  {
    input = new StreamTokenizer(r);
    input.resetSyntax();
    input.eolIsSignificant(true);
    input.parseNumbers();
    input.wordChars('a', 'z');
    input.wordChars('A', 'Z');
    input.wordChars('0', '9');
    input.wordChars('_', '_');
    input.wordChars('-', '-');
    input.wordChars('@', '@');
    input.ordinaryChar('(');
    input.ordinaryChar(')');
    input.quoteChar('"');
    input.whitespaceChars(' ',' ');
  }

  String getString()
  {
    return input.sval;
  }

  int getInt()
  {
    return (int)input.nval;
  }

  double getNumber()
  {
    return input.nval;
  }

  int nextToken()
  {
    int token=0;
    try
    {
      switch (input.nextToken())
      {
        case StreamTokenizer.TT_EOF:
          token=EOF;
          break;
        case StreamTokenizer.TT_EOL:
          token=EOL;
          break;
        case StreamTokenizer.TT_WORD:
          if ( input.sval.equalsIgnoreCase("@AND") )
            token=AND;
          else if ( input.sval.equalsIgnoreCase("@OR") )
            token = OR;
          else if ( input.sval.equalsIgnoreCase("@NOT") )
            token = NOT;
          else if ( input.sval.equalsIgnoreCase("@ATTR") )
            token = ATTR;
          else if ( input.sval.equalsIgnoreCase("@ATTRSET") )
            token = ATTRSET;
          else if ( input.sval.equalsIgnoreCase("@ELEMENTNAME") )
            token = ELEMENTNAME;
          else if ( input.sval.equalsIgnoreCase("@PARAM") )
            token = PARAM;
          else 
            token = TERM;
          break;
        case StreamTokenizer.TT_NUMBER:
          token=NUMBER;
          break;
        case '=':
          token = EQUALS;
          break;
        case '"':
            token = TERM;
          break;
      }
    }
    catch(IOException e)
    {
       token=EOF;
    }
    return token;
  }
}
