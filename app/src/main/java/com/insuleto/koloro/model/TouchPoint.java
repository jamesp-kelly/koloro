package com.insuleto.koloro.model;

public class TouchPoint {
  private int x;
  private int y;

  public TouchPoint(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public long distanceFrom(TouchPoint otherPoint) {
    int deltaX = otherPoint.x - this.x;
    int deltaY = otherPoint.y - this.y;

    return Math.round(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
  }
}
