package com.kerwin.ac.interval;

/**
 * 区间接口
 */
public interface Intervalable extends Comparable
{
    /**
     * 起点
     * @return
     */
    int getStart();

    /**
     * 终点
     * @return
     */
    int getEnd();

    /**
     * 长度
     * @return
     */
    int size();

}
