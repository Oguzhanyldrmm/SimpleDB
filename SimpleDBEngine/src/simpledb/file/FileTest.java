package simpledb.file;

import java.io.*;
import simpledb.server.SimpleDB;

public class FileTest {
   public static void main(String[] args) throws IOException {
      deleteRecursively(new File("filetest"));

      SimpleDB db = new SimpleDB("filetest", 400, 8);
      FileMgr fm = db.fileMgr();
      BlockId blk = new BlockId("testfile", 2);
      int pos1 = 88;

      System.out.println("Testing normal page writes...");
      Page p1 = new Page(fm.blockSize());
      p1.setString(pos1, "abcdefghijklm");
      int size = Page.maxLength("abcdefghijklm".length());
      int pos2 = pos1 + size;
      p1.setInt(pos2, 345);
      byte[] raw = {1, 2, 3, 4};
      int pos3 = pos2 + Integer.BYTES;
      p1.setBytes(pos3, raw);
      fm.write(blk, p1);

      Page p2 = new Page(fm.blockSize());
      fm.read(blk, p2);
      System.out.println("offset " + pos2 + " contains " + p2.getInt(pos2));
      System.out.println("offset " + pos1 + " contains " + p2.getString(pos1));
      byte[] readBytes = p2.getBytes(pos3);
      System.out.println("offset " + pos3 + " contains byte array of length " + readBytes.length);

      System.out.println();
      System.out.println("Testing overflow protection...");
      Page p3 = new Page(20);
      p3.setInt(0, 111);
      p3.setBytes(4, new byte[] {9, 8, 7});
      p3.setString(12, "ABCD");
      p3.setInt(18, 12);
      p3.setBytes(15, new byte[] {1, 2, 3, 4});
      p3.setString(14, "toolong");
      System.out.println("offset 0 still contains " + p3.getInt(0));
      System.out.println("offset 4 byte array length is still " + p3.getBytes(4).length);
   }

   private static void deleteRecursively(File file) {
      if (!file.exists())
         return;
      if (file.isDirectory()) {
         File[] children = file.listFiles();
         if (children != null) {
            for (File child : children)
               deleteRecursively(child);
         }
      }
      file.delete();
   }
}
