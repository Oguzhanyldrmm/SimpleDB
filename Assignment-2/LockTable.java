package simpledb.tx.concurrency;

import java.util.*;
import simpledb.file.BlockId;

/**
 * The lock table, which provides methods to lock and unlock blocks.
 * Positive transaction ids denote shared locks; negative ids denote
 * exclusive locks.
 */
class LockTable {
   private Map<BlockId,List<Integer>> locks = new HashMap<BlockId,List<Integer>>();

   /**
    * Grant an SLock on the specified block.
    * Under wait-die, a younger transaction aborts if it needs to wait
    * for an older conflicting transaction.
    * @param blk a reference to the disk block
    * @param txnum the id of the requesting transaction
    */
   synchronized void sLock(BlockId blk, int txnum) {
      try {
         while (hasConflictingXLock(blk, txnum)) {
            if (hasOlderConflictingTx(blk, txnum))
               throw new LockAbortException();
            wait();
         }
         addSLock(blk, txnum);
      }
      catch(InterruptedException e) {
         throw new LockAbortException();
      }
   }

   /**
    * Grant an XLock on the specified block.
    * Under wait-die, a younger transaction aborts if it needs to wait
    * for an older conflicting transaction.
    * @param blk a reference to the disk block
    * @param txnum the id of the requesting transaction
    */
   synchronized void xLock(BlockId blk, int txnum) {
      try {
         while (hasConflictingLock(blk, txnum)) {
            if (hasOlderConflictingTx(blk, txnum))
               throw new LockAbortException();
            wait();
         }
         grantXLock(blk, txnum);
      }
      catch(InterruptedException e) {
         throw new LockAbortException();
      }
   }

   /**
    * Release this transaction's lock on the specified block.
    * @param blk a reference to the disk block
    * @param txnum the id of the transaction releasing the lock
    */
   synchronized void unlock(BlockId blk, int txnum) {
      List<Integer> txlist = locks.get(blk);
      if (txlist == null)
         return;

      txlist.remove(Integer.valueOf(txnum));
      txlist.remove(Integer.valueOf(-txnum));
      if (txlist.isEmpty())
         locks.remove(blk);
      notifyAll();
   }

   private boolean hasConflictingXLock(BlockId blk, int txnum) {
      List<Integer> txlist = locks.get(blk);
      if (txlist == null)
         return false;
      for (Integer locktx : txlist)
         if (locktx < 0 && Math.abs(locktx) != txnum)
            return true;
      return false;
   }

   private boolean hasConflictingLock(BlockId blk, int txnum) {
      List<Integer> txlist = locks.get(blk);
      if (txlist == null)
         return false;
      for (Integer locktx : txlist)
         if (Math.abs(locktx) != txnum)
            return true;
      return false;
   }

   private boolean hasOlderConflictingTx(BlockId blk, int txnum) {
      List<Integer> txlist = locks.get(blk);
      if (txlist == null)
         return false;
      for (Integer locktx : txlist) {
         int holder = Math.abs(locktx);
         if (holder != txnum && holder < txnum)
            return true;
      }
      return false;
   }

   private void addSLock(BlockId blk, int txnum) {
      List<Integer> txlist = locks.get(blk);
      if (txlist == null) {
         txlist = new ArrayList<Integer>();
         locks.put(blk, txlist);
      }
      if (!txlist.contains(Integer.valueOf(txnum)) &&
          !txlist.contains(Integer.valueOf(-txnum)))
         txlist.add(Integer.valueOf(txnum));
   }

   private void grantXLock(BlockId blk, int txnum) {
      List<Integer> txlist = new ArrayList<Integer>();
      txlist.add(Integer.valueOf(-txnum));
      locks.put(blk, txlist);
   }
}
