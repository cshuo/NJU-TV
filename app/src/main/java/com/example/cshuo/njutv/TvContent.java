package com.example.cshuo.njutv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cshuo on 16/8/13.
 */
public class TvContent {

    public List<TvItem> items = new ArrayList<TvItem>();

    public void addItem(TvItem item){
        this.items.add(item);
    }

    public List<TvItem> getItems(){
        return this.items;
    }

    public static class TvItem {
        public String url;
        public String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public TvItem(String name, String url){
            this.name = name;
            this.url = url;
        }
    }
}
