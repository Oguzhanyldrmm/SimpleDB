package simpledb.tx.recovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import simpledb.file.Page;
import simpledb.log.LogMgr;
import simpledb.tx.Transaction;

/**
 * The non-quiescent CHECKPOINT log record.
 */
public class NQCheckpoint implements LogRecord {
   private List<Integer> txnums;

   public NQCheckpoint(Page p) {
      txnums = new ArrayList<Integer>();
      int countpos = Integer.BYTES;
      int count = p.getInt(countpos);
      int txpos = countpos + Integer.BYTES;
      for (int i = 0; i < count; i++) {
         txnums.add(p.getInt(txpos));
         txpos += Integer.BYTES;
      }
   }

   public int op() {
      return NQCKPT;
   }

   /**
    * Checkpoint records have no associated transaction,
    * and so the method returns a "dummy", negative txid.
    */
   public int txNumber() {
      return -1; // dummy value
   }

   /**
    * Does nothing, because a checkpoint record
    * contains no undo information.
    */
   public void undo(Transaction tx) {}

   public List<Integer> txNumbers() {
      return new ArrayList<Integer>(txnums);
   }

   public String toString() {
      String str = "<NQCKPT ";
      for (Integer txnum : txnums)
         str += txnum + " ";
      return str + ">";
   }

   /**
    * A static method to write a non-quiescent checkpoint record to the log.
    */
   public static int writeToLog(LogMgr lm, Collection<Integer> txnums) {
      byte[] rec = new byte[Integer.BYTES * (2 + txnums.size())];
      Page p = new Page(rec);
      p.setInt(0, NQCKPT);
      int countpos = Integer.BYTES;
      p.setInt(countpos, txnums.size());
      int txpos = countpos + Integer.BYTES;
      for (Integer txnum : txnums) {
         p.setInt(txpos, txnum);
         txpos += Integer.BYTES;
      }
      return lm.append(rec);
   }
}
