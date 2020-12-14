
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Usuario extends UnicastRemoteObject implements UsuarioRemoto {
	
	private static final long serialVersionUID = 1L;
	protected String nome;
	private Registry registro;
	private ServidorRemoto server;
	private Home janela;
	private String host, nickname;
	private int porta;
	
	public Usuario(Home janela, String ip, int port, String nickname) throws RemoteException{
		this.janela = janela;
		
		host = ip;
		porta = port;
		this.nickname = nickname;
		
		try {
			registro = LocateRegistry.getRegistry(porta);
			server = (ServidorRemoto)registro.lookup("//"+host+":"+porta+"/Servidor");
		}
		catch (Exception e) {
			Notificacao.servidorNaoDisponivel();
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public boolean solicitaConexao(){
		
		try {
			nome = this.nickname;
			if(nome!=null) {
				int resposta = server.conectaUsuario(this);
				janela.setMensagemLog("Codigo do retorno: "+resposta);
				if(resposta==-1) {
					Notificacao.usuarioExiste(nome);
				}
				else {
					janela.setMensagemLog("Connectado");
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean enviaMensagem(String nome, String conteudoMsg, boolean tipoFila) {
		
		try {
			if(tipoFila) {
				if(!server.produzMensagemFila(nome, conteudoMsg)) {
					Notificacao.naoExisteUsuario();
					return false;
				}
			}
			else {
				if(!server.produzMensagemTopico(nome, conteudoMsg)) {
					Notificacao.naoExisteTopico();
					return false;
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public ArrayList<String> recebeMensagem(String nome, boolean tipoFila) {
		
		try {
			if(tipoFila) {
				return server.recebeMensagemFila(nome);
			}
			else {
				if(!server.assinaTopico(nome, this.nome)) {
					Notificacao.topicoJaAssinado();
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public void notificaMensagem() throws RemoteException {
		janela.recebeMensagensUsuarios();
	}
	
	public void notificaDesconexao() throws RemoteException {
		Notificacao.desconectado();
		System.exit(0);
	}
	
	public void setMensagemTopico(String mensagem) throws RemoteException {
		janela.escreveMensagensTopico(mensagem);
	}
	
	public ArrayList<String> getUsuarios() {
		try {
			return server.getUsuariosOnline();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public ArrayList<String> getTopicos() {
		try {
			return server.getTopicosDisponiveis();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public String getNome() throws RemoteException {
		return nome;
	}
}
