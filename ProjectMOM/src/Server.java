import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

public class Server extends UnicastRemoteObject implements RemoteServer {

	private static final long serialVersionUID = 1L;
	private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private ActiveMQConnection conexao;
	private ArrayList<RemoteClient> listaUsuario = new ArrayList<RemoteClient>();
	private ArrayList<Assinante> listaAssinates = new ArrayList<Assinante>();
	private Registry registro;
	private ServerAdmin window;
	
	public Server(ServerAdmin window, String ip, int port) throws RemoteException {
		super();
		this.window = window;
		
		try {
			registro = LocateRegistry.createRegistry(port);
			registro.rebind("//"+ip+":"+port+"/Servidor",this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void connectBroker() {
		
		try {
			conexao = ActiveMQConnection.makeConnection(url);
			conexao.start();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void disconnectBroker() {
		
		try {
			conexao.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int conectaUsuario(RemoteClient usuario) throws RemoteException {
		
		String nome = usuario.getNome();
		boolean filaExiste = verificaFilaExiste(nome);
		int usuarioExiste = verificaUsuarioExiste(nome);
		
		if(!filaExiste&&usuarioExiste==-1) {
			criaFila(nome);
			criaUsuario(usuario);
			window.adicionaListaFila(nome);
			return 1;
		}
		else if(usuarioExiste==-1) {
			reconectaUsuario(usuario);
			window.setMensagemLog("Usu�rio '"+nome+"' reconectado");
			return 0;
		}
		else {
			window.setMensagemLog("Erro: Usu�rio '"+nome+"' j� existe");
			return -1;
		}
	}
	
	public int verificaUsuarioExiste(String nome) {
		
		int i = 0;
		
		if(!getListaUsuario().isEmpty()) {
			while(i<getListaUsuario().size()) {
				try {
					if(getListaUsuario().get(i).getNome().contentEquals(nome)) {
						return i;
					}
					i++;
				} catch (RemoteException e) {
					getListaUsuario().remove(i);
				}
			}
		}
		
		return -1;
	}
	
	public boolean verificaFilaExiste(String nomeFila) {
		
		ArrayList<String> nomeFilas = getFilas();
		int i;
		
		for(i=0;i<nomeFilas.size();i++) {
			if(nomeFila.contentEquals(nomeFilas.get(i))) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean verificaTopicoExiste(String nomeTopico) {
		
		ArrayList<String> nomeTopicos = getTopicos();
		int i;
		
		for(i=0;i<nomeTopicos.size();i++) {
			if(nomeTopico.contentEquals(nomeTopicos.get(i))) {
				return true;
			}
		}
		
		return false;
	}
	
	public void reconectaUsuario(RemoteClient usuario) {
		criaUsuario(usuario);
	}
	
	public void criaUsuario(RemoteClient usuario) {
		getListaUsuario().add(usuario);
	}
	
	public boolean criaFila(String nomeFila) {
		
		connectBroker();
		
		try {
			Session sessao = conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destino = sessao.createQueue(nomeFila);
			MessageProducer produtor = sessao.createProducer(destino);
			produtor.close();
			sessao.close();
		} catch (JMSException e) {
			e.printStackTrace();
			return false;
		}
		
		disconnectBroker();
		return true;
	}
	
	public boolean criaTopico(String nomeTopico) {
		
		connectBroker();
		
		try {
			Session sessao = conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destino = sessao.createTopic(nomeTopico);
			MessageProducer publicador = sessao.createProducer(destino);
			publicador.close();
			sessao.close();
		} catch (JMSException e) {
			e.printStackTrace();
			return false;
		}
		
		disconnectBroker();
		
		return true;
	}
	
	public void removeFila(String nomeFila) {
		
		connectBroker();
		
		try {
			conexao.destroyDestination(new ActiveMQQueue(nomeFila));
			int i = verificaUsuarioExiste(nomeFila);
			if(i!=-1) {
				getListaUsuario().get(i).notificaDesconexao();
			}
		} catch (JMSException|RemoteException e) {
			e.printStackTrace();
		}
		
		disconnectBroker();
	}
	
	public void removeTopico(String nomeTopico) {
		
		connectBroker();
		
		try {
			conexao.destroyDestination(new ActiveMQTopic(nomeTopico));
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		disconnectBroker();
	}
	
	public ArrayList<String> getFilas() {
		
		ArrayList<String> nomeFilas = new ArrayList<String>();
		
		connectBroker();
		
		try {
			Set<ActiveMQQueue> listaFila = conexao.getDestinationSource().getQueues();
			Thread.sleep(1000);
			Iterator<ActiveMQQueue> iterator = listaFila.iterator();
			
			while(iterator.hasNext()) {
				nomeFilas.add(iterator.next().getQueueName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		disconnectBroker();
		
		return nomeFilas;
	}
	
	public ArrayList<String> getUsuariosOnline() throws RemoteException {
		
		ArrayList<String> listaUsuarioOn = new ArrayList<String>();
		ArrayList<String> listaFilas = getFilas();
		String nome;
		
		for(int i=0;i<getListaUsuario().size();i++) {
			try {
				nome = getListaUsuario().get(i).getNome();
				if(listaFilas.contains(nome)) {
					listaUsuarioOn.add("->"+nome);
				}
			} catch (RemoteException e) {
				//Continua
			}
		}
		
		for(int i=0;i<listaFilas.size();i++) {
			nome = listaFilas.get(i);
			if(!listaUsuarioOn.contains("->"+nome)) {
				listaUsuarioOn.add("-"+nome);
			}
		}
		
		return listaUsuarioOn;
	}

	public int getQuantidadeMsg(String nomeFila) {
		
		int k = 0;
		
		connectBroker();
		
		try {
			QueueSession sessaoFila = conexao.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue fila = sessaoFila.createQueue(nomeFila);
			QueueBrowser browser = sessaoFila.createBrowser(fila);
			Enumeration<?> mensagensNaFila = browser.getEnumeration();
			
			while (mensagensNaFila.hasMoreElements()) {
				mensagensNaFila.nextElement();
				k++;
			}
			
			sessaoFila.close();
			browser.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		disconnectBroker();
		
		return k;
	}
	
	public ArrayList<String> getTopicos() {
		
		ArrayList<String> nomeTopicos = new ArrayList<String>();
		
		connectBroker();
		
		try {
			Set<ActiveMQTopic> listaTopico = conexao.getDestinationSource().getTopics();
			Thread.sleep(1000);
			Iterator<ActiveMQTopic> iterator = listaTopico.iterator();
			
			while(iterator.hasNext()) {
				nomeTopicos.add(iterator.next().getTopicName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		disconnectBroker();
		
		return nomeTopicos;
	}
	
	public ArrayList<String> getTopicosDisponiveis() throws RemoteException {
		
		return getTopicos();
	}
	
	public boolean produzMensagemFila(String nomeFila, String conteudoMsg) throws RemoteException {
		
		if(!verificaFilaExiste(nomeFila)) {
			return false;
		}
		
		int i;
		
		connectBroker();
		
		try {
			Session sessao = conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destino = sessao.createQueue(nomeFila);
			MessageProducer produtor = sessao.createProducer(destino);
			TextMessage mensagem = sessao.createTextMessage(conteudoMsg);
			produtor.send(mensagem);
			produtor.close();
			sessao.close();
		} catch (JMSException e) {
			e.printStackTrace();
			return false;
		}
		
		disconnectBroker();
		
		i = verificaUsuarioExiste(nomeFila);
		
		if(i != -1) {
			getListaUsuario().get(i).notificaMensagem();
		}
		else {
			window.incrementaQntMensagem(nomeFila);
		}
		
		return true;
	}
	
	public boolean produzMensagemTopico(String nomeTopico, String conteudoMsg) throws RemoteException {
		
		if(!verificaTopicoExiste(nomeTopico)) {
			return false;
		}
		
		connectBroker();
		
		try {
			Session sessao = conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destino = sessao.createTopic(nomeTopico);
			MessageProducer publicador = sessao.createProducer(destino);
			TextMessage mensagem = sessao.createTextMessage(conteudoMsg);
			publicador.send(mensagem);
			publicador.close();
			sessao.close();
		} catch (JMSException e) {
			e.printStackTrace();
			return false;
		}
		
		disconnectBroker();
		return true;
	}
	
	public ArrayList<String> recebeMensagemFila(String nomeFila) throws RemoteException {
		
		ArrayList<String> listaMensagem = new ArrayList<String>();
		
		connectBroker();
		
		try {
			Session sessao = conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue fila = sessao.createQueue(nomeFila);
			Destination destino = sessao.createQueue(nomeFila);
			MessageConsumer consumidor = sessao.createConsumer(destino);
			QueueBrowser browser = sessao.createBrowser(fila);
			Enumeration<?> mensagensNaFila = browser.getEnumeration();
			Message mensagem;
			
			while (mensagensNaFila.hasMoreElements()) {
				mensagem = consumidor.receive();
				
				if (mensagem instanceof TextMessage) {
					TextMessage mensagemTexto = (TextMessage) mensagem;
					String texto = mensagemTexto.getText();
					listaMensagem.add(texto);
				} else {
					listaMensagem.add(""+mensagem);
				}
				mensagensNaFila.nextElement();
			}
			
			consumidor.close();
			sessao.close();
		} catch (JMSException e) {
			e.printStackTrace();
			listaMensagem.add("<error>");
		}
		
		disconnectBroker();
		
		if(listaMensagem.size()>1) {
			window.iniciaValores();
		}
		
		return listaMensagem;
	}
	
	public boolean assinaTopico(String nomeTopico, String nomeUsuario) throws RemoteException {
		
		for(int i=0;i<listaAssinates.size();i++) {
			if(listaAssinates.get(i).getNomeUsuario().contentEquals(nomeUsuario)&&listaAssinates.get(i).getNomeTopico().contentEquals(nomeTopico)) {
				return false;
			}
		}
		listaAssinates.add(new Assinante(this, nomeTopico, nomeUsuario));
		return true;
	}

	public ArrayList<RemoteClient> getListaUsuario() {
		return listaUsuario;
	}

	public void setListaUsuario(ArrayList<RemoteClient> listaUsuario) {
		this.listaUsuario = listaUsuario;
	}
}