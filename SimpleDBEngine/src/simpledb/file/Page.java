package simpledb.file;

import java.nio.ByteBuffer;
import java.nio.charset.*;

public class Page {
   private ByteBuffer bb;
   public static Charset CHARSET = StandardCharsets.US_ASCII;

   // For creating data buffers
   public Page(int blocksize) {
      bb = ByteBuffer.allocateDirect(blocksize);
   }
   
   // For creating log pages
   public Page(byte[] b) {
      bb = ByteBuffer.wrap(b);
   }

   public int getInt(int offset) {
      return bb.getInt(offset);
   }

   public void setInt(int offset, int n) {
      if (!fits(offset, Integer.BYTES)) {
         System.out.println("ERROR: The integer " + n + " does not fit at location " + offset + " of the page");
         return;
      }
      bb.putInt(offset, n);
   }

   public byte[] getBytes(int offset) {
      bb.position(offset);
      int length = bb.getInt();
      byte[] b = new byte[length];
      bb.get(b);
      return b;
   }

   public void setBytes(int offset, byte[] b) {
      int needed = Integer.BYTES + b.length;
      if (!fits(offset, needed)) {
         System.out.println("ERROR: The byte array of length " + b.length + " does not fit at location " + offset + " of the page");
         return;
      }
      bb.position(offset);
      bb.putInt(b.length);
      bb.put(b);
   }
   
   public String getString(int offset) {
      StringBuilder sb = new StringBuilder();
      int pos = offset;
      while (fits(pos, Character.BYTES)) {
         char ch = bb.getChar(pos);
         if (ch == '\0')
            return sb.toString();
         sb.append(ch);
         pos += Character.BYTES;
      }
      return sb.toString();
   }

   public void setString(int offset, String s) {
      int needed = maxLength(s.length());
      if (!fits(offset, needed)) {
         System.out.println("ERROR: The string \"" + s + "\" does not fit at location " + offset + " of the page");
         return;
      }

      int pos = offset;
      for (int i = 0; i < s.length(); i++) {
         bb.putChar(pos, s.charAt(i));
         pos += Character.BYTES;
      }
      bb.putChar(pos, '\0');
   }

   public static int maxLength(int strlen) {
      return (strlen + 1) * Character.BYTES;
   }

   // a package private method, needed by FileMgr
   ByteBuffer contents() {
      bb.position(0);
      return bb;
   }

   private boolean fits(int offset, int length) {
      return offset >= 0 && length >= 0 && offset + length <= bb.capacity();
   }
}
