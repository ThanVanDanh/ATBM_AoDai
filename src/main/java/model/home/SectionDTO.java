package model.home;

import java.util.ArrayList;
import java.util.List;

public class SectionDTO {
    public String key;
    public String title;
    public List<TabDTO> tabs;
    public SectionDTO() {
        this.tabs = new ArrayList<>();
    }

    public SectionDTO(String key, String title) {
        this.key = key;
        this.title = title;
        this.tabs = new ArrayList<>();
    }

    public String getKey() { return key; }
    public String getTitle() { return title; }
    public List<TabDTO> getTabs() { return tabs; }

    public void setKey(String key) { this.key = key; }
    public void setTitle(String title) { this.title = title; }
    public void setTabs(List<TabDTO> tabs) { this.tabs = tabs; }

    @Override
    public String toString() {
        return "SectionDTO{" +
                "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", tabs=" + tabs.size() +
                '}';
    }
}
