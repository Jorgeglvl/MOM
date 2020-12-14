
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JRadioButton;

public class Home {

	//--------------------------------------------/-------------/--------------------------------------------//
	private JFrame frame;
	private Home window;
	private Usuario usuario;
	private ActionListener ato;
	//--------------------------------------------/-------------/--------------------------------------------//
	
	//--------------------------------------------/Paineis/Scroll/Text--------------------------------------------//
	private JPanel resumoTopicos;
	private JPanel resumoUsuario;
	private JScrollPane scrollLog;
	private JScrollPane scrollUsuario;
	private JScrollPane scrollChat;
	private JScrollPane scrollTopico;
	private JTextArea textDestino;
	private JTextArea textUsuarios;
	private JTextArea textLog;
	private JTextArea textTopicos;
	//--------------------------------------------/-------------/--------------------------------------------//
	
	//--------------------------------------------/Labels/Botoes/--------------------------------------------//
	private JLabel labelUsuarios;
	private JLabel labelTopicos;
	private JLabel labelLog;
	private JLabel lblNome;
	private JLabel lblDigiteAqui;
	private JButton assinarTopico ;
	private JButton botaoGetUsu;
	private JButton botaoGetTop;
	private JTextField chat;
	//--------------------------------------------/-------------/--------------------------------------------//
	private JRadioButton radioUsuario;
	private JRadioButton radioTopico;
	private ButtonGroup radioGrupo = new ButtonGroup();

	public Home(String ip, int port, String nickname) {
		window = this;
		try {
			usuario = new Usuario(window, ip, port, nickname);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 804, 575);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		iniciaPaineis();
		iniciaLabels();
		configuraUsuario();
		iniciaValores();
		createRunnable();
	}
	
	public void varreBotao() {
		
		ato = new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getSource() == assinarTopico) {
					String nome = Notificacao.addTopico();
					if(nome!=null) {
						usuario.recebeMensagem(nome, false);
					}
				}
				if(arg0.getSource() == botaoGetUsu) {
					ArrayList<String> listaUsuarios = usuario.getUsuarios();
					setMensagemLog("Lista de usuarios existentes");
					setMensagemLog("Legenda: * = online, - = offline");
					for(int i=0;i<listaUsuarios.size();i++) {
						setMensagemLog(listaUsuarios.get(i));
					}
				}
				if(arg0.getSource() == botaoGetTop) {
					ArrayList<String> listaTopicos = usuario.getTopicos();
					setMensagemLog("Lista de topicos existentes");
					setMensagemLog("Legenda: * = assinados, - = disponi");
					for(int i=0;i<listaTopicos.size();i++) {
						setMensagemLog(listaTopicos.get(i));
					}
				}
			}
		};
		
		assinarTopico.addActionListener(ato);
		botaoGetUsu.addActionListener(ato);
		botaoGetTop.addActionListener(ato);
		
		chat.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String mensagem = arg0.getActionCommand();
				if(radioUsuario.isSelected()) {
					if(usuario.enviaMensagem(textDestino.getText(), usuario.nome+": "+mensagem, true)) {
						textUsuarios.append("Você>"+textDestino.getText()+": "+mensagem+"\n");
					}
				}
				else {
					if(usuario.enviaMensagem(textDestino.getText(), textDestino.getText()+"<"+usuario.nome+": "+mensagem, false)){
						textTopicos.append("Você>"+textDestino.getText()+": "+mensagem+"\n");
					}
				}
				chat.setText("");
			}
		});
	}
	
	public void recebeMensagensUsuarios() {
		ArrayList<String> listaMensagens = usuario.recebeMensagem(usuario.nome, true);
		while(!listaMensagens.isEmpty()) {
			textUsuarios.append("Você<"+listaMensagens.remove(0)+"\n");
		}
	}
	
	public void escreveMensagensTopico(String mensagem) {
		textTopicos.append(mensagem+"\n");
	}
	
	public void setMensagemLog(String mensagem) {
		textLog.append(mensagem+"\n");
		textLog.setCaretPosition(textLog.getText().length());
	}
	
	private void iniciaPaineis() {
		
		resumoUsuario = new JPanel();
		resumoUsuario.setBounds(0, 0, 259, 435);
		resumoUsuario.setLayout(null);
		frame.getContentPane().add(resumoUsuario);
		
		resumoTopicos = new JPanel();
		resumoTopicos.setLayout(null);
		resumoTopicos.setBounds(267, 0, 259, 435);
		frame.getContentPane().add(resumoTopicos);
		
		labelLog = new JLabel("Log");
		labelLog.setBounds(644, 5, 42, 15);
		frame.getContentPane().add(labelLog);
		
		scrollLog = new JScrollPane();
		scrollLog.setBounds(534, 32, 254, 365);
		frame.getContentPane().add(scrollLog);
		
		textLog = new JTextArea();
		textLog.setEditable(false);
		scrollLog.setViewportView(textLog);
		
		scrollChat = new JScrollPane();
		scrollChat.setBounds(10, 510, 782, 30);
		frame.getContentPane().add(scrollChat);
		
		chat = new JTextField();
		scrollChat.setViewportView(chat);
		chat.setColumns(10);
		
		textDestino = new JTextArea();
		textDestino.setToolTipText("");
		textDestino.setBounds(331, 445, 457, 21);
		frame.getContentPane().add(textDestino);
		
	}
	
	private void iniciaLabels() {
		
		labelUsuarios = new JLabel("Mensagem Usuarios");
		labelUsuarios.setBounds(55, 6, 152, 15);
		resumoUsuario.add(labelUsuarios);
		
		labelTopicos = new JLabel("Mensagem Topicos");
		labelTopicos.setBounds(59, 6, 151, 15);
		resumoTopicos.add(labelTopicos);
		
		scrollUsuario = new JScrollPane();
		scrollUsuario.setBounds(12, 33, 235, 390);
		resumoUsuario.add(scrollUsuario);
		
		textUsuarios = new JTextArea();
		textUsuarios.setEditable(false);
		scrollUsuario.setViewportView(textUsuarios);
		
		assinarTopico = new JButton("Assinar Topico");
		assinarTopico.setBounds(12, 398, 235, 25);
		resumoTopicos.add(assinarTopico);
		
		scrollTopico = new JScrollPane();
		scrollTopico.setBounds(12, 33, 235, 363);
		resumoTopicos.add(scrollTopico);
		
		textTopicos = new JTextArea();
		textTopicos.setEditable(false);
		scrollTopico.setViewportView(textTopicos);
		
		radioUsuario = new JRadioButton("Usuario");
		radioUsuario.setSelected(true);
		radioUsuario.setBounds(10, 443, 88, 23);
		radioGrupo.add(radioUsuario);
		frame.getContentPane().add(radioUsuario);
		
		radioTopico = new JRadioButton("Topico");
		radioTopico.setBounds(102, 443, 81, 23);
		radioGrupo.add(radioTopico);
		frame.getContentPane().add(radioTopico);
		
		lblNome = new JLabel("Nome do destino");
		lblNome.setBounds(197, 447, 131, 15);
		frame.getContentPane().add(lblNome);
		
		lblDigiteAqui = new JLabel("Digite aqui sua mensagem");
		lblDigiteAqui.setBounds(10, 491, 201, 15);
		frame.getContentPane().add(lblDigiteAqui);
		
		botaoGetUsu = new JButton("Usuarios Disponiveis");
		botaoGetUsu.setBounds(534, 397, 254, 21);
		frame.getContentPane().add(botaoGetUsu);
		
		botaoGetTop = new JButton("Topicos Disponiveis");
		botaoGetTop.setBounds(534, 421, 254, 21);
		frame.getContentPane().add(botaoGetTop);
	}
	
	private void iniciaValores() {
		
		ArrayList<String> listaUsuarios = usuario.getUsuarios();
		ArrayList<String> listaTopicos = usuario.getTopicos();
		
		recebeMensagensUsuarios();
		
		setMensagemLog("Bem vindo(a) "+usuario.nome);
		setMensagemLog("Lista de usuarios existentes");
		setMensagemLog("Legenda: * = online, - = offline");
		for(int i=0;i<listaUsuarios.size();i++) {
			setMensagemLog(listaUsuarios.get(i));
		}
		
		setMensagemLog("Lista de topicos existentes");
		for(int i=0;i<listaTopicos.size();i++) {
			setMensagemLog(listaTopicos.get(i));
		}
	}
	
	private void configuraUsuario() {
		while(!usuario.solicitaConexao()) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
