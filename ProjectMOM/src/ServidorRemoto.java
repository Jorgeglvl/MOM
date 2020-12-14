import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServidorRemoto extends Remote{
	
	public int conectaUsuario(UsuarioRemoto usuario) throws RemoteException;
	public boolean produzMensagemFila(String nomeFila, String conteudoMsg) throws RemoteException;
	public boolean produzMensagemTopico(String nomeTopico, String conteudoMsg) throws RemoteException;
	public ArrayList<String> recebeMensagemFila(String nomeFila) throws RemoteException;
	public boolean assinaTopico(String nomeTopico, String nomeUsuario) throws RemoteException;
	public ArrayList<String> getUsuariosOnline() throws RemoteException;
	public ArrayList<String> getTopicosDisponiveis() throws RemoteException;
}