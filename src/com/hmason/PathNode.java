package com.hmason;

public class PathNode
{
   private int x;
   private int y;
   private boolean walkable;
   private int distanceFromStart;
   private int trueDistanceFromEnd;
   private int totalDistance;
   private PathNode parentNode;
   
   public int[] getPosition()
   {
      return new int[] {x, y};
   }
   
   public void setPosition(int x, int y)
   {
      this.x = x;
      this.y = y;
   }
   
   public boolean getWalkable()
   {
      return walkable;
   }
   
   public void setWalkable(boolean walkable)
   {
      this.walkable = walkable;
   }
   
   public int getDistanceFromStart()
   {
      return distanceFromStart;
   }
   
   public void setDistanceFromStart(int distance)
   {
      distanceFromStart = distance;
   }
   
   public int getTrueDistanceFromEnd()
   {
      return trueDistanceFromEnd;
   }
   
   public void setTrueDistanceFromEnd(int distance)
   {
      trueDistanceFromEnd = distance;
   }

   public int getTotalDistance()
   {
      return totalDistance;
   }
   
   public void setTotalDistance(int distance)
   {
      totalDistance = distance;
   }
   
   public PathNode getParentNode()
   {
      return parentNode;
   }
   
   public void setParentNode(PathNode node)
   {
      parentNode = node;
   }
   
   public PathNode(int x, int y) {
      this.x = x;
      this.y = y;
   }
}
