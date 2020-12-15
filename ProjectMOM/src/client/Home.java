package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Home {

	private JFrame frame, msg_frame;
	private Home home;
	private Client client;
	
	private JPanel resumoTopicos, resumoUsuario, jp_menu;
	private JScrollPane scrollLog, scrollUsuario, scrolljt_message, scrollTopico;
	private JTextArea textDestino;
	private JTextArea textUsuarios;
	private JTextArea textLog;
	private JTextArea textTopicos;
	
	private JLabel jl_users, jl_topics, jl_log, jl_name, jl_message;
	private JButton jb_subscribe, jb_listUsers, jb_listTopics, jb_newMessage;
	private JTextField jt_message;

	private JRadioButton radioUsuario;
	private JRadioButton radioTopico;
	private ButtonGroup radioGrupo = new ButtonGroup();

	public Home(String ip, int port, String nickname) {
		home = this;
		try {
			client = new Client(home, ip, port, nickname);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 805, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		
		initComponents();
		configClient();
		insertActions();
		createRunnable();
	}
	
    private void insertActions(){
    	jb_subscribe.addActionListener(event -> {
    		String nome = addTopic();
			if(nome!=null) {
				client.recebeMensagem(nome, false);
			}
        });
    	
    	jb_newMessage.addActionListener(event -> {
    		msg_frame = new JFrame();
    		msg_frame.setBounds(100, 100, 500, 150);
    		msg_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		msg_frame.getContentPane().setLayout(null);
    		msg_frame.setResizable(false);
    		
    		this.initMsg_frame();
    		this.insertActionsMsg_frame();
    		
    		msg_frame.setVisible(true);
    });
    	
    	jb_listUsers.addActionListener(event -> {
    		ArrayList<String> listaUsuarios = client.getClients();
			setMensagemLog("Lista de usuarios:");
			setMensagemLog("-> para online, - para offline");
			for(int i=0;i<listaUsuarios.size();i++) {
				setMensagemLog(listaUsuarios.get(i));
			}
        });
    	
    	jb_listTopics.addActionListener(event -> {
    		ArrayList<String> listaTopicos = client.getTopics();
			setMensagemLog("Lista de topicos:");
			for(int i=0;i<listaTopicos.size();i++) {
				setMensagemLog(listaTopicos.get(i));
			}
        });
    	
    }
	
	public void recebeMensagensUsuarios() {
		ArrayList<String> listaMensagens = client.recebeMensagem(client.nickname, true);
		while(!listaMensagens.isEmpty()) {
			textUsuarios.append("Recebido de "+listaMensagens.remove(0)+"\n");
		}
	}
	
	public void escreveMensagensTopico(String mensagem) {
		textTopicos.append(mensagem+"\n");
	}
	
	public void setMensagemLog(String mensagem) {
		textLog.append(mensagem+"\n");
		textLog.setCaretPosition(textLog.getText().length());
	}
	
	private void initMsg_frame() {
		radioUsuario = new JRadioButton("Usuario");
		radioUsuario.setSelected(true);
		radioUsuario.setBounds(10, 10, 90, 25);
		radioGrupo.add(radioUsuario);
		msg_frame.getContentPane().add(radioUsuario);
		
		radioTopico = new JRadioButton("Topico");
		radioTopico.setBounds(100, 10, 80, 25);
		radioGrupo.add(radioTopico);
		msg_frame.getContentPane().add(radioTopico);
		
		jl_name = new JLabel("Destino:");
		jl_name.setBounds(190, 15, 130, 15);
		msg_frame.getContentPane().add(jl_name);
		
		textDestino = new JTextArea();
		textDestino.setToolTipText("");
		textDestino.setBounds(260, 15, 220, 20);
		msg_frame.getContentPane().add(textDestino);
		
		jl_message = new JLabel("Digite sua mensagem:");
		jl_message.setBounds(10, 60, 200, 15);
		msg_frame.getContentPane().add(jl_message);
		
		scrolljt_message = new JScrollPane();
		scrolljt_message.setBounds(10, 80, 475, 30);
		msg_frame.getContentPane().add(scrolljt_message);
		
		jt_message = new JTextField();
		scrolljt_message.setViewportView(jt_message);
		jt_message.setColumns(10);
		
	}
	
	private void insertActionsMsg_frame(){
		jt_message.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String mensagem = arg0.getActionCommand();
				if(radioUsuario.isSelected()) {
					if(client.enviaMensagem(textDestino.getText(), client.nickname+": "+mensagem, true)) {
						textUsuarios.append("Você para "+textDestino.getText()+": "+mensagem+"\n");
					}
				}
				else {
					if(client.enviaMensagem(textDestino.getText(), textDestino.getText()+"<"+client.nickname+": "+mensagem, false)){
						textTopicos.append("Você para "+textDestino.getText()+": "+mensagem+"\n");
					}
				}
				jt_message.setText("");
			}
		});
	}
	
	private void initComponents() {
		
		resumoUsuario = new JPanel();
		resumoUsuario.setBounds(0, 0, 259, 435);
		resumoUsuario.setLayout(null);
		frame.getContentPane().add(resumoUsuario);
		
		resumoTopicos = new JPanel();
		resumoTopicos.setLayout(null);
		resumoTopicos.setBounds(267, 0, 259, 435);
		frame.getContentPane().add(resumoTopicos);
		
		jl_log = new JLabel("Log");
		jl_log.setBounds(644, 5, 42, 15);
		frame.getContentPane().add(jl_log);
		
		scrollLog = new JScrollPane();
		scrollLog.setBounds(534, 35, 255, 390);
		frame.getContentPane().add(scrollLog);
		
		jp_menu = new JPanel();
		jp_menu.setBounds(10, 450, 900, 100);
		jp_menu.setLayout(null);
		frame.getContentPane().add(jp_menu);		
		
		textLog = new JTextArea();
		textLog.setEditable(false);
		scrollLog.setViewportView(textLog);		
		
		jl_users = new JLabel("Mensagem Usuarios");
		jl_users.setBounds(55, 6, 152, 15);
		resumoUsuario.add(jl_users);
		
		jl_topics = new JLabel("Mensagem Topicos");
		jl_topics.setBounds(59, 6, 151, 15);
		resumoTopicos.add(jl_topics);
		
		scrollUsuario = new JScrollPane();
		scrollUsuario.setBounds(12, 35, 235, 390);
		resumoUsuario.add(scrollUsuario);
		
		textUsuarios = new JTextArea();
		textUsuarios.setEditable(false);
		scrollUsuario.setViewportView(textUsuarios);
		
		jb_subscribe = new JButton("Insvrecer-se no Topico");
		jb_subscribe.setBounds(0, 30, 385, 25);
		jp_menu.add(jb_subscribe);
		
		jb_newMessage = new JButton("Escrever Mensagem");
		jb_newMessage.setBounds(0, 0, 385, 25);
		jp_menu.add(jb_newMessage);
		
		scrollTopico = new JScrollPane();
		scrollTopico.setBounds(12, 35, 235, 390);
		resumoTopicos.add(scrollTopico);
		
		textTopicos = new JTextArea();
		textTopicos.setEditable(false);
		scrollTopico.setViewportView(textTopicos);
		
		jb_listUsers = new JButton("Usuarios Disponiveis");
		jb_listUsers.setBounds(390, 0, 385, 25);
		jp_menu.add(jb_listUsers);
		
		jb_listTopics = new JButton("Topicos Disponiveis");
		jb_listTopics.setBounds(390, 30, 385, 25);
		jp_menu.add(jb_listTopics);
		
		ArrayList<String> listaUsuarios = client.getClients();
		ArrayList<String> listaTopicos = client.getTopics();
		
		recebeMensagensUsuarios();
		
		setMensagemLog("Bem vindo(a) "+client.nickname);		
	}
	
	
	private void configClient() {
		while(!client.connect()) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {	
				e.printStackTrace();
			}
		}
	}
	
	public  String addTopic() {
		
		String nomeTopico = null;
		
		nomeTopico = JOptionPane.showInputDialog("Digite um nome para o Topico");
		
		while(nomeTopico!=null&&nomeTopico.contentEquals("")) {
			nomeTopico = JOptionPane.showInputDialog("Nome inválido, digite um nome para o Tópico");
		}
		
		return nomeTopico;
	}
	
	private void createRunnable() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					home.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}