
package servidorgamepong;

/**
 *
 * @author Orlando
 */
public class Cliente {
    private String IP;
    private int port;
    private String player;
    int barraX;
    int barraY;

    public Cliente(String IP, int port, String player) {
        this.IP = IP;
        this.port = port;
        this.player = player;
    }

    public int getBarraX() {
        return barraX;
    }

    public void setBarraX(int barraX) {
        this.barraX = barraX;
    }

    public int getBarraY() {
        return barraY;
    }

    public void setBarraY(int barraY) {
        this.barraY = barraY;
    }
    
    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
