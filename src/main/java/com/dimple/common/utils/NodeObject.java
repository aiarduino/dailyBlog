package com.dimple.common.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeObject{
    private String href;
    private String title;
    public NodeObject(String href,String title){
        this.href=href;
        this.title=title;
    }
}