package pepperweather.pepperweather;


public class Item {

    public String day;
    public String icon;
    public String maxMin;

    public Item(String day,String icon,String maxMin)
    {
        this.day=day;
        this.icon=icon;
        this.maxMin=maxMin;
    }
    public String getDay()
    {
        return day;
    }
    public String getIcon()
    {
        return icon;
    }
    public String getMaxMin()
    {
        return maxMin;
    }
}
