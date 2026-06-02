package aplicacao;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {

    public static void main(String[] args) throws RemoteException {
        aplicacao.JogoImpl jogo = new JogoImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("Jogo", jogo);
        System.out.println("Servidor RMI pronto!");
    }
}