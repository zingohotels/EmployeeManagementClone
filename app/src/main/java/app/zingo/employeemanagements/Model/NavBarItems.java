package app.zingo.employeemanagements.Model;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public class NavBarItems {

    private String title;
    private int icon;

    public NavBarItems(String title, int icon)
    {
        this.title = title;
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }
}
