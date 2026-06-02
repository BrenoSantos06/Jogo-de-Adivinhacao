package aplicacao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class JogoImpl extends UnicastRemoteObject implements Jogo {

    private int numeroSecreto;

    private int turno;

    private boolean encerrado = false;

    private String historico = "";

    private boolean jogador1Conectado = false;

    private boolean jogador2Conectado = false;

    protected JogoImpl()
            throws RemoteException {

        historico =
                "Aguardando jogadores...\n";
    }

    private void iniciarJogo() {

        numeroSecreto =
                new Random().nextInt(100) + 1;

        turno =
                new Random().nextBoolean()
                        ? 1
                        : 2;

        encerrado = false;

        historico +=
                "\n\nJogo iniciado!";

        historico +=
                "\nJogador "
                        + turno
                        + " começa.";
    }

    @Override
    public synchronized String jogar(
            int palpite,
            int jogador
    ) throws RemoteException {

        if (!jogoPronto()) {

            return "AGUARDANDO_JOGADORES";
        }

        if (jogador != 1
                && jogador != 2) {

            return "JOGADOR_INVALIDO";
        }

        if (encerrado) {
            return "JOGO_ENCERRADO";
        }

        if (jogador != turno) {
            return "ESPERE";
        }

        historico +=
                "\nJogador "
                        + jogador
                        + " jogou "
                        + palpite;

        if (palpite == numeroSecreto) {

            encerrado = true;

            historico +=
                    "\n\nJogador "
                            + jogador
                            + " venceu!";

            return "VENCEU";
        }

        String resposta;

        if (palpite < numeroSecreto) {

            resposta = "MAIOR";

            historico +=
                    "\nO número é MAIOR";

        } else {

            resposta = "MENOR";

            historico +=
                    "\nO número é MENOR";
        }

        turno = (turno == 1) ? 2 : 1;

        return resposta;
    }

    @Override
    public synchronized boolean entrar(
            int jogador
    ) throws RemoteException {

        if (jogador == 1) {

            if (jogador1Conectado) {
                return false;
            }

            jogador1Conectado = true;

            historico +=
                    "\nJogador 1 conectado.";
        }

        else if (jogador == 2) {

            if (jogador2Conectado) {
                return false;
            }

            jogador2Conectado = true;

            historico +=
                    "\nJogador 2 conectado.";
        }

        else {
            return false;
        }

        if (jogador1Conectado
                && jogador2Conectado) {

            iniciarJogo();
        }

        return true;
    }

    @Override
    public synchronized String obterHistorico()
            throws RemoteException {

        return historico;
    }

    @Override
    public synchronized int obterTurno()
            throws RemoteException {

        return turno;
    }

    @Override
    public synchronized boolean jogoPronto()
            throws RemoteException {

        return jogador1Conectado
                && jogador2Conectado;
    }

    @Override
    public synchronized boolean jogoEncerrado()
            throws RemoteException {

        return encerrado;
    }

    @Override
    public synchronized void reiniciarJogo()
            throws RemoteException {

        numeroSecreto =
                new Random().nextInt(100) + 1;

        turno =
                new Random().nextBoolean()
                        ? 1
                        : 2;

        encerrado = false;

        historico =
                "Novo jogo iniciado!\n";

        historico +=
                "\nJogador "
                        + turno
                        + " começa.";
    }
}