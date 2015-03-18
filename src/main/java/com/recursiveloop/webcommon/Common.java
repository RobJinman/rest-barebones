// This file is property of Recursive Loop Ltd.
//
// Author: Rob Jinman
// Web: http://recursiveloop.org
// Copyright Recursive Loop Ltd 2015
// Copyright Rob Jinman 2015


package com.recursiveloop.webcommon;

import java.lang.NumberFormatException;


public class Common {
  private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

  private static byte charToNum(char c) {
    switch (c) {
      case '0': return 0x0;
      case '1': return 0x1;
      case '2': return 0x2;
      case '3': return 0x3;
      case '4': return 0x4;
      case '5': return 0x5;
      case '6': return 0x6;
      case '7': return 0x7;
      case '8': return 0x8;
      case '9': return 0x9;
      case 'a':
      case 'A': return 0xA;
      case 'b':
      case 'B': return 0xB;
      case 'c':
      case 'C': return 0xC;
      case 'd':
      case 'D': return 0xD;
      case 'e':
      case 'E': return 0xE;
      case 'f':
      case 'F': return 0xF;
      default:
        throw new NumberFormatException(String.format("%c is not a valid hex digit", c));
    }
  }

  public static byte[] fromHex(String hex) {
    char[] hexChars = hex.toCharArray();
    int n = hexChars.length;
    byte[] bytes = new byte[(int)Math.ceil(n / 2.0)];

    for (int c = 0; c < n; c += 2) {
      byte i = (byte)(charToNum(hexChars[c]) << 4);
      byte j = (c + 1 < n) ? charToNum(hexChars[c + 1]) : 0;

      bytes[c / 2] = (byte)(i + j);
    }

    return bytes;
  }

  public static String toHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];

    for (int b = 0; b < bytes.length; ++b) {
      byte i = (byte)((bytes[b] & 0xF0) >> 4);
      byte j = (byte)(bytes[b] & 0x0F);

      hexChars[b * 2] = hexArray[i];
      hexChars[b * 2 + 1] = hexArray[j];
    }

    return new String(hexChars);
  }
}
