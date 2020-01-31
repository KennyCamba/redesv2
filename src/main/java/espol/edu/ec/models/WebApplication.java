package espol.edu.ec.models;

import espol.edu.ec.packetsniffer.Const;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

public class WebApplication {
    private String hostName;
    private Image image;

    public WebApplication(InetAddress address){
            hostName = address.getHostName();
            image = new Image("https://www.google.com/s2/favicons?domain_url=" + hostName);
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebApplication)) return false;
        WebApplication that = (WebApplication) o;
        return hostName.equals(that.hostName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostName);
    }
}
