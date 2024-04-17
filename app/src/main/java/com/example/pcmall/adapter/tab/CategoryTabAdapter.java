package com.example.pcmall.adapter.tab;

import com.example.pcmall.model.Category;

import java.util.List;

import q.rorbin.verticaltablayout.adapter.SimpleTabAdapter;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class CategoryTabAdapter extends SimpleTabAdapter {
    private List<Category> categoryList;

    public CategoryTabAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public TabView.TabTitle getTitle(int position) {
        //自定义Tab选择器的字体大小颜色
        Category category = categoryList.get(position);
        return new QTabView.TabTitle.Builder()
                .setTextColor(0xFF2A2323, 0xFF2A2323)
                .setTextSize(15)
                .setContent(category.getCname())
                .build();
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }
}
