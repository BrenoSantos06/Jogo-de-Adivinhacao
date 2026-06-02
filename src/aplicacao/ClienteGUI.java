package aplicacao;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClienteGUI extends JFrame {

    private JTextField campoJogador;

    private JTextField campoPalpite;

    private JTextArea areaHistorico;

    private JLabel labelStatus;

    private JButton botaoEntrar;

    private JButton botaoEnviar;

    private aplicacao.Jogo jogo;

    private int jogador = 0;

    private JButton botaoReiniciar;

    public ClienteGUI() {

        configurarTela();

        conectarServidor();

        iniciarAtualizacao();
    }

    private void configurarTela() {

        setTitle("Acerte o Número");

        setSize(600, 500);


        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        JPanel painel = new JPanel();

        painel.setLayout(new BorderLayout());

        painel.setBackground(
                new Color(30, 30, 30)
        );

        // TOPO

        JPanel topo = new JPanel();

        topo.setLayout(new GridLayout(3, 2));

        topo.setBackground(
                new Color(30, 30, 30)
        );

        topo.add(criarLabel(
                "Jogador (1 ou 2):"
        ));

        campoJogador = new JTextField();

        topo.add(campoJogador);

        botaoEntrar =
                new JButton("ENTRAR");

        topo.add(botaoEntrar);

        topo.add(criarLabel(""));

        topo.add(criarLabel(
                "Palpite:"
        ));

        campoPalpite =
                new JTextField();

        campoPalpite.addKeyListener(
                new java.awt.event.KeyAdapter() {

                    public void keyTyped(
                            java.awt.event.KeyEvent e
                    ) {

                        char c = e.getKeyChar();

                        if (!Character.isDigit(c)) {

                            e.consume();
                        }
                    }
                }
        );

        campoPalpite.setEnabled(false);

        topo.add(campoPalpite);

        painel.add(topo,
                BorderLayout.NORTH);

        // CENTRO

        areaHistorico =
                new JTextArea();

        areaHistorico.setEditable(false);

        areaHistorico.setBackground(
                new Color(20, 20, 20)
        );

        areaHistorico.setForeground(
                Color.WHITE
        );

        JScrollPane scroll =
                new JScrollPane(
                        areaHistorico
                );

        painel.add(scroll,
                BorderLayout.CENTER);

        // BAIXO

        JPanel baixo =
                new JPanel();

        baixo.setBackground(
                new Color(30, 30, 30)
        );

        labelStatus =
                criarLabel("Conectado");

        botaoEnviar =
                new JButton("ENVIAR");

        botaoEnviar.setEnabled(false);

        botaoReiniciar =
                new JButton("JOGAR NOVAMENTE!");

        botaoReiniciar.setEnabled(false);

        baixo.add(labelStatus);

        baixo.add(botaoEnviar);

        baixo.add(botaoReiniciar);

        painel.add(baixo,
                BorderLayout.SOUTH);

        add(painel);

        // EVENTOS

        botaoEntrar.addActionListener(
                e -> entrar()
        );

        botaoEnviar.addActionListener(
                e -> enviar()
        );

        botaoReiniciar.addActionListener(
                e -> reiniciar()
        );

        setVisible(true);
    }

    private JLabel criarLabel(
            String texto
    ) {

        JLabel label =
                new JLabel(texto);

        label.setForeground(
                Color.WHITE
        );

        return label;
    }

    private void conectarServidor() {

        try {

            Registry registry =
                    LocateRegistry.getRegistry(
                            //use localhost para rodar apenas em 1 pc, ou o ip da rede para mais de um pc
                            "localhost"
                    );

            jogo =
                    (Jogo) registry.lookup(
                            "Jogo"
                    );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void entrar() {

        try {

            String texto =
                    campoJogador
                            .getText()
                            .trim();

            if (texto.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Digite 1 ou 2."
                );

                return;
            }

            int valor =
                    Integer.parseInt(texto);

            if (valor != 1 && valor != 2) {

                JOptionPane.showMessageDialog(
                        this,
                        "O jogador deve ser 1 ou 2."
                );

                return;
            }

            boolean conectado;

            try {

                conectado =
                        jogo.entrar(valor);

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Erro de conexão."
                );

                return;
            }

            if (!conectado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Esse jogador já está em uso."
                );

                return;
            }

            jogador = valor;

            campoJogador.setEnabled(false);

            botaoEntrar.setEnabled(false);

            labelStatus.setText(
                    "Jogador " + jogador
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite apenas números."
            );
        }
    }

    private void enviar() {

        try {

            String texto =
                    campoPalpite.getText().trim();

            if (texto.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Digite um número."
                );

                return;
            }

            int palpite;

            try {

                palpite =
                        Integer.parseInt(texto);

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Digite apenas números."
                );

                return;
            }

            if (palpite < 1 || palpite > 100) {

                JOptionPane.showMessageDialog(
                        this,
                        "Digite um número entre 1 e 100."
                );

                return;
            }

            String resposta =
                    jogo.jogar(
                            palpite,
                            jogador
                    );

            if (
                    resposta.equals(
                            "AGUARDANDO_JOGADORES"
                    )
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Aguardando outro jogador conectar."
                );
            }

            if (resposta.equals("VENCEU")) {

                JOptionPane.showMessageDialog(
                        this,
                        "VOCÊ VENCEU!"
                );

                botaoReiniciar.setEnabled(true);
            }

            if (resposta.equals("JOGADOR_INVALIDO")) {

                JOptionPane.showMessageDialog(
                        this,
                        "Jogador inválido."
                );
            }

            campoPalpite.setText("");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao enviar palpite."
            );
        }

    }

    private void iniciarAtualizacao() {

        Timer timer =
                new Timer(
                        1000,
                        e -> atualizar()
                );

        timer.start();
    }

    private void atualizar() {

        try {

            areaHistorico.setText(
                    jogo.obterHistorico()
            );

            if (!jogo.jogoPronto()) {

                labelStatus.setText(
                        "Aguardando jogadores..."
                );

                return;
            }

            if (jogo.jogoPronto()) {

                labelStatus.setText(
                        "Partida iniciada!"
                );
            }

            if (jogador != 0) {

                if (jogo.obterTurno()
                        == jogador
                        && !jogo.jogoEncerrado()) {

                    labelStatus.setText(
                            "SUA VEZ"
                    );

                    labelStatus.setForeground(
                            Color.GREEN
                    );

                    campoPalpite.setEnabled(true);

                    botaoEnviar.setEnabled(true);

                } else {

                    labelStatus.setText(
                            "Aguardando adversário"
                    );

                    labelStatus.setForeground(
                            Color.ORANGE
                    );

                    campoPalpite.setEnabled(false);

                    botaoEnviar.setEnabled(false);
                }

                if (jogo.jogoEncerrado()) {

                    labelStatus.setText(
                            "JOGO ENCERRADO"
                    );

                    campoPalpite.setEnabled(false);

                    botaoEnviar.setEnabled(false);

                    botaoReiniciar.setEnabled(true);
                }
            }

        } catch (Exception e) {

            labelStatus.setText(
                    "Erro de conexão"
            );
        }
    }

    private void reiniciar() {

        try {

            jogo.reiniciarJogo();

            JOptionPane.showMessageDialog(
                    this,
                    "Novo jogo iniciado!"
            );

            botaoReiniciar.setEnabled(false);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao reiniciar."
            );
        }
    }

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                ClienteGUI::new
        );
    }
}