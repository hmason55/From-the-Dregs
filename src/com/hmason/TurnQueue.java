package com.hmason;
import java.util.ArrayList;
import java.util.Collections;

public class TurnQueue implements Runnable
{
   private Thread thread;
   private ArrayList<Turn> turns;

   public TurnQueue()
   {
      turns = new ArrayList<Turn>();
   }

   // Initialize the queue and start a thread
   public void start()
   {
      if (turns == null)
      {
         return;
      }

      if (turns.size() > 0)
      {
         turns.get(0).getUnit().beginTurn();
      }

      if (thread == null)
      {
         thread = new Thread(this);
         thread.start();
      }
   }
   
   // Add a turn to the queue
   public void addTurn(Turn t)
   {
      turns.add(t);
   }

   // Remove all turn instances of Unit u from the queue
   public void removeTurns(Unit u)
   {
      if (turns == null)
      {
         return;
      }

      if (turns.size() > 0)
      {
         for (int i = turns.size() - 1; i >= 0; i--)
         {
            Turn turn = turns.get(i);
            if (turn != null)
            {
               if(turn.getUnit() != null) {
                  if (turn.getUnit().equals(u))
                  {
                     System.out.println("removing " + turn.toString());
                     turns.get(i).setUnit(null);
                  }
               }
            }
         }
      }
   }

   // Clean up the queue and move it forward
   public void nextTurn()
   {
      if (turns.size() > 0)
      {
         for (int i = turns.size() - 1; i >= 0; i--)
         {
            Turn turn = turns.get(i);
            
            // Remove any null instances of turns from the queue
            if (turn == null)
            {
               System.out.println("removing null turn at index " + i);
               turns.remove(i);
            } else if (turn.getUnit() == null)
            {
               System.out.println("removing turn with null unit at index " + i);
               turns.remove(i);
            } else
            {
               // Older turns will have decreased priority and will eventually be moved to the front of the queue
               turn.decreasePriority();
            }
         }

         sortTurns();
         if (turns.size() > 0)
         {
            turns.get(0).getUnit().beginTurn();
         }
      }
   }

   // Remove the first turn in the queue because it will be completed
   public void endTurn()
   {
      if (turns != null)
      {
         if (turns.size() > 0)
         {
            turns.remove(0);
         }
      }
   }

   // Sorts turns
   private void sortTurns()
   {
      // Insertion sort based on turn priority (ascending)
      if (turns == null)
      {
         return;
      } else if (turns.size() < 2)
      {
         return;
      }

      int i = 1;
      while (i < turns.size())
      {
         int j = i;

         while (j > 0 && turns.get(j - 1).getPriority() > turns.get(j).getPriority())
         {
            Collections.swap(turns, j, j - 1);
            j--;
         }
         i++;
      }
   }

   @Override
   public void run()
   {
      try
      {
         while (turns.size() > 0)
         {
            if(!Viewport.paused)
            {
               if (turns.get(0) == null)
               {
                  nextTurn();
               } else if (turns.get(0).getUnit() == null)
               {
                  nextTurn();
               } else if (!turns.get(0).getUnit().isMyTurn())
               {
                  nextTurn();
               } else
               {
                  Thread.sleep(50);
                  
                  Unit u = turns.get(0).getUnit();
                  if(u != null)
                  {
                     if(u.isPlayerControlled())
                     {
                        // Wait for player input
                     } else {
                        // Let the ai decide
                        u.aiTurn();
                     }
                  }
               }
            }
            Thread.sleep(50);
         }
      } catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }
   
   public void clearTurns()
   {
      turns.clear();
   }
   
   public String getTurnName()
   {
      if(turns == null)
      {
         return "";
      }
      
      if(turns.size() > 0 && turns.get(0) != null)
      {
         if(turns.get(0).getUnit() != null)
         {
            return turns.get(0).getUnit().getName() + "'s turn";
         }
      }
      return "";
   }
}
