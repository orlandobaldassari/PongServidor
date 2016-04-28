package servidorgamepong;

//import java.awt.event.KeyEvent;
import java.io.*;
import static java.lang.Thread.sleep;
import java.net.*;
//import java.security.NoSuchAlgorithmException;
//import java.sql.*;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.swing.text.DefaultCaret;

public class ServidorGamePong {

    private ServerSocket socket = null;
    int direcaoX = 0;
    int direcaoY = 0;
    int posBarra1;
    int posBarra2;
    int posBolaY;
    int posBolaX;
    int ponto = 0;
    int velocidadeBola = 15;
    int portaServidor = 5554;
    int largura = 790;
    int altura = 600;
    int pontuacaoA = 0;
    int pontuacaoB = 0;
    Cliente player1 = null;
    Cliente player2 = null;

    /**
     * @param bola
     * @param barra
     */
    private void verificaBola(int bolaX, int bolaY, int barraX, int barraY) {
        if (barraX != 13) {
            if ((bolaX + 40) >= (barraX)) {
                if ((((bolaY + 77) < barraY + 10) || (bolaY > barraY + 90))) {
                    if ((bolaX + 77 + velocidadeBola) >= (largura - velocidadeBola)) {
                        if (ponto == 0) {
                            ponto = 1;
                            pontuacaoB++;
                            if (velocidadeBola < 5) {
                                velocidadeBola++;
                            }
                        }
                    }
                } else {
                    direcaoX = 0;
                    if (bolaY + 77 < barraY + 30) {
                        direcaoY = 0;
                    }
                    if (bolaY > barraY + 70) {
                        direcaoY = 1;
                    }
                }
            }
        } else if (bolaX - velocidadeBola <= (barraX + 25)) {
            if ((((bolaY + 77) < barraY + 10) || (bolaY > barraY + 90))) {
                if (bolaX - velocidadeBola <= 10 + velocidadeBola) {
                    if (ponto == 0) {
                        ponto = 1;
                        pontuacaoA++;
                        if (velocidadeBola < 5) {
                            velocidadeBola++;
                        }
                    }
                }
            } else {
                direcaoX = 1;
                if (bolaY + 77 < barraY + 30) {
                    direcaoY = 0;
                }
                if (bolaY > barraY + 70) {
                    direcaoY = 1;
                }
            }
        }
    }

    private void sendARequest(Map<String, Object> request, String IP, int PORT) {
        try (Socket s = new Socket(IP, PORT)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream())) {
                oos.writeObject(request);
            } catch (UnknownHostException ex) {
                Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, "HOST INVALIDO.", ex);
            } catch (IOException ex) {
                Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, "I/0 error.", ex);
            }
            s.close();
        } catch (ConnectException ex) {
            //
        } catch (IOException ex) {
            Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void StartServer() {
        Thread envioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(50);
                    } catch (InterruptedException erro) {
                        System.out.println("ERRO: " + erro);
                    }
                    if (player1 != null && player2 != null) {
                        //verificações as colisões da bola com parede
                        if ((direcaoX == 0) && (posBolaX > 10)) {
                            posBolaX -= velocidadeBola;
                        } else {
                            direcaoX = 1;
                        }
                        if ((direcaoX == 1) && (posBolaX < (largura - 60))) {
                            posBolaX += velocidadeBola;
                        } else {
                            direcaoX = 0;
                        }
                        if ((direcaoY == 0) && (posBolaY > 10)) {
                            posBolaY -= velocidadeBola;
                        } else {
                            direcaoY = 1;
                        }
                        if ((direcaoY == 1) && (posBolaY < (altura - 85))) {
                            posBolaY += velocidadeBola;
                        } else {
                            direcaoY = 0;
                        }
                        if (posBolaX > 100 && posBolaX < 600) {
                            ponto = 0;
                        }
                        /**
                         * Verifica se a bola colidiu na barra ou parede..
                         */
                        if (posBolaX >= ((largura / 4))) {
                            verificaBola(posBolaX, posBolaY, player1.barraX, player1.barraY);
                        } else if (posBolaX <= ((largura / 4))) {
                            verificaBola(posBolaX, posBolaY, player2.barraX, player2.barraY);
                        } else {
                            ponto = 0;
                        }
                        if (player1 != null) {
                            Map<String, Object> requisicaoBola = new LinkedHashMap<>();
                            requisicaoBola.put("acao", "moverBola");
                            requisicaoBola.put("bolaX", String.valueOf(posBolaX));
                            requisicaoBola.put("bolaY", String.valueOf(posBolaY));
                            requisicaoBola.put("pontuacaoA", String.valueOf(pontuacaoA));
                            requisicaoBola.put("pontuacaoB", String.valueOf(pontuacaoB));
                            sendARequest(requisicaoBola, player1.getIP(), player1.getPort());
                        }
                        if (player2 != null) {
                            Map<String, Object> requisicaoBola = new LinkedHashMap<>();
                            requisicaoBola.put("acao", "moverBola");
                            requisicaoBola.put("bolaX", String.valueOf(posBolaX));
                            requisicaoBola.put("bolaY", String.valueOf(posBolaY));
                            requisicaoBola.put("pontuacaoA", String.valueOf(pontuacaoA));
                            requisicaoBola.put("pontuacaoB", String.valueOf(pontuacaoB));
                            sendARequest(requisicaoBola, player2.getIP(), player2.getPort());
                        }
                    }
                }
            }
        });
        envioThread.start();

        Thread recebimentoThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sleep(3);
                } catch (InterruptedException erro) {
                    System.out.println("ERRO: " + erro);
                }
                try (ServerSocket ss = new ServerSocket(portaServidor)) {
                    System.out.println("O servidor esta na porta " + String.valueOf(portaServidor) + "...");
                    socket = ss;
                    while (true) {
                        Socket sock = ss.accept();

                        try (ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {
                            Map<String, Object> request = (Map<String, Object>) ois.readObject();
                            String action = (String) request.get("action");
                            String player = (String) request.get("player");
                            String ip = String.valueOf(sock.getInetAddress()).replace("/", "");
                            int porta = Integer.parseInt((String) request.get("porta"));
                            int barraX = Integer.parseInt((String) request.get("barraX"));
                            int barraY = Integer.parseInt((String) request.get("barraY"));
                            if (player.equals("1")) {
                                if (player1 == null) {
                                    player1 = new Cliente(ip, porta, "1");
                                    System.out.println(ip + ":" + porta + " se conectou [player1]");
                                }
                            } else if (player2 == null) {
                                player2 = new Cliente(ip, porta, "2");
                                System.out.println(ip + ":" + porta + " se conectou [player2]");
                            }
                            switch (action) {
                                case "moverBarra":
                                    moverBarra(player, barraX, barraY);
                                    if (player1 != null && player2 != null) {
                                        if (player.equals("1")) {
                                            Map<String, Object> requisicao = new LinkedHashMap<>();
                                            requisicao.put("acao", "moverBarra");
                                            requisicao.put("player", String.valueOf(1));
                                            requisicao.put("posicaoY", String.valueOf(player1.barraY));
                                            sendARequest(requisicao, player2.getIP(), player2.getPort());
                                        } else if (player.equals("2")) {
                                            Map<String, Object> requisicao = new LinkedHashMap<>();
                                            requisicao.put("acao", "moverBarra");
                                            requisicao.put("player", String.valueOf(2));
                                            requisicao.put("posicaoY", String.valueOf(player2.barraY));
                                            sendARequest(requisicao, player1.getIP(), player1.getPort());
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                } catch (SocketException ex) {

                } catch (IOException ex) {
                    Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, "I/O Error.", ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, "Class not found error.", ex);
                }
            }

        });
        recebimentoThread.start();
    }

    public void moverBarra(String player, int barraX, int barraY) {
        if (player.equals("1")) {
            player1.setBarraX(barraX);
            player1.setBarraY(barraY);
        } else {
            player2.setBarraX(barraX);
            player2.setBarraY(barraY);
        }
    }

    public static void main(String args[]) {
        ServidorGamePong server = new ServidorGamePong();
        server.StartServer();
    }
}
