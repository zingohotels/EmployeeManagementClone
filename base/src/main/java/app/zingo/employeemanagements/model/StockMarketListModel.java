package app.zingo.employeemanagements.model;

public class StockMarketListModel {
    private int icon;
    private String title;

    private boolean isGroupHeader = false;

    public StockMarketListModel(String title) {
        this(-1,title);
        isGroupHeader = true;
    }
    public StockMarketListModel(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isGroupHeader() {
        return isGroupHeader;
    }

    public void setGroupHeader(boolean groupHeader) {
        isGroupHeader = groupHeader;
    }
}