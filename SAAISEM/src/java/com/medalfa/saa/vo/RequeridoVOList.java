/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.medalfa.saa.vo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author HP-MEDALFA
 */
public class RequeridoVOList {

    public List<RequeridoVO> list;

    public RequeridoVOList() {
        list = new ArrayList<>();
    }

    public List<RequeridoVO> getList() {
        return list;
    }

    public void setList(List<RequeridoVO> list) {
        this.list = list;
    }

    public void add(RequeridoVO element) {
        this.list.add(element);
    }

    public void add(List<RequeridoVO> elements) {
        this.list.addAll(elements);
    }

//    public List<RequeridoVO> sort() {
//        Collections.sort(list);
//        return this.list;
//    }
}
