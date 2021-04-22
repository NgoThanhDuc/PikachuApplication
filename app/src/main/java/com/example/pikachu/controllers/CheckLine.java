package com.example.pikachu.controllers;

import android.graphics.Point;

import com.example.pikachu.models.PointLine;

import static com.example.pikachu.controllers.GraphicView.CardMatrix;

public class CheckLine {

    private boolean checkLineX(int y1, int y2, int x) {
        // find point have column max and min
        int min = Math.min(y1, y2);
        int max = Math.max(y1, y2);
        // run column
        for (int y = min + 1; y < max; y++) {
            if (CardMatrix[x][y] != -1) { // if see barrier then die
                return false;
            }
        }
        // not die -> success
        return true;
    }

    private boolean checkLineY(int x1, int x2, int y) {
        int min = Math.min(x1, x2);
        int max = Math.max(x1, x2);
        for (int x = min + 1; x < max; x++) {
            if (CardMatrix[x][y] != -1) {
                return false;
            }
        }
        return true;
    }

    // check in rectangle
    private boolean checkRectX(Point p1, Point p2) {
        // find point have y min and max
        Point pMinY = p1, pMaxY = p2;
        if (p1.y > p2.y) {
            pMinY = p2;
            pMaxY = p1;
        }
        for (int y = pMinY.y; y <= pMaxY.y; y++) {
            if (y > pMinY.y && CardMatrix[pMinY.x][y] != -1) {
                return false;
            }
            // check two line
            if ((CardMatrix[pMaxY.x][y] == -1)
                    && checkLineY(pMinY.x, pMaxY.x, y)
                    && checkLineX(y, pMaxY.y, pMaxY.x)) {

                // if three line is true return column y
                return true;
            }
        }
        // have a line in three line not true then return -1
        return false;
    }

    private boolean checkRectY(Point p1, Point p2) {
        // find point have y min
        Point pMinX = p1, pMaxX = p2;
        if (p1.x > p2.x) {
            pMinX = p2;
            pMaxX = p1;
        }
        // find line and y begin
        for (int x = pMinX.x; x <= pMaxX.x; x++) {
            if (x > pMinX.x && CardMatrix[x][pMinX.y] != -1) {
                return false;
            }
            if ((CardMatrix[x][pMaxX.y] == -1)
                    && checkLineX(pMinX.y, pMaxX.y, x)
                    && checkLineY(x, pMaxX.x, pMaxX.y)) {

                return true;
            }
        }
        return false;
    }

    private boolean checkMoreLineX(Point p1, Point p2, int type) {
        // find point have y min
        Point pMinY = p1, pMaxY = p2;
        if (p1.y > p2.y) {
            pMinY = p2;
            pMaxY = p1;
        }
        // find line and y begin
        int y = pMaxY.y + type;
        int row = pMinY.x;
        int colFinish = pMaxY.y;
        if (type == -1) {
            colFinish = pMinY.y;
            y = pMinY.y + type;
            row = pMaxY.x;
        }

        // find column finish of line
        // check more
        if ((CardMatrix[row][colFinish] == -1 || pMinY.y == pMaxY.y)
                && checkLineX(pMinY.y, pMaxY.y, row)) {
            while (CardMatrix[pMinY.x][y] == -1
                    && CardMatrix[pMaxY.x][y] == -1) {
                if (checkLineY(pMinY.x, pMaxY.x, y)) {

                    return true;
                }
                y += type;
            }
        }
        return false;
    }

    private boolean checkMoreLineY(Point p1, Point p2, int type) {
        Point pMinX = p1, pMaxX = p2;
        if (p1.x > p2.x) {
            pMinX = p2;
            pMaxX = p1;
        }
        int x = pMaxX.x + type;
        int col = pMinX.y;
        int rowFinish = pMaxX.x;
        if (type == -1) {
            rowFinish = pMinX.x;
            x = pMinX.x + type;
            col = pMaxX.y;
        }
        if ((CardMatrix[rowFinish][col] == -1 || pMinX.x == pMaxX.x)
                && checkLineY(pMinX.x, pMaxX.x, col)) {
            while (CardMatrix[x][pMinX.y] == -1
                    && CardMatrix[x][pMaxX.y] == -1) {
                if (checkLineX(pMinX.y, pMaxX.y, x)) {

                    return true;
                }
                x += type;
            }
        }
        return false;
    }

    public PointLine checkTwoPoint(Point p1, Point p2) {
        if (!p1.equals(p2) && CardMatrix[p1.x][p1.y] == CardMatrix[p2.x][p2.y]) {
            // check line with x
            if (p1.x == p2.x) {
                if (checkLineX(p1.y, p2.y, p1.x)) {
                    return new PointLine(p1, p2);
                }
            }
            // check line with y
            if (p1.y == p2.y) {
                if (checkLineY(p1.x, p2.x, p1.y)) {
                    return new PointLine(p1, p2);
                }
            }
            // check in rectangle with x
            if (checkRectX(p1, p2)) {
                return new PointLine(p1, p2);
            }
            // check in rectangle with y
            if (checkRectY(p1, p2)) {
                return new PointLine(p1, p2);
            }
            // check more right
            if (checkMoreLineX(p1, p2, 1)) {
                return new PointLine(p1, p2);
            }
            // check more left
            if (checkMoreLineX(p1, p2, -1)) {
                return new PointLine(p1, p2);
            }
            // check more down
            if (checkMoreLineY(p1, p2, 1)) {
                return new PointLine(p1, p2);
            }
            // check more up
            if (checkMoreLineY(p1, p2, -1)) {
                return new PointLine(p1, p2);
            }
        }
        return null;
    }
}