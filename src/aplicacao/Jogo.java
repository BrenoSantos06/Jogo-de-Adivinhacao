package aplicacao;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Jogo extends Remote {

    String jogar(int palpite, int jogador)
            throws RemoteException;

    String obterHistorico()
            throws RemoteException;

    int obterTurno()
            throws RemoteException;

    boolean jogoPronto()
            throws RemoteException;

    boolean jogoEncerrado()
            throws RemoteException;

    boolean entrar(int jogador)
            throws RemoteException;

    void reiniciarJogo()
            throws RemoteException;
}
