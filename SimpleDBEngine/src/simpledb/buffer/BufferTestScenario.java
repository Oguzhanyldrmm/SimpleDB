package simpledb.buffer;

import java.io.File;

import simpledb.file.BlockId;
import simpledb.server.SimpleDB;

public class BufferTestScenario {
   public static void main(String[] args) {
      deleteRecursively(new File("buffertestscenario"));

      SimpleDB db = new SimpleDB("buffertestscenario", 400, 4);
      BufferMgr bm = db.bufferMgr();

      Buffer b0 = bm.pin(new BlockId("test", 10));
      Buffer b1 = bm.pin(new BlockId("test", 20));
      Buffer b2 = bm.pin(new BlockId("test", 30));
      Buffer b3 = bm.pin(new BlockId("test", 40));

      bm.unpin(b1);
      Buffer b4 = bm.pin(new BlockId("test", 50));

      bm.unpin(b0);
      bm.unpin(b2);
      bm.unpin(b3);
      bm.unpin(b4);

      System.out.println("Status after the initial Fig 4.9-style setup:");
      bm.printStatus();
      System.out.println();

      Buffer b5 = bm.pin(new BlockId("test", 60));
      System.out.println("After pin(60):");
      bm.printStatus();
      System.out.println();

      Buffer b6 = bm.pin(new BlockId("test", 70));
      System.out.println("After pin(70):");
      bm.printStatus();
      System.out.println();

      bm.unpin(b5);
      bm.unpin(b6);
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
