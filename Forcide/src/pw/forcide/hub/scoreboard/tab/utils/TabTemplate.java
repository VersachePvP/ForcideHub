package pw.forcide.hub.scoreboard.tab.utils;

import java.util.ArrayList;
import java.util.List;

public class TabTemplate
{
    private List<String> left;
    private List<String> middle;
    private List<String> right;
    private List<String> farRight;
    
    public TabTemplate() {
        this.left = new ArrayList<String>();
        this.middle = new ArrayList<String>();
        this.right = new ArrayList<String>();
        this.farRight = new ArrayList<String>();
    }
    
    public TabTemplate farRight(final String s) {
        return this.farRight(this.farRight.size(), s);
    }
    
    public TabTemplate farRight(final int index, final String s) {
        if (index > this.farRight.size()) {
            for (int i = this.farRight.size(); i < index; ++i) {
                this.farRight.add("");
            }
        }
        this.farRight.add(index, s);
        return this;
    }
    
    public TabTemplate left(final String s) {
        return this.left(this.left.size(), s);
    }
    
    public TabTemplate middle(final String s) {
        return this.middle(this.middle.size(), s);
    }
    
    public TabTemplate right(final String s) {
        return this.right(this.right.size(), s);
    }
    
    public TabTemplate left(final int index, final String s) {
        if (index > this.left.size()) {
            for (int i = this.left.size(); i < index; ++i) {
                this.left.add("");
            }
        }
        this.left.add(index, s);
        return this;
    }
    
    public TabTemplate middle(final int index, final String string) {
        if (index > this.middle.size()) {
            for (int i = this.middle.size(); i < index; ++i) {
                this.middle.add("");
            }
        }
        this.middle.add(index, string);
        return this;
    }
    
    public TabTemplate right(final int index, final String string) {
        if (index > this.right.size()) {
            for (int i = this.right.size(); i < index; ++i) {
                this.right.add("");
            }
        }
        this.right.add(index, string);
        return this;
    }
    
    public List<String> getLeft() {
        return this.left;
    }
    
    public List<String> getMiddle() {
        return this.middle;
    }
    
    public List<String> getRight() {
        return this.right;
    }
    
    public List<String> getFarRight() {
        return this.farRight;
    }
}
