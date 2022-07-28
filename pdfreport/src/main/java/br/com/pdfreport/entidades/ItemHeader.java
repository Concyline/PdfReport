package br.com.pdfreport.entidades;

public class ItemHeader {

    private String subtitle;
    private String title;

    public ItemHeader(String subtitle, String title) {
        this.subtitle = subtitle;
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
