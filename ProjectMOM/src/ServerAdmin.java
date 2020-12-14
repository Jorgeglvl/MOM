import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JTextArea;

public class ServerAdmin {

	//--------------------------------------------/-------------/--------------------------------------------//
	private JFrame frame;
	private ServerAdmin window;
	private ServidorMOM server;
	private ActionListener ato;
	private JTextArea textLog;
	//--------------------------------------------/-------------/--------------------------------------------//
	
	//--------------------------------------------/Paineis/Scroll/--------------------------------------------//
	private JPanel resumoTopicos;
	private JPanel resumoFilas;
	private JPanel painelTopicos;
	private JPanel painelFilas;
	private JScrollPane scrollPT;
	private JScrollPane scrollPF;
	private JScrollPane scrollLog;
	private ArrayList<JLabel> celulasFila = new ArrayList<JLabel>();
	private ArrayList<JLabel> celulasTopico = new ArrayList<JLabel>();
	//--------------------------------------------/-------------/--------------------------------------------//
	
	//--------------------------------------------/Labels/Botoes/--------------------------------------------//
	private JLabel labelFilas;
	private JLabel labelTopicos;
	private JLabel apagarFila;
	private JLabel apagarTopico;
	private JLabel nomeFila;
	private JLabel nomeTopico;
	private JLabel qntMensagensFila;	
	private JLabel labelLog;
	private JButton addUsuario;
	private JButton addTopico ;
	private ArrayList<JButton> xButtonFila = new ArrayList<JButton>();
	private ArrayList<JButton> xButtonTopico = new ArrayList<JButton>();
	//--------------------------------------------/-------------/--------------------------------------------//
	
	
	public static void main(String[] args) {
		
		new ServerAdmin();
	}

	public ServerAdmin() {
		window = this;
		try {
			server = new ServidorMOM(window);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 460);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		iniciaPaineis();
		iniciaLabels();
		iniciaValores();
		createRunnable();
		setMensagemLog("Bem vindo ao serverMOM");
	}
	
	public void atualizaInterface() {
		
		labelLog.setText(""+celulasFila.size());
		labelLog.setText(""+celulasTopico.size());
		labelLog.setText("Log");
	}
	
	public void varreBotao() {
		
		ato = new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getSource() == addUsuario) {
					//Retirado
				}
				if(arg0.getSource() == addTopico) {
					String nome = Notificacao.addTopico();
					if(nome!=null) {
						if(!server.verificaTopicoExiste(nome)) {
							adicionaListaTopico(nome);
						}
						else {
							Notificacao.topicoExiste(nome);
							setMensagemLog("Erro: Topico Duplicado");
						}
					}
				}
				for(int i=0;i<xButtonFila.size();i++) {
					if(arg0.getSource() == xButtonFila.get(i)) {
						if(Notificacao.confirmaApagarFila()==0) {
							server.removeFila(celulasFila.get(i*2).getText());
							setMensagemLog("Usuario '"+celulasFila.get(i*2).getText()+"' Deletado");
							removeBotaoFila(i);
							removeLabelFila(i);
							removeLabelFila(i);
						}
					}
				}
				for(int i=0;i<xButtonTopico.size();i++) {
					if(arg0.getSource() == xButtonTopico.get(i)) {
						if(Notificacao.confirmaApagarTopico()==0) {
							try {
								server.produzMensagemTopico(celulasTopico.get(i).getText(), celulasTopico.get(i).getText()+"<Servidor: <fechado>");
							} catch (RemoteException e) {
								e.printStackTrace();
							}
							server.removeTopico(celulasTopico.get(i).getText());
							setMensagemLog("Topico '"+celulasTopico.get(i).getText()+"' Deletado");
							removeBotaoTopico(i);
							removeLabelTopico(i);	
						}
					}
				}
				atualizaInterface();
			}
		};
		
		addUsuario.addActionListener(ato);
		addTopico.addActionListener(ato);
		
		int i;
		
		for(i=0;i<xButtonFila.size();i++) {
			xButtonFila.get(i).addActionListener(ato);
		}
		
		setMensagemLog("Há "+i+" filas criadas");
		
		for(i=0;i<xButtonTopico.size();i++) {
			xButtonTopico.get(i).addActionListener(ato);
		}
		
		setMensagemLog("e "+i+" topicos disponiveis");
	}
	
	public void criarBotaoFila() {
		xButtonFila.add(new JButton("X"));
		painelFilas.add(xButtonFila.get(xButtonFila.size()-1));
	}
	
	public void criarBotaoTopico() {
		xButtonTopico.add(new JButton("X"));
		painelTopicos.add(xButtonTopico.get(xButtonTopico.size()-1));
	}
	
	public void iniciaBotaoFila() {
		xButtonFila.get(xButtonFila.size()-1).addActionListener(ato);
	}
	
	public void iniciaBotaoTopico() {
		xButtonTopico.get(xButtonTopico.size()-1).addActionListener(ato);
	}
	
	public void removeBotaoFila(int i) {
		xButtonFila.remove(i);
		painelFilas.remove(i*3);
	}
	
	public void removeBotaoTopico(int i) {
		xButtonTopico.remove(i);
		painelTopicos.remove(i*2);
	}
	
	public void criarLabelFila(String valor) {
		celulasFila.add(new JLabel(""+valor));
		painelFilas.add(celulasFila.get(celulasFila.size()-1));
	}
	
	public void criarLabelTopico(String valor) {
		celulasTopico.add(new JLabel(""+valor));
		painelTopicos.add(celulasTopico.get(celulasTopico.size()-1));
	}

	public void removeLabelFila(int i) {
		celulasFila.remove(i*2);
		painelFilas.remove(i*3);
	}
	
	public void removeLabelTopico(int i) {
		celulasTopico.remove(i);
		painelTopicos.remove(i*2);
	}
	
	public void incrementaQntMensagem(String nomeFila) {
		int i = 0;
		while(i<celulasFila.size()) {
			if(celulasFila.get(i).getText().contentEquals(nomeFila)) {
				String valor = celulasFila.get(i+1).getText();
				celulasFila.get(i+1).setText(""+(Integer.parseInt(valor)+1));
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
		resumoFilas.setBounds(0, 0, 259, 435);
		resumoFilas.setLayout(null);
		frame.getContentPane().add(resumoFilas);
		
		resumoTopicos = new JPanel();
		resumoTopicos.setLayout(null);
		resumoTopicos.setBounds(267, 0, 259, 435);
		frame.getContentPane().add(resumoTopicos);
		
		scrollPF = new JScrollPane();
		scrollPF.setBounds(12, 52, 235, 340);
		resumoFilas.add(scrollPF);
		
		scrollPT = new JScrollPane();
		scrollPT.setBounds(12, 52, 235, 340);
		resumoTopicos.add(scrollPT);
		
		painelFilas = new JPanel();
		scrollPF.setViewportView(painelFilas);
		painelFilas.setLayout(new GridLayout(0, 3, 10, 10));
		
		painelTopicos = new JPanel();
		scrollPT.setViewportView(painelTopicos);
		painelTopicos.setLayout(new GridLayout(0, 2, 10, 10));
		
		labelLog = new JLabel("Log");
		labelLog.setBounds(645, 5, 42, 15);
		frame.getContentPane().add(labelLog);
		
		scrollLog = new JScrollPane();
		scrollLog.setBounds(534, 32, 254, 391);
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
		
		addUsuario = new JButton("About");
		addUsuario.setBounds(12, 398, 235, 25);
		resumoFilas.add(addUsuario);
		
		apagarFila = new JLabel("Remover");
		apagarFila.setBounds(11, 33, 70, 15);
		resumoFilas.add(apagarFila);
		
		addTopico = new JButton("Novo Topico");
		addTopico.setBounds(12, 398, 235, 25);
		resumoTopicos.add(addTopico);
		
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
		
		celulasFila.clear();
		celulasTopico.clear();
		xButtonFila.clear();
		xButtonTopico.clear();
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
