package common;


import java.rmi.RemoteException;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;

import server.Server;

public class Assinante implements MessageListener{
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private Server server;
	protected String nomeUsuario;
	protected String nomeTopico;
	private ActiveMQConnection conexao;

	public Assinante(Server server, String nomeTopico, String nomeUsuario) {
		this.server = server;
		this.nomeUsuario = nomeUsuario;
		this.nomeTopico = nomeTopico;
		
		assina(nomeTopico);
	}
	
	private boolean assina(String nomeTopico) {
		
		conectaBroker();
		
		try {
			Session sessao = conexao.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destino = sessao.createTopic(nomeTopico);
			MessageConsumer assinante = sessao.createConsumer(destino);
			assinante.setMessageListener(this);
		} catch (JMSException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void onMessage(Message mensage) {
		if(mensage instanceof TextMessage){
			try{
				int i = server.verificaUsuarioExiste(nomeUsuario);
				String mensageSTR = ((TextMessage)mensage).getText();
				int indice1 = mensageSTR.indexOf("<")+1;
				int indice2 = mensageSTR.indexOf(":");
				String remetente = mensageSTR.substring(indice1, indice2);
				if(remetente.contentEquals("Servidor")) {
					server.getListaUsuario().get(i).setMensagemTopico(mensageSTR);
					desconectaBroker();
				}
				else if(!nomeUsuario.contentEquals(remetente)) {
					server.getListaUsuario().get(i).setMensagemTopico(mensageSTR);
				}
				else if(i==-1) {
					desconectaBroker();
				}
			}catch(JMSException|RemoteException e){
				e.printStackTrace();
			}
		}
	}
	
	private void conectaBroker() {
		
		try {
			conexao = ActiveMQConnection.makeConnection(url);
			conexao.start();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void desconectaBroker() {
		
		try {
			conexao.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNomeUsuario() {
		return this.nomeUsuario;
	}

	public String getNomeTopico() {
		return this.nomeTopico;
	}
}
