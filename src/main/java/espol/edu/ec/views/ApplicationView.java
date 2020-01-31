package espol.edu.ec.views;

import espol.edu.ec.models.WebApplication;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.Objects;


public class ApplicationView implements Comparable<ApplicationView>{
    private WebApplication application;
    private ImageView icon;
    private Text name;
    private ProgressBar pb;
    private ProgressIndicator pi;
    private int count;

    public ApplicationView(WebApplication application){
        this.application = application;
        pb = new ProgressBar(0);
        pi = new ProgressIndicator(0);
        this.count = 0;
        init();
    }

    private void init() {
        icon = new ImageView(application.getImage());
        name = new Text(application.getHostName());;
    }

    public void setPercentage(int count, int size){
        this.count = count;
        double value = ((double)count)/((double)size);
        pb.setProgress(value);
        pi.setProgress(value);
    }

    public int getCount(){
        return count;
    }

    public WebApplication getApplication() {
        return application;
    }

    public ImageView getIcon() {
        return icon;
    }

    public Text getName() {
        return name;
    }

    public ProgressBar getPb() {
        return pb;
    }

    public ProgressIndicator getPi() {
        return pi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationView)) return false;
        ApplicationView that = (ApplicationView) o;
        return application.equals(that.application);
    }

    @Override
    public int hashCode() {
        return Objects.hash(application);
    }

    @Override
    public int compareTo(ApplicationView o) {
        return (int)((o.pb.getProgress() - this.pb.getProgress()) * 100);
    }
}
