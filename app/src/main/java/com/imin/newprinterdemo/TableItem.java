package com.imin.newprinterdemo;

public class TableItem {
    String[] colsTextArr ;
    int[] colsWidthArr ;
    int[] colsAlignArr ;

    public TableItem(String[] colsTextArr, int[] colsWidthArr, int[] colsAlignArr, int[] colsSizeArr) {
        this.colsTextArr = colsTextArr;
        this.colsWidthArr = colsWidthArr;
        this.colsAlignArr = colsAlignArr;
        this.colsSizeArr = colsSizeArr;
    }

    public String[] getColsTextArr() {
        return colsTextArr;
    }

    public void setColsTextArr(String[] colsTextArr) {
        this.colsTextArr = colsTextArr;
    }

    public int[] getColsWidthArr() {
        return colsWidthArr;
    }

    public void setColsWidthArr(int[] colsWidthArr) {
        this.colsWidthArr = colsWidthArr;
    }

    public int[] getColsAlignArr() {
        return colsAlignArr;
    }

    public void setColsAlignArr(int[] colsAlignArr) {
        this.colsAlignArr = colsAlignArr;
    }

    public int[] getColsSizeArr() {
        return colsSizeArr;
    }

    public void setColsSizeArr(int[] colsSizeArr) {
        this.colsSizeArr = colsSizeArr;
    }

    int[] colsSizeArr ;
}
