package servidorgamepong;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.*;
import static java.lang.Thread.sleep;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.text.DefaultCaret;
import lib.ReadFile;

//Teste de comunicação github.

public class ServidorGamePong extends javax.swing.JFrame {

    private ServerSocket socket = null;
    private Image onImage;
    private Image offImage;

    int posBarra1;
    int posBarra2;
    int direcaoX = 0;
    int direcaoY = 0;
    int posBolaY;
    int posBolaX;
    int ponto = 0;
    int pontuacaoA = 0;
    int pontuacaoB = 0;
    int velocidadeBola = 15;
    int portaServidor = 5554;
    int largura = 790;
    int altura = 600;
    Cliente player1 = null;
    Cliente player2 = null;

    public ServidorGamePong() throws SQLException, NoSuchAlgorithmException {
        initComponents();
    }

    private String getDiretorio() {
        String caminho = "";
        try {
            caminho = ReadFile.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            caminho = caminho.substring(1, caminho.lastIndexOf('/'));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return caminho;
    }

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

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jInput1 = new javax.swing.JTextField(20);
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jTextPane1.setEditable(false);

       
        try {
            this.onImage = new ImageIcon(ImageIO.read(new File(this.getDiretorio() + "/img/server_on.png"))).getImage();
            this.offImage = new ImageIcon(ImageIO.read(new File(this.getDiretorio() + "/img/server_off.png"))).getImage();
        } catch (IOException ex) {
            Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, null, ex);
        }
        setIconImage(offImage);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }

        });

        jButton1.setText("Start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    jButton1ActionPerformed(evt);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, null, ex);
                }
                setIconImage(onImage);
            }

        });

        jButton2.setText("Stop");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
                setIconImage(offImage);
            }

        });
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addComponent(jButton2)
                                        .addGap(0, 226, Short.MAX_VALUE)))
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton1)
                                .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                        .addContainerGap()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addContainerGap())
        );
        pack();
    }

    private void setTextOnInput(String text) {
        jTextPane1.setText(jTextPane1.getText() + text + "\n");
        DefaultCaret caret = (DefaultCaret) jTextPane1.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) throws FileNotFoundException {
        Thread envioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(50);
                    } catch (InterruptedException erro) {
                        System.out.println("ERRO AO COLOCAR DELAY: " + erro);
                    }
                    if (player1 != null && player2 != null) {
                        //Todas as verificações para identificar as colisões com da bola com parede
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
                         * Verificando se a bola colidiu com a barra ou parede.
                         * Se foi com a parede deve-se aumentar a pontuação do
                         * Player Adversário.
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
                    System.out.println("ERRO AO COLOCAR DELAY: " + erro);
                }
                try (ServerSocket ss = new ServerSocket(portaServidor)) {
                    setTextOnInput("[SERVIDOR] servidor rodando na porta " + String.valueOf(portaServidor) + "...");
                    jButton1.setEnabled(false);
                    jButton2.setEnabled(true);
                    socket = ss;
                    while (true) {
                        Socket sock = ss.accept();
                        //RECEBE MENSAGEM DO CLIENTES
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
                                    jTextPane1.setText(
                                            jTextPane1.getText() + "\n"
                                            + ip + ":" + porta + " se conectou [player1]"
                                    );
                                }
                            } else if (player2 == null) {
                                player2 = new Cliente(ip, porta, "2");
                                jTextPane1.setText(
                                        jTextPane1.getText() + "\n"
                                        + ip + ":" + porta + " se conectou [player2]"
                                );
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

    public void responseToOtherClient(String player) {

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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        jTextPane1.setText(null);
        jButton1.setEnabled(true);
        jButton2.setEnabled(false);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, "I/O Error.", ex);
            }
        }
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, "I/O Error.", ex);
            }
        }
    }

    public static void main(String args[]) {
    

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    new ServidorGamePong().setVisible(true);
                } catch (SQLException | NoSuchAlgorithmException ex) {
                    Logger.getLogger(ServidorGamePong.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
    }
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JTextField jInput1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
}
