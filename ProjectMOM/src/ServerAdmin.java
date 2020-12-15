import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerAdmin {

	private JFrame frame;
	private ServerAdmin window;
	private Server server;
	private ActionListener action;
	private JTextArea textLog;
	
	private JPanel resumoTopicos, resumoFilas, painelTopicos, painelFilas;
	private JScrollPane scrollPT, scrollPF, scrollLog;
	
	private ArrayList<JLabel> queueCells = new ArrayList<JLabel>();
	private ArrayList<JLabel> topicCells = new ArrayList<JLabel>();
	
	private JLabel labelFilas, labelTopicos, apagarFila, apagarTopico;
	private JLabel nomeFila, nomeTopico, qntMensagensFila, labelLog;
	
	private ArrayList<JButton> jb_deleteQueue = new ArrayList<JButton>();
	private ArrayList<JButton> jb_deleteTopic = new ArrayList<JButton>();


	public ServerAdmin(String ip, int port) {
		window = this;
		try {
			server = new Server(window, ip, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 810, 470);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		iniciaPaineis();
		iniciaLabels();
		iniciaValores();
		createRunnable();
	}
	
	public void atualizaInterface() {
		
		labelLog.setText(""+queueCells.size());
		labelLog.setText(""+topicCells.size());
		labelLog.setText("Log");
	}
	
	public void varreBotao() {
		
		action = new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				for(int i=0;i<jb_deleteQueue.size();i++) {
					if(arg0.getSource() == jb_deleteQueue.get(i)) {
						if(confirmaApagarFila()==0) {
							server.removeFila(queueCells.get(i*2).getText());
							setMensagemLog("Usuario '"+queueCells.get(i*2).getText()+"' Deletado");
							removeBotaoFila(i);
							removeLabelFila(i);
							removeLabelFila(i);
						}
					}
				}
				for(int i=0;i<jb_deleteTopic.size();i++) {
					if(arg0.getSource() == jb_deleteTopic.get(i)) {
						if(confirmaApagarTopico()==0) {
							try {
								server.produzMensagemTopico(topicCells.get(i).getText(), topicCells.get(i).getText()+"<Servidor: <fechado>");
							} catch (RemoteException e) {
								e.printStackTrace();
							}
							server.removeTopico(topicCells.get(i).getText());
							setMensagemLog("Topico '"+topicCells.get(i).getText()+"' Deletado");
							removeBotaoTopico(i);
							removeLabelTopico(i);	
						}
					}
				}
				atualizaInterface();
			}
		};
		
		int i;
		
		for(i=0;i<jb_deleteQueue.size();i++) {
			jb_deleteQueue.get(i).addActionListener(action);
		}
		
		setMensagemLog("Existem "+i+" filas criadas");
		
		for(i=0;i<jb_deleteTopic.size();i++) {
			jb_deleteTopic.get(i).addActionListener(action);
		}
		
		setMensagemLog("Existem "+i+" topicos disponiveis");
	}
	
	public void criarBotaoFila() {
		jb_deleteQueue.add(new JButton("X"));
		painelFilas.add(jb_deleteQueue.get(jb_deleteQueue.size()-1));
	}
	
	public void criarBotaoTopico() {
		jb_deleteTopic.add(new JButton("X"));
		painelTopicos.add(jb_deleteTopic.get(jb_deleteTopic.size()-1));
	}
	
	public void iniciaBotaoFila() {
		jb_deleteQueue.get(jb_deleteQueue.size()-1).addActionListener(action);
	}
	
	public void iniciaBotaoTopico() {
		jb_deleteTopic.get(jb_deleteTopic.size()-1).addActionListener(action);
	}
	
	public void removeBotaoFila(int i) {
		jb_deleteQueue.remove(i);
		painelFilas.remove(i*3);
	}
	
	public void removeBotaoTopico(int i) {
		jb_deleteTopic.remove(i);
		painelTopicos.remove(i*2);
	}
	
	public void criarLabelFila(String valor) {
		queueCells.add(new JLabel(""+valor));
		painelFilas.add(queueCells.get(queueCells.size()-1));
	}
	
	public void criarLabelTopico(String valor) {
		topicCells.add(new JLabel(""+valor));
		painelTopicos.add(topicCells.get(topicCells.size()-1));
	}

	public void removeLabelFila(int i) {
		queueCells.remove(i*2);
		painelFilas.remove(i*3);
	}
	
	public void removeLabelTopico(int i) {
		topicCells.remove(i);
		painelTopicos.remove(i*2);
	}
	
	public int confirmaApagarFila() {
		return JOptionPane.showConfirmDialog(null, "Tem certeza?","Apagar Fila",JOptionPane.YES_NO_OPTION);
	}
	
	public int confirmaApagarTopico() {
		return JOptionPane.showConfirmDialog(null, "Tem certeza?","Apagar Topico",JOptionPane.YES_NO_OPTION);
	}
	
	public void incrementaQntMensagem(String nomeFila) {
		int i = 0;
		while(i<queueCells.size()) {
			if(queueCells.get(i).getText().contentEquals(nomeFila)) {
				String valor = queueCells.get(i+1).getText();
				queueCells.get(i+1).setText(""+(Integer.parseInt(valor)+1));
				setMensagemLog("Mensagem para '"+nomeFila+"' adicionada");
			}
			i+=2;
		}
	}
	
	public void adicionaListaFila(String nome) {
		server.criaFila(nome);
		criarBotaoFila();
		criarLabelFila(nome);
		criarLabelFila("0");
		iniciaBotaoFila();
		setMensagemLog("Usuário '"+nome+"' Criado");
	}
	
	public void adicionaListaTopico(String nome) {
		server.criaTopico(nome);
		criarBotaoTopico();
		criarLabelTopico(nome);
		iniciaBotaoTopico();
		setMensagemLog("Topico '"+nome+"' Criado");
	}
	
	public void setMensagemLog(String mensagem) {
		textLog.append(mensagem+"\n");
		textLog.setCaretPosition(textLog.getText().length());
	}
	
	public void preenchePainelFila(ArrayList<String> listaFilas, ArrayList<Integer> listaQntMensagens) {
		for(int i=0;i<listaFilas.size();i++) {
			criarBotaoFila();
			criarLabelFila(listaFilas.get(i));
			criarLabelFila(""+listaQntMensagens.get(i));
		}
	}
	
	public void preenchePainelTopico(ArrayList<String> listaTopicos) {
		for(int i=0;i<listaTopicos.size();i++) {
			criarBotaoTopico();
			criarLabelTopico(listaTopicos.get(i));
		}
	}
	
	private void iniciaPaineis() {
		
		resumoFilas = new JPanel();
		resumoFilas.setBounds(0, 0, 260, 435);
		resumoFilas.setLayout(null);
		frame.getContentPane().add(resumoFilas);
		
		resumoTopicos = new JPanel();
		resumoTopicos.setLayout(null);
		resumoTopicos.setBounds(267, 0, 260, 435);
		frame.getContentPane().add(resumoTopicos);
		
		scrollPF = new JScrollPane();
		scrollPF.setBounds(15, 50, 235, 340);
		resumoFilas.add(scrollPF);
		
		scrollPT = new JScrollPane();
		scrollPT.setBounds(15, 50, 235, 340);
		resumoTopicos.add(scrollPT);
		
		painelFilas = new JPanel();
		scrollPF.setViewportView(painelFilas);
		painelFilas.setLayout(new GridLayout(0, 3, 10, 10));
		
		painelTopicos = new JPanel();
		scrollPT.setViewportView(painelTopicos);
		painelTopicos.setLayout(new GridLayout(0, 3, 10, 10));
		
		labelLog = new JLabel("Log");
		labelLog.setBounds(645, 5, 40, 15);
		frame.getContentPane().add(labelLog);
		
		scrollLog = new JScrollPane();
		scrollLog.setBounds(535, 30, 255, 390);
		frame.getContentPane().add(scrollLog);
		
		textLog = new JTextArea();
		textLog.setEditable(false);
		scrollLog.setViewportView(textLog);
	}
	
	private void iniciaLabels() {
		
		labelFilas = new JLabel("Filas");
		labelFilas.setBounds(102, 6, 44, 15);
		resumoFilas.add(labelFilas);
		
		labelTopicos = new JLabel("Topicos");
		labelTopicos.setBounds(93, 6, 63, 15);
		resumoTopicos.add(labelTopicos);
		
		nomeFila = new JLabel("Nome");
		nomeFila.setBounds(93, 33, 53, 15);
		resumoFilas.add(nomeFila);
		
		nomeTopico = new JLabel("Nome");
		nomeTopico.setBounds(141, 33, 53, 15);
		resumoTopicos.add(nomeTopico);
		
		qntMensagensFila = new JLabel("Qnt msg");
		qntMensagensFila.setBounds(158, 33, 74, 15);
		resumoFilas.add(qntMensagensFila);
		
		apagarFila = new JLabel("Remover");
		apagarFila.setBounds(11, 33, 70, 15);
		resumoFilas.add(apagarFila);
		
		apagarTopico = new JLabel("Remover");
		apagarTopico.setBounds(12, 33, 70, 15);
		resumoTopicos.add(apagarTopico);
	}
	
	public void iniciaValores() {
		
		ArrayList<String> listaFilas = server.getFilas();
		ArrayList<Integer> listaQntMensagens = new ArrayList<Integer>();
		for(int i=0;i<listaFilas.size();i++) {
			listaQntMensagens.add(server.getQuantidadeMsg(listaFilas.get(i)));
		}
		ArrayList<String> listaTopicos = server.getTopicos();
		
		queueCells.clear();
		topicCells.clear();
		jb_deleteQueue.clear();
		jb_deleteTopic.clear();
		painelTopicos.removeAll();
		painelFilas.removeAll();
		
		preenchePainelFila(listaFilas, listaQntMensagens);
		preenchePainelTopico(listaTopicos);
		atualizaInterface();
	}
	
	private void createRunnable() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window.frame.setVisible(true);
					window.varreBotao();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}